// Standalone download window controller for Apex Download Manager
import { SegmentedDownloader } from './downloader.js';

let downloadId = null;
let downloadItem = null;
let downloader = null;
let queue = [];
let lastStorageWriteTime = 0;

// DOM Elements
const txtFilename = document.getElementById('txt-filename');
const fileExtBadge = document.getElementById('file-ext-badge');
const txtStatusBadge = document.getElementById('txt-status-badge');
const txtUrl = document.getElementById('txt-url');
const txtSize = document.getElementById('txt-size');
const txtDownloaded = document.getElementById('txt-downloaded');
const txtSpeed = document.getElementById('txt-speed');
const txtTimeLeft = document.getElementById('txt-time-left');

const progressBarFill = document.getElementById('progress-bar-fill');
const txtProgressPercent = document.getElementById('txt-progress-percent');
const segmentsGrid = document.getElementById('segments-grid');

const btnOpenFile = document.getElementById('btn-open-file');
const btnOpenFolder = document.getElementById('btn-open-folder');
const btnPauseResume = document.getElementById('btn-pause-resume');
const btnCancel = document.getElementById('btn-cancel');

document.addEventListener('DOMContentLoaded', async () => {
  const urlParams = new URLSearchParams(window.location.search);
  downloadId = urlParams.get('id');

  if (!downloadId) {
    alert('No download task specified.');
    window.close();
    return;
  }

  await loadDownloadItem();
  setupEventListeners();
  
  if (downloadItem.status === 'queued' || downloadItem.status === 'downloading' || downloadItem.status === 'idle') {
    startDownloadProcess();
  } else {
    updateUIWithPausedState();
  }
});

// Load task details from storage
async function loadDownloadItem() {
  const data = await chrome.storage.local.get(['downloads_queue']);
  queue = data.downloads_queue || [];
  downloadItem = queue.find(q => q.id === downloadId);

  if (!downloadItem) {
    alert('Download task not found.');
    window.close();
    return;
  }

  // Set file headers and detail values
  txtFilename.textContent = downloadItem.filename;
  fileExtBadge.textContent = downloadItem.extension.toUpperCase();
  
  // Set badge color category
  fileExtBadge.className = `file-ext-badge badge-${downloadItem.category}`;
  
  txtUrl.textContent = downloadItem.url;
  txtUrl.title = downloadItem.url;
  txtSize.textContent = formatBytes(downloadItem.size);
  txtDownloaded.textContent = formatBytes(downloadItem.downloadedBytes || 0);
  
  updateProgressUI(downloadItem.progress || 0);
}

// Save queue to local storage
async function saveQueue() {
  await chrome.storage.local.set({ downloads_queue: queue });
}

// Throttle storage writes to avoid performance lag
function throttleSaveQueue() {
  const now = Date.now();
  if (now - lastStorageWriteTime > 2000) {
    saveQueue();
    lastStorageWriteTime = now;
  }
}

// Setup events
function setupEventListeners() {
  // Pause / Resume Toggle
  btnPauseResume.addEventListener('click', () => {
    if (downloader) {
      if (downloader.status === 'downloading') {
        downloader.pause();
      } else if (downloader.status === 'paused' || downloader.status === 'failed') {
        downloader.resume();
      }
    } else {
      // Recreate engine if destroyed
      startDownloadProcess();
    }
  });

  // Cancel Download
  btnCancel.addEventListener('click', async () => {
    if (downloader) {
      downloader.pause();
    }
    
    if (confirm('Cancel and remove this download task?')) {
      // Cleanup temporary files
      const tempEngine = new SegmentedDownloader(downloadItem.url, downloadItem.filename, downloadItem.segmentsCount);
      tempEngine.id = downloadId.replace('dl_', 'dl_engine_');
      await tempEngine.cleanupTempFiles();

      // Remove from queue
      queue = queue.filter(q => q.id !== downloadId);
      await saveQueue();

      // Notify dashboard
      chrome.runtime.sendMessage({
        action: 'DOWNLOAD_PROGRESS',
        downloadId: downloadId,
        status: 'deleted'
      }).catch(() => {});

      window.close();
    } else {
      if (downloader && downloader.status === 'paused') {
        downloader.resume();
      }
    }
  });

  // Open completed file on desktop
  btnOpenFile.addEventListener('click', () => {
    if (downloadItem.chromeDownloadId) {
      chrome.downloads.open(downloadItem.chromeDownloadId);
    }
  });

  // Open download containing folder on desktop
  btnOpenFolder.addEventListener('click', () => {
    if (downloadItem.chromeDownloadId) {
      chrome.downloads.show(downloadItem.chromeDownloadId);
    }
  });

  // Pause if user closes popup window mid-download
  window.addEventListener('beforeunload', () => {
    if (downloader && downloader.status === 'downloading') {
      downloader.pause();
      downloadItem.status = 'paused';
      downloadItem.speed = 0;
      saveQueue();
    }
  });
}

