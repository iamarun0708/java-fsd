// Background service worker for Apex Download Manager

// In-memory store for media detected on active tabs
const detectedMedia = {};

// File extensions to automatically sniff
const MEDIA_EXTENSIONS = [
  'mp4', 'webm', 'mkv', 'avi', 'mov', 'flv', 'wmv', 'm3u8', 'mpd',
  'mp3', 'wav', 'aac', 'ogg', 'm4a', 'flac',
  'zip', 'rar', '7z', 'tar', 'gz', 'iso', 'exe', 'msi', 'pdf', 'dmg'
];

// Disable the browser's native downloads shelf
function disableDownloadsShelf() {
  if (chrome.downloads && chrome.downloads.setShelfEnabled) {
    chrome.downloads.setShelfEnabled(false);
  }
}

chrome.runtime.onInstalled.addListener(() => {
  disableDownloadsShelf();
  console.log("Apex Download Manager installed. Downloads shelf disabled.");
});

chrome.runtime.onStartup.addListener(() => {
  disableDownloadsShelf();
});

// Helper to extract filename from URL or headers
function getFilename(url, headers) {
  let filename = '';

  // Try Content-Disposition header first
  if (headers) {
    const disposition = headers.find(h => h.name.toLowerCase() === 'content-disposition');
    if (disposition && disposition.value) {
      const filenameMatch = disposition.value.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      if (filenameMatch && filenameMatch[1]) {
        filename = filenameMatch[1].replace(/['"]/g, '');
        // Decode RFC 5987 parameter if present
        if (filename.startsWith("UTF-8''")) {
          filename = decodeURIComponent(filename.substring(7));
        }
      }
    }
  }

  // Fallback to URL path
  if (!filename) {
    try {
      const parsedUrl = new URL(url);
      const pathname = parsedUrl.pathname;
      filename = pathname.substring(pathname.lastIndexOf('/') + 1);
      filename = decodeURIComponent(filename.split('?')[0]);
    } catch (e) {
      filename = '';
    }
  }

  return filename || 'downloaded_file';
}

// Helper to get extension from filename or url
function getExtension(filename, url) {
  let ext = '';
  if (filename) {
    const parts = filename.split('.');
    if (parts.length > 1) ext = parts.pop().toLowerCase();
  }
  if (!ext && url) {
    try {
      const pathname = new URL(url).pathname;
      const parts = pathname.split('.');
      if (parts.length > 1) ext = parts.pop().split('?')[0].toLowerCase();
    } catch (e) {}
  }
  return ext;
}

// Sniff requests (Media sniffer)
chrome.webRequest.onHeadersReceived.addListener(
  (details) => {
    // Ignore extension pages and background requests
    if (details.url.startsWith('chrome-extension://')) return;

    const headers = details.responseHeaders || [];
    const contentTypeHeader = headers.find(h => h.name.toLowerCase() === 'content-type');
    const contentLengthHeader = headers.find(h => h.name.toLowerCase() === 'content-length');

    const contentType = contentTypeHeader ? contentTypeHeader.value.toLowerCase() : '';
    const contentLength = contentLengthHeader ? parseInt(contentLengthHeader.value, 10) : 0;

    let isMedia = false;
    let category = 'document';

    // Check by content-type
    if (contentType.startsWith('video/')) {
      isMedia = true;
      category = 'video';
    } else if (contentType.startsWith('audio/')) {
      isMedia = true;
      category = 'audio';
    } else if (
      contentType.includes('application/x-mpegurl') || 
      contentType.includes('application/vnd.apple.mpegurl') ||
      contentType.includes('application/x-mpeg')
    ) {
      isMedia = true;
      category = 'video'; // HLS Stream
    } else if (contentType.includes('application/dash+xml')) {
      isMedia = true;
      category = 'video'; // DASH Stream
    } else if (
      contentType.includes('application/zip') ||
      contentType.includes('application/x-rar-compressed') ||
      contentType.includes('application/x-7z-compressed') ||
      contentType.includes('application/x-zip-compressed')
    ) {
      isMedia = true;
      category = 'compressed';
    }

    // Double check by file extension in URL
    const filename = getFilename(details.url, headers);
    const ext = getExtension(filename, details.url);

    if (!isMedia && MEDIA_EXTENSIONS.includes(ext)) {
      isMedia = true;
      if (['mp4', 'webm', 'mkv', 'avi', 'mov', 'flv', 'wmv', 'm3u8', 'mpd'].includes(ext)) {
        category = 'video';
      } else if (['mp3', 'wav', 'aac', 'ogg', 'm4a', 'flac'].includes(ext)) {
        category = 'audio';
      } else if (['zip', 'rar', '7z', 'tar', 'gz'].includes(ext)) {
        category = 'compressed';
      } else {
        category = 'document';
      }
    }

    // Skip small files under 50KB unless it's a playlist m3u8/mpd
    if (isMedia && contentLength < 50000 && !['m3u8', 'mpd'].includes(ext)) {
      if (contentLength > 0 && !contentType.startsWith('video/')) {
        isMedia = false;
      }
    }

    if (isMedia && details.tabId >= 0) {
      const tabId = details.tabId;
      if (!detectedMedia[tabId]) {
        detectedMedia[tabId] = [];
      }

      // Check for duplicates
      const exists = detectedMedia[tabId].some(m => m.url === details.url);
      if (!exists) {
        const mediaItem = {
          id: 'media_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9),
          url: details.url,
          filename: filename,
          extension: ext || 'mp4',
          mimeType: contentType,
          size: contentLength,
          category: category,
          detectedAt: Date.now()
        };

        detectedMedia[tabId].push(mediaItem);

        // Update badge
        chrome.action.setBadgeText({ text: String(detectedMedia[tabId].length), tabId });
        chrome.action.setBadgeBackgroundColor({ color: '#A855F7', tabId });

        // Notify content scripts or popup if open
        chrome.runtime.sendMessage({
          action: 'MEDIA_DETECTED',
          tabId: tabId,
          media: mediaItem
        }).catch(() => { /* Ignore errors when popup is closed */ });
      }
    }
  },
  { urls: ["<all_urls>"] },
  ["responseHeaders"]
);

// Intercept browser downloads
chrome.downloads.onCreated.addListener((downloadItem) => {
  // CRITICAL LOOP PREVENTION:
  // If download was programmatically triggered by us, ignore it.
  if (downloadItem.byExtensionId === chrome.runtime.id) {
    return;
  }

  // Ignore internal/blob URLs
  if (
    downloadItem.url.startsWith('blob:') || 
    downloadItem.url.startsWith('chrome-extension:') || 
    downloadItem.url.startsWith('data:')
  ) {
    return;
  }

  // Cancel the native browser download
  chrome.downloads.cancel(downloadItem.id);
  // Erase from download history to avoid browser dialogs
  chrome.downloads.erase({ id: downloadItem.id });

  // Extract filename
  let filename = downloadItem.filename ? downloadItem.filename.split(/[\\/]/).pop() : '';
  if (!filename) {
    try {
      filename = decodeURIComponent(new URL(downloadItem.url).pathname.split('/').pop()) || 'download';
    } catch (e) {
      filename = 'download';
    }
  }

  const ext = filename.split('.').pop().toLowerCase() || 'dat';
  
  let category = 'document';
  if (['mp4', 'webm', 'mkv', 'avi', 'mov', 'flv', 'wmv'].includes(ext)) category = 'video';
  else if (['mp3', 'wav', 'aac', 'ogg', 'm4a', 'flac'].includes(ext)) category = 'audio';
  else if (['zip', 'rar', '7z', 'tar', 'gz'].includes(ext)) category = 'compressed';

  const downloadId = 'dl_' + Date.now();
  const newDownload = {
    id: downloadId,
    url: downloadItem.url,
    filename: filename,
    extension: ext,
    mimeType: downloadItem.mimeType || 'application/octet-stream',
    size: downloadItem.fileSize || 0,
    category: category,
    status: 'queued',
    progress: 0,
    speed: 0,
    addedAt: Date.now(),
    method: 'segmented', // Default to segmented downloader
    segmentsCount: 8
  };

  // Add to local storage downloads_queue
  chrome.storage.local.get(['downloads_queue'], (data) => {
    const queue = data.downloads_queue || [];
    const exists = queue.some(q => q.url === newDownload.url && q.status === 'downloading');
    
    if (!exists) {
      queue.push(newDownload);
      chrome.storage.local.set({ downloads_queue: queue }, () => {
        openDownloadWindow(downloadId);
      });
    } else {
      const existingItem = queue.find(q => q.url === newDownload.url);
      if (existingItem) {
        openDownloadWindow(existingItem.id);
      }
    }
  });
});

// Launch standalone window for downloading
function openDownloadWindow(downloadId) {
  const url = chrome.runtime.getURL(`download_window.html?id=${downloadId}`);
  chrome.windows.create({
    url: url,
    type: 'popup',
    width: 680,
    height: 440,
    focused: true
  });
}

// Clear tab data on reload/close
chrome.tabs.onUpdated.addListener((tabId, changeInfo) => {
  if (changeInfo.status === 'loading') {
    detectedMedia[tabId] = [];
    chrome.action.setBadgeText({ text: '', tabId });
  }
});

chrome.tabs.onRemoved.addListener((tabId) => {
  delete detectedMedia[tabId];
});

// Listener for messages
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
  if (message.action === 'GET_MEDIA') {
    const tabId = message.tabId || (sender.tab ? sender.tab.id : null);
    sendResponse({ media: detectedMedia[tabId] || [] });
  } else if (message.action === 'OPEN_DASHBOARD') {
    const url = chrome.runtime.getURL('dashboard.html');
    chrome.tabs.query({ url: url }, (tabs) => {
      if (tabs.length > 0) {
        chrome.tabs.update(tabs[0].id, { active: true });
      } else {
        chrome.tabs.create({ url: url });
      }
    });
    sendResponse({ success: true });
  } else if (message.action === 'OPEN_DOWNLOAD_WINDOW') {
    openDownloadWindow(message.downloadId);
    sendResponse({ success: true });
  }
  return true;
});

console.log("Apex Download Manager background active. Shelf disabled and download interceptor registered.");
