// Popup controller for Apex Download Manager

document.addEventListener('DOMContentLoaded', () => {
  const mediaListContainer = document.getElementById('media-list');
  const btnOpenDashboard = document.getElementById('btn-open-dashboard');
  const tabButtons = document.querySelectorAll('.tab-btn');
  const footerStatusText = document.getElementById('footer-status-text');

  let activeTabId = null;
  let allMedia = [];
  let currentFilter = 'all';

  // Get active tab and load media
  chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
    if (tabs.length === 0) return;
    const activeTab = tabs[0];
    activeTabId = activeTab.id;

    loadMediaItems();
  });

  // Open Full Dashboard click
  btnOpenDashboard.addEventListener('click', () => {
    chrome.runtime.sendMessage({ action: 'OPEN_DASHBOARD' });
  });

  // Filter Buttons Click
  tabButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      tabButtons.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      currentFilter = btn.getAttribute('data-filter');
      renderMediaList();
    });
  });

  // Request media list from background service worker
  function loadMediaItems() {
    if (!activeTabId) return;

    chrome.runtime.sendMessage({ action: 'GET_MEDIA', tabId: activeTabId }, (response) => {
      allMedia = response?.media || [];
      renderMediaList();
      updateFooterStatus();
    });
  }

  // Format byte sizes
  function formatBytes(bytes) {
    if (!bytes || bytes === 0) return 'Unknown Size';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  // Update status in footer
  function updateFooterStatus() {
    const totalCount = allMedia.length;
    if (totalCount === 0) {
      footerStatusText.textContent = 'No media detected';
    } else {
      footerStatusText.textContent = `${totalCount} item${totalCount > 1 ? 's' : ''} detected on page`;
    }
  }

  // Render media items based on filter
  function renderMediaList() {
    mediaListContainer.innerHTML = '';

    const filteredMedia = allMedia.filter(item => {
      if (currentFilter === 'all') return true;
      return item.category === currentFilter;
    });

    if (filteredMedia.length === 0) {
      mediaListContainer.innerHTML = `
        <div class="empty-state">
          <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="1.5" fill="none">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
          <p>No ${currentFilter !== 'all' ? currentFilter : ''} files detected.</p>
          <span class="tip">Tip: Play a video or audio file to capture it.</span>
        </div>
      `;
      return;
    }

    filteredMedia.forEach(item => {
      const row = document.createElement('div');
      row.className = 'media-item';

      const fileExt = item.extension.toUpperCase();
      const fileSize = formatBytes(item.size);

      row.innerHTML = `
        <div class="media-details">
          <div class="media-title-row">
            <span class="media-badge badge-${item.category}">${fileExt}</span>
            <span class="media-title" title="${item.filename}">${item.filename}</span>
          </div>
          <div class="media-meta-row">
            <span class="media-size">${fileSize}</span>
            <span class="media-mime">${item.mimeType.split(';')[0]}</span>
          </div>
        </div>
        <div class="media-actions">
          <button class="action-btn btn-copy" title="Copy Download Link">
            <svg viewBox="0 0 24 24" width="14" height="14" stroke="currentColor" stroke-width="2" fill="none" class="copy-icon">
              <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"></path>
              <rect x="8" y="2" width="8" height="4" rx="1" ry="1"></rect>
            </svg>
            <svg viewBox="0 0 24 24" width="14" height="14" stroke="#10B981" stroke-width="2" fill="none" class="check-icon hidden">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
          </button>
          <button class="action-btn btn-download" title="Add to Download Manager">
            <svg viewBox="0 0 24 24" width="14" height="14" stroke="currentColor" stroke-width="2" fill="none">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="7 10 12 15 17 10"></polyline>
              <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
          </button>
        </div>
      `;

      // Copy Link Functionality
      const btnCopy = row.querySelector('.btn-copy');
      const copyIcon = btnCopy.querySelector('.copy-icon');
      const checkIcon = btnCopy.querySelector('.check-icon');

      btnCopy.addEventListener('click', (e) => {
        e.stopPropagation();
        navigator.clipboard.writeText(item.url).then(() => {
          copyIcon.classList.add('hidden');
          checkIcon.classList.remove('hidden');
          setTimeout(() => {
            copyIcon.classList.remove('hidden');
            checkIcon.classList.add('hidden');
          }, 1500);
        });
      });

      // Download Action Functionality
      const btnDownload = row.querySelector('.btn-download');
      btnDownload.addEventListener('click', (e) => {
        e.stopPropagation();
        
        chrome.storage.local.get(['downloads_queue'], (data) => {
          const queue = data.downloads_queue || [];
          const existing = queue.find(q => q.url === item.url);
          
          if (!existing) {
            const dlId = 'dl_' + Date.now();
            queue.push({
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
              method: 'segmented',
              segmentsCount: 8
            });
            chrome.storage.local.set({ downloads_queue: queue }, () => {
              chrome.runtime.sendMessage({ action: 'OPEN_DOWNLOAD_WINDOW', downloadId: dlId });
            });
          } else {
            chrome.runtime.sendMessage({ action: 'OPEN_DOWNLOAD_WINDOW', downloadId: existing.id });
          }
        });
      });

      mediaListContainer.appendChild(row);
    });
  }

  // Dynamically listen for new items added while popup is open
  chrome.runtime.onMessage.addListener((message) => {
    if (message.action === 'MEDIA_DETECTED' && message.tabId === activeTabId) {
      // Check if duplicate in memory list
      if (!allMedia.some(m => m.url === message.media.url)) {
        allMedia.push(message.media);
        renderMediaList();
        updateFooterStatus();
      }
    }
  });
});