// Launch parallel segmented downloader engine or fallback standard engine
function startDownloadProcess() {
  if (downloadItem.method === 'standard') {
    if (downloadItem.chromeDownloadId) {
      chrome.downloads.resume(downloadItem.chromeDownloadId, () => {
        if (chrome.runtime.lastError) {
          restartNativeDownload();
        } else {
          startNativeTrackingLoop(downloadItem.chromeDownloadId);
        }
      });
    } else {
      restartNativeDownload();
    }
    return;
  }

  downloader = new SegmentedDownloader(downloadItem.url, downloadItem.filename, downloadItem.segmentsCount);
  // sync engine unique temporary path id
  downloader.id = downloadId.replace('dl_', 'dl_engine_');

  downloader.onStatusChange = (status) => {
    downloadItem.status = status;
    updateStatusUI(status);
    
    if (status === 'completed' || status === 'failed' || status === 'paused') {
      downloadItem.speed = 0;
      saveQueue(); // Save immediately on status change
    }

    // Broadcast update to dashboard
    broadcastProgress();
  };

  downloader.onProgress = (progressData) => {
    downloadItem.progress = progressData.progress;
    downloadItem.speed = progressData.speed;
    downloadItem.downloadedBytes = progressData.downloadedBytes;
    downloadItem.size = progressData.totalBytes || downloadItem.size;

    // Direct UI edits
    txtSize.textContent = formatBytes(downloadItem.size);
    txtDownloaded.textContent = `${formatBytes(downloadItem.downloadedBytes)} of ${formatBytes(downloadItem.size)}`;
    txtSpeed.textContent = formatSpeed(downloadItem.speed);
    
    // Time left ETA
    const remainingBytes = downloadItem.size - downloadItem.downloadedBytes;
    txtTimeLeft.textContent = (downloadItem.speed > 0 && remainingBytes > 0) ? 
                              formatETA(remainingBytes / downloadItem.speed) : 'Calculating...';
    
    updateProgressUI(downloadItem.progress);
    updateSegmentsGrid(progressData.segments);

    // Save to storage (throttled)
    throttleSaveQueue();

    // Broadcast to dashboard
    broadcastProgress(progressData.segments);
  };

  downloader.onComplete = (blob) => {
    downloadItem.progress = 100;
    downloadItem.speed = 0;
    
    const blobUrl = URL.createObjectURL(blob);
    chrome.downloads.download({
      url: blobUrl,
      filename: downloadItem.filename,
      saveAs: false
    }, (chromeId) => {
      downloadItem.chromeDownloadId = chromeId;
      saveQueue();
      
      // Enable native folder actions
      btnOpenFile.removeAttribute('disabled');
      btnOpenFolder.removeAttribute('disabled');
      
      setTimeout(() => URL.revokeObjectURL(blobUrl), 25000);
    });

    // Show native system notifications
    chrome.storage.local.get(['settings'], (data) => {
      const settings = data.settings || {};
      if (settings.showNotifications !== false) {
        chrome.notifications.create({
          type: 'basic',
          iconUrl: 'icons/icon48.png',
          title: 'Apex Download Manager',
          message: `Finished: ${downloadItem.filename}`
        });
      }
    });
  };

  downloader.onError = (err) => {
    console.warn('Accelerated Segmented Downloader failed, falling back to native browser downloader:', err);
    
    // Update UI status to show fallback
    txtStatusBadge.className = 'status-badge paused';
    txtStatusBadge.textContent = 'FALLBACK';
    txtTimeLeft.textContent = 'Switching to browser native engine...';
    
    // Trigger native browser download (uses shared cookies/auth context and bypasses CORS)
    chrome.downloads.download({
      url: downloadItem.url,
      filename: downloadItem.filename,
      saveAs: false
    }, (chromeId) => {
      if (chrome.runtime.lastError) {
        console.error('Native download fallback also failed:', chrome.runtime.lastError.message);
        downloadItem.status = 'failed';
        saveQueue();
        updateStatusUI('failed');
        alert(`Download failed: ${chrome.runtime.lastError.message}`);
        return;
      }
      
      // Successfully fell back to native!
      downloadItem.method = 'standard';
      downloadItem.chromeDownloadId = chromeId;
      downloadItem.status = 'downloading';
      saveQueue();
      
      // Stop segmented downloader UI updates and start tracking native download instead
      downloader = null;
      startNativeTrackingLoop(chromeId);
    });
  };

  // Initialize visual segment grids before starting download
  initializeSegmentsGrid(downloadItem.segmentsCount);

  downloader.start();
}

