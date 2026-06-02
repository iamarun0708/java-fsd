// Content script for Apex Download Manager

let activeVideos = new Map(); // Maps video elements to their overlay buttons

// Helper to create the floating download button
function createFloatingButton(video) {
  if (activeVideos.has(video)) return;

  const btnContainer = document.createElement('div');
  btnContainer.className = 'apex-downloader-floating-widget';
  
  const mainBtn = document.createElement('button');
  mainBtn.className = 'apex-downloader-btn';
  mainBtn.innerHTML = `
    <svg viewBox="0 0 24 24" width="16" height="16" stroke="currentColor" stroke-width="2.5" fill="none" stroke-linecap="round" stroke-linejoin="round">
      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
      <polyline points="7 10 12 15 17 10"></polyline>
      <line x1="12" y1="15" x2="12" y2="3"></line>
    </svg>
    <span>Download Video</span>
  `;

  const dropdown = document.createElement('div');
  dropdown.className = 'apex-downloader-dropdown';
  dropdown.innerHTML = `<div class="apex-dropdown-loading">Sniffing video links...</div>`;

  btnContainer.appendChild(mainBtn);
  btnContainer.appendChild(dropdown);

  // Position logic
  // We append it to the video's parent container to make sure it scales and goes full screen with the player
  const container = video.parentElement;
  if (!container) return;

  const computedStyle = window.getComputedStyle(container);
  if (computedStyle.position === 'static') {
    container.style.setProperty('position', 'relative', 'important');
  }

  container.appendChild(btnContainer);
  activeVideos.set(video, btnContainer);

  // Toggle dropdown on button click
  mainBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    e.preventDefault();
    
    const isShowing = btnContainer.classList.contains('show-dropdown');
    
    // Hide all other dropdowns
    document.querySelectorAll('.apex-downloader-floating-widget').forEach(w => w.classList.remove('show-dropdown'));
    
    if (!isShowing) {
      btnContainer.classList.add('show-dropdown');
      loadDropdownContent(dropdown, video);
    }
  });

  // Hide dropdown when clicking elsewhere
  document.addEventListener('click', () => {
    btnContainer.classList.remove('show-dropdown');
  });

  // Monitor video removal from DOM to clean up
  const cleanupObserver = new MutationObserver(() => {
    if (!video.isConnected) {
      btnContainer.remove();
      activeVideos.delete(video);
      cleanupObserver.disconnect();
    }
  });
  cleanupObserver.observe(document.body, { childList: true, subtree: true });
}

// Format byte size
function formatBytes(bytes) {
  if (!bytes) return 'Unknown Size';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
}

// Load sniffed files and video sources into the dropdown
function loadDropdownContent(dropdownEl, videoEl) {
  chrome.runtime.sendMessage({ action: 'GET_MEDIA' }, (response) => {
    const list = response?.media || [];
    
    // Add current video source if it's a direct URL and not already sniffed
    const currentSrc = videoEl.currentSrc || videoEl.src;
    if (currentSrc && !currentSrc.startsWith('blob:') && !list.some(m => m.url === currentSrc)) {
      list.push({
        id: 'direct_' + Date.now(),
        url: currentSrc,
        filename: document.title || 'Video Download',
        extension: currentSrc.split('.').pop().split('?')[0] || 'mp4',
        mimeType: 'video/mp4',
        size: 0,
        category: 'video'
      });
    }

    dropdownEl.innerHTML = '';

    if (list.length === 0) {
      dropdownEl.innerHTML = `<div class="apex-dropdown-empty">No download links captured yet. Play the video to capture.</div>`;
      return;
    }

    // Filter to show video/audio content
    const mediaItems = list.filter(item => item.category === 'video' || item.category === 'audio');

    if (mediaItems.length === 0) {
      dropdownEl.innerHTML = `<div class="apex-dropdown-empty">No video streams detected.</div>`;
      return;
    }

    // List items
    mediaItems.forEach(item => {
      const row = document.createElement('div');
      row.className = 'apex-dropdown-item';
      
      const fileExt = item.extension.toUpperCase();
      const fileSize = formatBytes(item.size);
      
      row.innerHTML = `
        <div class="apex-item-info">
          <span class="apex-item-ext badge-${item.category}">${fileExt}</span>
          <span class="apex-item-name" title="${item.filename}">${item.filename}</span>
        </div>
        <div class="apex-item-actions">
          <span class="apex-item-size">${fileSize}</span>
          <button class="apex-item-download-btn" title="Download Now">
            <svg viewBox="0 0 24 24" width="12" height="12" stroke="currentColor" stroke-width="2.5" fill="none">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="7 10 12 15 17 10"></polyline>
              <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
          </button>
        </div>
      `;

      row.querySelector('.apex-item-download-btn').addEventListener('click', (e) => {
        e.stopPropagation();
        e.preventDefault();
        
        chrome.storage.local.get(['downloads_queue'], (data) => {
          const queue = data.downloads_queue || [];
          const existing = queue.find(q => q.url === item.url);
          
          if (!existing) {
            const dlId = 'dl_' + Date.now();
            const newDownload = {
              id: dlId,
              url: item.url,
              filename: item.filename,
              extension: item.extension,
              mimeType: item.mimeType,
              size: item.size,
              category: item.category,
              status: 'queued',
              progress: 0,
              speed: 0,
              addedAt: Date.now(),
              method: 'segmented', // Default to segmented downloader
              segmentsCount: 8
            };
            queue.push(newDownload);
            chrome.storage.local.set({ downloads_queue: queue }, () => {
              chrome.runtime.sendMessage({ action: 'OPEN_DOWNLOAD_WINDOW', downloadId: dlId });
            });
          } else {
            chrome.runtime.sendMessage({ action: 'OPEN_DOWNLOAD_WINDOW', downloadId: existing.id });
          }
        });
      });

      dropdownEl.appendChild(row);
    });
  });
}

// Main scanner
function scanForVideos() {
  const videos = document.querySelectorAll('video');
  videos.forEach(video => {
    // Only show button if video is visible and has a source
    if (video.offsetWidth > 100 && video.offsetHeight > 50) {
      // Create button when video starts playing or is hovered
      video.addEventListener('play', () => createFloatingButton(video));
      video.addEventListener('mouseenter', () => createFloatingButton(video));
      
      // If already playing, create button immediately
      if (!video.paused) {
        createFloatingButton(video);
      }
    }
  });
}

// Run scans periodically and on events
setInterval(scanForVideos, 2000);
document.addEventListener('DOMContentLoaded', scanForVideos);
window.addEventListener('load', scanForVideos);

console.log("Apex Download Manager video capture injected.");
