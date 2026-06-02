// Parallel Segmented Downloader for Apex Download Manager

export class SegmentedDownloader {
  constructor(url, filename, segmentsCount = 8) {
    this.url = url;
    this.filename = filename || 'download';
    this.segmentsCount = segmentsCount;
    this.id = 'dl_engine_' + Date.now();
    
    // Status states: 'idle', 'downloading', 'paused', 'merging', 'completed', 'failed'
    this.status = 'idle';
    this.totalBytes = 0;
    this.downloadedBytes = 0;
    this.segments = [];
    this.activeFetches = [];
    this.speed = 0; // Bytes per second
    
    // Speed tracking
    this.lastTime = Date.now();
    this.lastBytes = 0;
    
    // Callbacks
    this.onProgress = null;
    this.onComplete = null;
    this.onError = null;
    this.onStatusChange = null;

    this.rangeSupported = false;
    this.abortControllers = [];
  }

  // Update downloader status
  setStatus(status) {
    this.status = status;
    if (this.onStatusChange) {
      this.onStatusChange(status);
    }
  }

  // Start the download process
  async start() {
    if (this.status === 'downloading') return;
    
    this.setStatus('downloading');
    this.lastTime = Date.now();
    this.lastBytes = 0;

    try {
      // 1. Send HEAD request to probe file size and Range support (sending credentials/cookies)
      const headResponse = await fetch(this.url, { 
        method: 'HEAD',
        credentials: 'include'
      }).catch(() => null);
      
      let acceptRanges = false;
      let contentLength = 0;
      let contentType = '';

      if (headResponse && headResponse.ok) {
        contentLength = parseInt(headResponse.headers.get('content-length') || '0', 10);
        contentType = (headResponse.headers.get('content-type') || '').toLowerCase();
        acceptRanges = headResponse.headers.get('accept-ranges') === 'bytes' || 
                       headResponse.headers.has('content-range');
      } else {
        // Fallback: Try a GET request with Range 0-0 to check if it supports ranges (sending credentials/cookies)
        const getTest = await fetch(this.url, {
          headers: { 'Range': 'bytes=0-0' },
          credentials: 'include'
        }).catch(() => null);

        if (getTest && getTest.ok) {
          contentType = (getTest.headers.get('content-type') || '').toLowerCase();
          if (getTest.status === 206 || getTest.headers.has('content-range')) {
            acceptRanges = true;
            const rangeInfo = getTest.headers.get('content-range');
            if (rangeInfo) {
              const match = rangeInfo.match(/\/(\d+)/);
              if (match) contentLength = parseInt(match[1], 10);
            }
          }
        }
      }

      // If redirected to login or HTML page, abort parallel download and trigger native download
      if (contentType.includes('text/html')) {
        throw new Error('Webpage redirection detected. Falling back to native browser downloader to preserve session credentials.');
      }

      this.totalBytes = contentLength;
      this.rangeSupported = acceptRanges && contentLength > 0;

      if (!this.rangeSupported) {
        // Fallback: Single connection download
        this.segmentsCount = 1;
        this.segments = [{
          id: 0,
          start: 0,
          end: null,
          downloaded: 0,
          total: this.totalBytes || null,
          done: false
        }];
      } else {
        // Create segments
        const segmentSize = Math.ceil(contentLength / this.segmentsCount);
        this.segments = [];
        for (let i = 0; i < this.segmentsCount; i++) {
          const start = i * segmentSize;
          const end = Math.min((i + 1) * segmentSize - 1, contentLength - 1);
          this.segments.push({
            id: i,
            start: start,
            end: end,
            downloaded: 0,
            total: end - start + 1,
            done: false
          });
        }
      }

      // Initialize OPFS cleanup for any old parts
      await this.cleanupTempFiles();

      // Launch segment downloads
      const downloadPromises = this.segments.map(seg => this.downloadSegment(seg));
      
      // Periodically update speed meter
      this.speedInterval = setInterval(() => this.calculateSpeed(), 1000);

      await Promise.all(downloadPromises);

      // Verify all segments are downloaded successfully
      const allDone = this.segments.every(s => s.done);
      if (!allDone) {
        throw new Error('Some segments failed to download.');
      }

      // Merge segments
      clearInterval(this.speedInterval);
      this.setStatus('merging');
      const fileBlob = await this.mergeSegments();
      
      this.setStatus('completed');
      if (this.onComplete) {
        this.onComplete(fileBlob);
      }

      // Post-download cleanup
      await this.cleanupTempFiles();

    } catch (err) {
      clearInterval(this.speedInterval);
      this.setStatus('failed');
      if (this.onError) {
        this.onError(err);
      }
    }
  }