// Update UI button displays based on engine status
function updateStatusUI(status) {
  const badgeClasses = {
    idle: 'status-badge queued',
    queued: 'status-badge queued',
    downloading: 'status-badge active',
    paused: 'status-badge paused',
    completed: 'status-badge done',
    failed: 'status-badge error',
    merging: 'status-badge merging'
  };

  txtStatusBadge.className = badgeClasses[status] || 'status-badge';
  txtStatusBadge.textContent = status.toUpperCase();

  if (status === 'downloading') {
    btnPauseResume.textContent = 'Pause';
    btnPauseResume.className = 'action-btn btn-primary';
    btnPauseResume.style.display = 'inline-block';
    btnCancel.style.display = 'inline-block';
  } else if (status === 'paused' || status === 'failed') {
    btnPauseResume.textContent = 'Resume';
    btnPauseResume.className = 'action-btn btn-primary';
    btnPauseResume.style.display = 'inline-block';
    btnCancel.style.display = 'inline-block';
    txtSpeed.textContent = '--';
    txtTimeLeft.textContent = 'Paused';
  } else if (status === 'merging') {
    btnPauseResume.style.display = 'none';
    btnCancel.style.display = 'none';
    txtSpeed.textContent = '--';
    txtTimeLeft.textContent = 'Assembling segments...';
  } else if (status === 'completed') {
    btnPauseResume.style.display = 'none';
    btnCancel.textContent = 'Close';
    btnCancel.className = 'action-btn btn-secondary';
    btnCancel.style.display = 'inline-block';
    txtSpeed.textContent = '--';
    txtTimeLeft.textContent = 'Finished';
    btnOpenFile.removeAttribute('disabled');
    btnOpenFolder.removeAttribute('disabled');
  }
}

// Set initial empty state for progress map
function updateUIWithPausedState() {
  updateStatusUI(downloadItem.status);
  initializeSegmentsGrid(downloadItem.segmentsCount);
  for (let i = 0; i < downloadItem.segmentsCount; i++) {
    const p = downloadItem.status === 'completed' ? 100 : downloadItem.progress;
    updateSegmentBar(i, p);
  }
}

// Broadcast progress state to dashboards in real-time
function broadcastProgress(segments = []) {
  chrome.runtime.sendMessage({
    action: 'DOWNLOAD_PROGRESS',
    downloadId: downloadId,
    progress: downloadItem.progress,
    speed: downloadItem.speed,
    downloadedBytes: downloadItem.downloadedBytes,
    size: downloadItem.size,
    status: downloadItem.status,
    segments: segments
  }).catch(() => { /* Ignore errors when dashboard is closed */ });
}

// Create blank connection bars
function initializeSegmentsGrid(count) {
  segmentsGrid.innerHTML = '';
  for (let i = 0; i < count; i++) {
    const row = document.createElement('div');
    row.className = 'segment-row';
    row.innerHTML = `
      <span class="seg-num">#${i+1}</span>
      <div class="seg-bar-container">
        <div id="seg-fill-${i}" class="seg-bar-fill"></div>
      </div>
      <span id="seg-percent-${i}" class="seg-percent">0%</span>
    `;
    segmentsGrid.appendChild(row);
  }
}

// Update specific segments grid bars
function updateSegmentsGrid(segments) {
  if (!segments) return;
  segments.forEach(seg => {
    const percent = seg.total ? Math.min(100, (seg.downloaded / seg.total) * 100) : 0;
    updateSegmentBar(seg.id, percent);
  });
}

function updateSegmentBar(id, percent) {
  const fill = document.getElementById(`seg-fill-${id}`);
  const text = document.getElementById(`seg-percent-${id}`);
  if (fill) fill.style.width = `${percent}%`;
  if (text) text.textContent = `${Math.round(percent)}%`;
}

// Formatting Helpers
function formatBytes(bytes) {
  if (!bytes || bytes === 0) return '0.00 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function formatSpeed(bytesPerSec) {
  if (!bytesPerSec || bytesPerSec === 0) return '0 B/s';
  return formatBytes(bytesPerSec) + '/s';
}

function formatETA(seconds) {
  if (seconds === Infinity || isNaN(seconds) || seconds < 0) return 'Unknown';
  if (seconds === 0) return 'Completed';
  
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = Math.floor(seconds % 60);

  if (h > 0) return `${h}h ${m}m ${s}s`;
  if (m > 0) return `${m}m ${s}s`;
  return `${s}s`;
}

function updateProgressUI(percent) {
  progressBarFill.style.width = `${percent}%`;
  txtProgressPercent.textContent = `${Math.round(percent)}%`;
}

// Restart standard native download from scratch
function restartNativeDownload() {
  chrome.downloads.download({
    url: downloadItem.url,
    filename: downloadItem.filename,
    saveAs: false
  }, (chromeId) => {
    if (chrome.runtime.lastError) {
      downloadItem.status = 'failed';
      saveQueue();
      updateStatusUI('failed');
      alert(`Download failed: ${chrome.runtime.lastError.message}`);
      return;
    }
    downloadItem.chromeDownloadId = chromeId;
    downloadItem.status = 'downloading';
    saveQueue();
    startNativeTrackingLoop(chromeId);
  });
}

// Track and update standard native downloads progress in the popup window
function startNativeTrackingLoop(chromeId) {
  updateStatusUI('downloading');
  // Hide segments grid since it's a single stream
  segmentsGrid.innerHTML = `
    <div class="empty-state" style="padding: 10px; text-align: center; color: var(--text-muted); font-size: 12px; font-style: italic; width: 100%;">
      Standard download mode active (preserving cookie sessions & CORS checks)
    </div>
  `;

  const nativeInterval = setInterval(() => {
    chrome.downloads.search({ id: chromeId }, (results) => {
      if (chrome.runtime.lastError || !results || results.length === 0) {
        clearInterval(nativeInterval);
        return;
      }
      const item = results[0];
      
      if (item.state === 'complete') {
        clearInterval(nativeInterval);
        downloadItem.status = 'completed';
        downloadItem.progress = 100;
        downloadItem.speed = 0;
        saveQueue();
        updateStatusUI('completed');
        updateProgressUI(100);
        broadcastProgress();
      } else if (item.state === 'interrupted') {
        clearInterval(nativeInterval);
        downloadItem.status = 'failed';
        downloadItem.speed = 0;
        saveQueue();
        updateStatusUI('failed');
        broadcastProgress();
      } else if (item.state === 'in_progress') {
        downloadItem.size = item.totalBytes || downloadItem.size;
        downloadItem.downloadedBytes = item.bytesReceived;
        downloadItem.progress = downloadItem.size ? (item.bytesReceived / downloadItem.size) * 100 : 0;
        downloadItem.speed = item.estimatedSpeed || 0;
        
        // Update DOM
        txtSize.textContent = formatBytes(downloadItem.size);
        txtDownloaded.textContent = `${formatBytes(downloadItem.downloadedBytes)} of ${formatBytes(downloadItem.size)}`;
        txtSpeed.textContent = formatSpeed(downloadItem.speed);
        
        const remainingBytes = downloadItem.size - downloadItem.downloadedBytes;
        txtTimeLeft.textContent = (downloadItem.speed > 0 && remainingBytes > 0) ? 
                                  formatETA(remainingBytes / downloadItem.speed) : 'Calculating...';
        
        updateProgressUI(downloadItem.progress);
        
        // Save to storage (throttled)
        throttleSaveQueue();
        // Broadcast to dashboard
        broadcastProgress();
      }
    });
  }, 1000);
}