  // Download a single byte range segment
  async downloadSegment(segment) {
    const controller = new AbortController();
    this.abortControllers.push(controller);

    const headers = {};
    if (this.rangeSupported) {
      headers['Range'] = `bytes=${segment.start + segment.downloaded}-${segment.end}`;
    }

    try {
      const response = await fetch(this.url, {
        headers,
        signal: controller.signal,
        credentials: 'include'
      });

      if (!response.ok && response.status !== 206) {
        throw new Error(`Server returned HTTP ${response.status}`);
      }

      // Setup OPFS Writer for this segment part
      const root = await navigator.storage.getDirectory();
      const filename = `temp_${this.id}_part_${segment.id}`;
      const fileHandle = await root.getFileHandle(filename, { create: true });
      const writable = await fileHandle.createWritable({ keepExistingData: segment.downloaded > 0 });
      
      // If resuming, seek to the end of currently written bytes
      if (segment.downloaded > 0) {
        await writable.seek(segment.downloaded);
      }

      const reader = response.body.getReader();
      
      while (this.status === 'downloading') {
        const { done, value } = await reader.read();
        
        if (done) break;

        await writable.write(value);
        segment.downloaded += value.length;
        this.downloadedBytes += value.length;

        // Callback progress
        if (this.onProgress) {
          this.onProgress({
            progress: this.totalBytes ? Math.min(100, (this.downloadedBytes / this.totalBytes) * 100) : 0,
            speed: this.speed,
            downloadedBytes: this.downloadedBytes,
            totalBytes: this.totalBytes,
            segments: this.segments
          });
        }
      }

      await writable.close();
      segment.done = true;

    } catch (err) {
      if (err.name === 'AbortError') {
        // User paused the download
        return;
      }
      segment.error = true;
      throw err;
    }
  }

  // Calculate download speed
  calculateSpeed() {
    const now = Date.now();
    const timeDelta = (now - this.lastTime) / 1000; // in seconds
    const bytesDelta = this.downloadedBytes - this.lastBytes;

    if (timeDelta > 0) {
      this.speed = Math.round(bytesDelta / timeDelta);
    }

    this.lastTime = now;
    this.lastBytes = this.downloadedBytes;
  }

  // Pause downloading
  pause() {
    if (this.status !== 'downloading') return;
    this.setStatus('paused');
    
    clearInterval(this.speedInterval);
    this.abortControllers.forEach(c => c.abort());
    this.abortControllers = [];
  }

  // Resume downloading
  async resume() {
    if (this.status !== 'paused' && this.status !== 'failed') return;
    this.setStatus('downloading');

    this.lastTime = Date.now();
    this.lastBytes = this.downloadedBytes;

    try {
      this.speedInterval = setInterval(() => this.calculateSpeed(), 1000);

      // Re-download incomplete segments from where they stopped
      const resumePromises = this.segments.map(seg => {
        if (seg.done) return Promise.resolve();
        seg.error = false;
        return this.downloadSegment(seg);
      });

      await Promise.all(resumePromises);

      const allDone = this.segments.every(s => s.done);
      if (!allDone) {
        throw new Error('Some segments failed to resume.');
      }

      clearInterval(this.speedInterval);
      this.setStatus('merging');
      const fileBlob = await this.mergeSegments();
      
      this.setStatus('completed');
      if (this.onComplete) {
        this.onComplete(fileBlob);
      }

      await this.cleanupTempFiles();

    } catch (err) {
      clearInterval(this.speedInterval);
      this.setStatus('failed');
      if (this.onError) {
        this.onError(err);
      }
    }
  }

  // Assemble the parallel chunks into a single unified blob
  async mergeSegments() {
    const root = await navigator.storage.getDirectory();
    const blobs = [];

    for (let i = 0; i < this.segmentsCount; i++) {
      const partName = `temp_${this.id}_part_${i}`;
      const fileHandle = await root.getFileHandle(partName);
      const file = await fileHandle.getFile();
      blobs.push(file);
    }

    // Combine all segments into one file blob
    // This is safe because browser reads them as reference, won't load entire array into RAM immediately
    return new Blob(blobs, { type: 'application/octet-stream' });
  }

  // Clean up the temporary segment files in OPFS
  async cleanupTempFiles() {
    try {
      const root = await navigator.storage.getDirectory();
      for (let i = 0; i < this.segmentsCount; i++) {
        const partName = `temp_${this.id}_part_${i}`;
        await root.removeEntry(partName).catch(() => {});
      }
    } catch (e) {
      console.warn("Clean up failed:", e);
    }
  }
}
