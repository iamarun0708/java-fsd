// Dashboard manager for Apex Download Manager
import { SegmentedDownloader } from './downloader.js';

let downloads = [];
const activeEngines = {}; // Map of download ID -> SegmentedDownloader instance
let selectedDownloadId = null;
const latestSegments = {}; // In-memory cache for parallel segment states from active windows

// Settings default
let settings = {
  maxDownloads: 3,
  defaultSegments: 8,
  showNotifications: true
};

// DOM Elements
const downloadsList = document.getElementById('downloads-list');
const tableEmptyState = document.getElementById('table-empty-state');
const segmentPanel = document.getElementById('segment-panel');
const segmentsContainer = document.getElementById('segments-progress-container');
const btnCloseSegments = document.getElementById('btn-close-segments');

// Toolbar buttons
const btnAddUrl = document.getElementById('btn-add-url');
const btnResumeAll = document.getElementById('btn-resume-all');
const btnPauseAll = document.getElementById('btn-pause-all');
const btnClearCompleted = document.getElementById('btn-clear-completed');
const btnSettings = document.getElementById('btn-settings');
const searchInput = document.getElementById('search-input');

// Modals
const modalAddUrl = document.getElementById('modal-add-url');
const modalSettings = document.getElementById('modal-settings');

// Add URL inputs
const btnCloseAddModal = document.getElementById('btn-close-add-modal');
const btnCancelAdd = document.getElementById('btn-cancel-add');
const btnSubmitAdd = document.getElementById('btn-submit-add');
const inputDownloadUrl = document.getElementById('input-download-url');
const inputDownloadFilename = document.getElementById('input-download-filename');
const selectDownloadMethod = document.getElementById('select-download-method');

// Settings inputs
const btnCloseSettingsModal = document.getElementById('btn-close-settings-modal');
const btnCancelSettings = document.getElementById('btn-cancel-settings');
const btnSaveSettings = document.getElementById('btn-save-settings');
const inputSettingMaxDownloads = document.getElementById('input-setting-max-downloads');
const inputSettingDefaultSegments = document.getElementById('input-setting-default-segments');
const inputSettingNotifications = document.getElementById('input-setting-notifications');

// Categories
const catItems = document.querySelectorAll('.cat-item');
let activeFilter = { type: 'status', val: 'all' };

// Initialize Dashboard
document.addEventListener('DOMContentLoaded', async () => {
  await loadSettings();
  await loadDownloadsQueue();
  setupEventListeners();
  startGlobalUpdateLoop();
});

// Load settings from storage
async function loadSettings() {
  const data = await chrome.storage.local.get(['settings']);
  if (data.settings) {
    settings = { ...settings, ...data.settings };
  }
}

// Load downloads queue and handle active downloads recovery
async function loadDownloadsQueue() {
  const data = await chrome.storage.local.get(['downloads_queue']);
  downloads = data.downloads_queue || [];
  
  // Recovery: Any download set as 'downloading' should be reset to 'paused' on dashboard reload
  downloads.forEach(dl => {
    if (dl.status === 'downloading') {
      dl.status = 'paused';
      dl.speed = 0;
    }
  });
  await saveDownloadsQueue();
  renderDownloadsList();
}

// Save queue to local storage
async function saveDownloadsQueue() {
  await chrome.storage.local.set({ downloads_queue: downloads });
}

// Setup Event Listeners
function setupEventListeners() {
  // Categories Sidebar
  catItems.forEach(item => {
    item.addEventListener('click', () => {
      catItems.forEach(i => i.classList.remove('active'));
      item.classList.add('active');
      activeFilter.type = item.getAttribute('data-filter');
      activeFilter.val = item.getAttribute('data-val');
      renderDownloadsList();
    });
  });

  // Toolbar Actions
  btnAddUrl.addEventListener('click', () => showModal(modalAddUrl));
  btnSettings.addEventListener('click', () => {
    inputSettingMaxDownloads.value = settings.maxDownloads;
    inputSettingDefaultSegments.value = settings.defaultSegments;
    inputSettingNotifications.checked = settings.showNotifications;
    showModal(modalSettings);
  });
  
  btnResumeAll.addEventListener('click', resumeAllDownloads);
  btnPauseAll.addEventListener('click', pauseAllDownloads);
  btnClearCompleted.addEventListener('click', clearCompletedTasks);
  
  searchInput.addEventListener('input', renderDownloadsList);

  // Close Modals
  btnCloseAddModal.addEventListener('click', () => hideModal(modalAddUrl));
  btnCancelAdd.addEventListener('click', () => hideModal(modalAddUrl));
  btnCloseSettingsModal.addEventListener('click', () => hideModal(modalSettings));
  btnCancelSettings.addEventListener('click', () => hideModal(modalSettings));

  // Add Task submit
  btnSubmitAdd.addEventListener('click', submitAddTask);

  // Save Settings submit
  btnSaveSettings.addEventListener('click', saveSettingsAction);

  // Close segments drawer
  btnCloseSegments.addEventListener('click', () => {
    segmentPanel.classList.add('collapsed');
    selectedDownloadId = null;
    renderDownloadsList(); // Clear active highlights
  });

  // Intercept downloads storage updates from popup/content scripts
  chrome.storage.onChanged.addListener((changes, namespace) => {
    if (namespace === 'local' && changes.downloads_queue) {
      const newQueue = changes.downloads_queue.newValue || [];
      // Keep running engines synced, only update list structures
      const activeIds = Object.keys(activeEngines);
      
      // Update our local array while preserving status of active engines
      downloads = newQueue.map(item => {
        if (activeIds.includes(item.id)) {
          const currentItem = downloads.find(d => d.id === item.id);
          if (currentItem) {
            // Keep memory-updated state of active downloads
            return currentItem;
          }
        }
        return item;
      });

      renderDownloadsList();
      
      // Check if any newly added task in the queue needs starting
      downloads.forEach(dl => {
        if (dl.status === 'queued') {
          startDownload(dl.id);
        }
      });
    }
  });

  // Listen for real-time progress updates from standalone download windows
  chrome.runtime.onMessage.addListener((message) => {
    if (message.action === 'DOWNLOAD_PROGRESS') {
      const dl = downloads.find(d => d.id === message.downloadId);
      if (dl) {
        if (message.status === 'deleted') {
          loadDownloadsQueue();
          return;
        }

        dl.progress = message.progress;
        dl.speed = message.speed;
        dl.status = message.status;
        dl.downloadedBytes = message.downloadedBytes;
        dl.size = message.size;
        
        if (message.segments) {
          latestSegments[message.downloadId] = message.segments;
        }

        updateDOMRowProgress(dl);
        
        if (selectedDownloadId === dl.id) {
          updateSegmentPanel(dl);
        }

        if (message.status === 'completed' || message.status === 'paused' || message.status === 'failed') {
          loadDownloadsQueue();
        }
      }
    }
  });

  // Handle keys inside modals
  window.addEventListener('click', (e) => {
    if (e.target === modalAddUrl) hideModal(modalAddUrl);
    if (e.target === modalSettings) hideModal(modalSettings);
  });
}

function showModal(modalEl) {
  modalEl.classList.add('show');
}

function hideModal(modalEl) {
  modalEl.classList.remove('show');
}

// Format byte size
function formatBytes(bytes) {
  if (!bytes || bytes === 0) return '0.00 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// Format Speed (Bytes per second)
function formatSpeed(bytesPerSec) {
  if (!bytesPerSec || bytesPerSec === 0) return '0 B/s';
  return formatBytes(bytesPerSec) + '/s';
}

// Format ETA (seconds)
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

// Add manual URL download task
function submitAddTask() {
  const url = inputDownloadUrl.value.trim();
  let filename = inputDownloadFilename.value.trim();
  const method = selectDownloadMethod.value;

  if (!url) {
    alert('Please enter a valid URL.');
    return;
  }

  try {
    new URL(url); // Validate URL
  } catch (e) {
    alert('Invalid URL format. Please include protocol (http/https).');
    return;
  }

  if (!filename) {
    // derive filename
    try {
      const pathname = new URL(url).pathname;
      filename = pathname.substring(pathname.lastIndexOf('/') + 1);
      filename = decodeURIComponent(filename.split('?')[0]) || 'download';
    } catch(e) {
      filename = 'download';
    }
  }

  // Get extension
  const ext = filename.split('.').pop().toLowerCase() || 'dat';
  
  let category = 'document';
  if (['mp4', 'webm', 'mkv', 'avi', 'mov', 'flv'].includes(ext)) category = 'video';
  else if (['mp3', 'wav', 'aac', 'ogg', 'm4a'].includes(ext)) category = 'audio';
  else if (['zip', 'rar', '7z', 'tar', 'gz'].includes(ext)) category = 'compressed';

  const newDownload = {
    id: 'dl_' + Date.now(),
    url: url,
    filename: filename,
    extension: ext,
    mimeType: 'application/octet-stream',
    size: 0,
    category: category,
    status: 'queued',
    progress: 0,
    speed: 0,
    addedAt: Date.now(),
    method: method,
    segmentsCount: parseInt(settings.defaultSegments, 10)
  };

  downloads.push(newDownload);
  saveDownloadsQueue();
  renderDownloadsList();
  hideModal(modalAddUrl);
  
  // Clear inputs
  inputDownloadUrl.value = '';
  inputDownloadFilename.value = '';

  // Auto trigger download start
  startDownload(newDownload.id);
}

// Save Settings Action
function saveSettingsAction() {
  settings.maxDownloads = parseInt(inputSettingMaxDownloads.value, 10) || 3;
  settings.defaultSegments = parseInt(inputSettingDefaultSegments.value, 10) || 8;
  settings.showNotifications = inputSettingNotifications.checked;

  chrome.storage.local.set({ settings: settings }, () => {
    hideModal(modalSettings);
  });
}

// Render downloads table list
function renderDownloadsList() {
  const query = searchInput.value.toLowerCase().trim();
  downloadsList.innerHTML = '';

  // Filter queue
  const filtered = downloads.filter(dl => {
    // Search query filter
    const matchesSearch = dl.filename.toLowerCase().includes(query) || dl.url.toLowerCase().includes(query);
    if (!matchesSearch) return false;

    // Sidebar Category Filter
    if (activeFilter.type === 'status') {
      if (activeFilter.val === 'all') return true;
      return dl.status === activeFilter.val;
    } else if (activeFilter.type === 'category') {
      return dl.category === activeFilter.val;
    }
    return true;
  });

  if (filtered.length === 0) {
    tableEmptyState.style.display = 'flex';
    return;
  } else {
    tableEmptyState.style.display = 'none';
  }

  // Populate rows
  filtered.forEach(dl => {
    const row = document.createElement('tr');
    row.className = `download-row ${dl.status} ${selectedDownloadId === dl.id ? 'selected' : ''}`;
    row.setAttribute('data-id', dl.id);

    // Calculate time left (ETA)
    let etaText = 'Completed';
    if (dl.status === 'downloading') {
      const remainingBytes = dl.size - dl.downloadedBytes;
      if (dl.speed > 0 && remainingBytes > 0) {
        etaText = formatETA(remainingBytes / dl.speed);
      } else {
        etaText = 'Calculating...';
      }
    } else if (dl.status === 'paused') {
      etaText = 'Paused';
    } else if (dl.status === 'queued') {
      etaText = 'Queued';
    } else if (dl.status === 'failed') {
      etaText = 'Failed';
    }

    // Class for state badge
    const statusClasses = {
      queued: 'status-queued',
      downloading: 'status-active',
      paused: 'status-paused',
      completed: 'status-done',
      failed: 'status-error',
      merging: 'status-merging'
    };

    const statusLabel = dl.status.toUpperCase();

    row.innerHTML = `
      <td>
        <div class="file-name-cell">
          <span class="file-icon badge-${dl.category}">${dl.extension.toUpperCase()}</span>
          <div class="file-info">
            <span class="file-name" title="${dl.filename}">${dl.filename}</span>
            <span class="file-url" title="${dl.url}">${dl.url}</span>
          </div>
        </div>
      </td>
      <td>${formatBytes(dl.size)}</td>
      <td>
        <div class="progress-bar-container">
          <div class="progress-bar-fill" style="width: ${dl.progress}%"></div>
          <span class="progress-percent">${Math.round(dl.progress)}%</span>
        </div>
      </td>
      <td><span class="status-badge ${statusClasses[dl.status] || ''}">${statusLabel}</span></td>
      <td>${dl.status === 'downloading' ? formatSpeed(dl.speed) : '--'}</td>
      <td>${etaText}</td>
      <td>
        <div class="row-actions">
          ${dl.status === 'downloading' ? 
            `<button class="row-btn btn-pause" title="Pause Download">
              <svg viewBox="0 0 24 24" width="12" height="12" stroke="currentColor" stroke-width="2.5" fill="none"><rect x="6" y="4" width="4" height="16"></rect><rect x="14" y="4" width="4" height="16"></rect></svg>
             </button>` : 
            dl.status !== 'completed' ? 
            `<button class="row-btn btn-resume" title="Start/Resume Download">
              <svg viewBox="0 0 24 24" width="12" height="12" stroke="currentColor" stroke-width="2.5" fill="none"><polygon points="5 3 19 12 5 21 5 3"></polygon></svg>
             </button>` : ''
          }
          <button class="row-btn btn-delete" title="Delete Download Task">
            <svg viewBox="0 0 24 24" width="12" height="12" stroke="currentColor" stroke-width="2.5" fill="none"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
          </button>
        </div>
      </td>
    `;

    // Row selection for segment details
    row.addEventListener('click', (e) => {
      // Don't select if clicked actions button
      if (e.target.closest('.row-actions') || e.target.closest('.row-btn')) return;
      
      selectedDownloadId = dl.id;
      document.querySelectorAll('.download-row').forEach(r => r.classList.remove('selected'));
      row.classList.add('selected');
      
      updateSegmentPanel(dl);
    });

    // Row button event listeners
    const btnPause = row.querySelector('.btn-pause');
    const btnResume = row.querySelector('.btn-resume');
    const btnDelete = row.querySelector('.btn-delete');

    if (btnPause) {
      btnPause.addEventListener('click', () => pauseDownload(dl.id));
    }
    if (btnResume) {
      btnResume.addEventListener('click', () => startDownload(dl.id));
    }
    if (btnDelete) {
      btnDelete.addEventListener('click', () => deleteDownload(dl.id));
    }

    downloadsList.appendChild(row);
  });
}

// Update the Segment allocations visual bars (IDM Segment Panel)
function updateSegmentPanel(dl) {
  if (!dl) return;

  const isActiveSegment = dl.method === 'segmented' && (dl.status === 'downloading' || dl.status === 'paused' || dl.status === 'completed' || dl.status === 'merging');

  if (!isActiveSegment) {
    segmentPanel.classList.add('collapsed');
    return;
  }

  segmentPanel.classList.remove('collapsed');
  
  // Update header text
  document.getElementById('segment-title-name').textContent = `Segment Allocation Map: ${dl.filename}`;
  document.getElementById('seg-stat-size').textContent = formatBytes(dl.size);
  document.getElementById('seg-stat-downloaded').textContent = formatBytes(dl.downloadedBytes);
  document.getElementById('seg-stat-speed').textContent = dl.status === 'downloading' ? formatSpeed(dl.speed) : '--';
  document.getElementById('seg-stat-connections').textContent = dl.status === 'downloading' ? `${dl.segmentsCount} HTTP Ranges` : '--';

  // Render individual segment progress rows (Visual representation of parallel connections)
  segmentsContainer.innerHTML = '';
  
  const segments = latestSegments[dl.id] || [];

  if (segments.length === 0) {
    // If paused or completed, display estimated representation
    for (let i = 0; i < dl.segmentsCount; i++) {
      const isDone = dl.status === 'completed';
      const percent = isDone ? 100 : dl.progress;
      createSegmentProgressRow(i, percent);
    }
    return;
  }

  // Draw actual segment details from downloader instance
  segments.forEach((seg, idx) => {
    const percent = seg.total ? Math.min(100, (seg.downloaded / seg.total) * 100) : 0;
    createSegmentProgressRow(idx, percent, seg.downloaded, seg.total, seg.done);
  });
}

function createSegmentProgressRow(index, percent, downloaded = 0, total = 0, isDone = false) {
  const row = document.createElement('div');
  row.className = 'segment-progress-row';

  const label = `Conn #${index + 1}:`;
  const infoText = total ? `${formatBytes(downloaded)} / ${formatBytes(total)}` : '';
  const status = isDone ? 'Finished' : percent > 0 ? 'Receiving...' : 'Ready';

  row.innerHTML = `
    <div class="segment-label">${label}</div>
    <div class="segment-bar-container">
      <div class="segment-bar-fill" style="width: ${percent}%"></div>
      <span class="segment-bar-percent">${Math.round(percent)}%</span>
    </div>
    <div class="segment-status-text">${status}</div>
    <div class="segment-size-text">${infoText}</div>
  `;
  segmentsContainer.appendChild(row);
}

// Start download engine for a task by opening the standalone transfer window
function startDownload(id) {
  chrome.runtime.sendMessage({ action: 'OPEN_DOWNLOAD_WINDOW', downloadId: id });
}

// Pause individual download
function pauseDownload(id) {
  const dl = downloads.find(d => d.id === id);
  if (!dl) return;

  if (dl.method === 'standard' && dl.chromeDownloadId) {
    chrome.downloads.pause(dl.chromeDownloadId, () => {
      dl.status = 'paused';
      dl.speed = 0;
      renderDownloadsList();
      saveDownloadsQueue();
    });
    return;
  }

  const engine = activeEngines[id];
  if (engine) {
    engine.pause(); // status callbacks handles storage update and deletes engine instance
  }
}

// Delete download item
async function deleteDownload(id) {
  const dl = downloads.find(d => d.id === id);
  if (!dl) return;

  if (confirm(`Are you sure you want to delete ${dl.filename} task?`)) {
    // 1. Terminate engine if running
    const engine = activeEngines[id];
    if (engine) {
      engine.pause();
      delete activeEngines[id];
    }

    // 2. Clean up temporary filesystem chunks
    const tempEngine = new SegmentedDownloader(dl.url, dl.filename, dl.segmentsCount);
    tempEngine.id = id.replace('dl_', 'dl_engine_'); // recreate internal temp path id
    await tempEngine.cleanupTempFiles();

    // 3. Remove item from local history lists
    downloads = downloads.filter(d => d.id !== id);
    if (selectedDownloadId === id) {
      selectedDownloadId = null;
      segmentPanel.classList.add('collapsed');
    }

    saveDownloadsQueue();
    renderDownloadsList();
    
    // Resume queue items
    processQueue();
  }
}

// Resume all paused downloads
function resumeAllDownloads() {
  downloads.forEach(dl => {
    if (dl.status === 'paused' || dl.status === 'queued') {
      startDownload(dl.id);
    }
  });
}

// Pause all active downloads
function pauseAllDownloads() {
  Object.keys(activeEngines).forEach(id => {
    pauseDownload(id);
  });
}

// Clear completed tasks from download manager history
function clearCompletedTasks() {
  downloads = downloads.filter(dl => dl.status !== 'completed');
  saveDownloadsQueue();
  renderDownloadsList();
}

// Auto-run next in queue
function processQueue() {
  const activeCount = Object.keys(activeEngines).length;
  if (activeCount >= settings.maxDownloads) return;

  const nextTask = downloads.find(dl => dl.status === 'queued');
  if (nextTask) {
    startDownload(nextTask.id);
  }
}

// DOM Helper: fast update progress and speeds on specific table rows
function updateDOMRowProgress(dl) {
  const row = document.querySelector(`.download-row[data-id="${dl.id}"]`);
  if (!row) return;

  // Update progress width
  const fill = row.querySelector('.progress-bar-fill');
  if (fill) fill.style.width = `${dl.progress}%`;

  const percentText = row.querySelector('.progress-percent');
  if (percentText) percentText.textContent = `${Math.round(dl.progress)}%`;

  // Update speed
  const speedCell = row.cells[4];
  if (speedCell) speedCell.textContent = formatSpeed(dl.speed);

  // Update ETA
  const remainingBytes = dl.size - dl.downloadedBytes;
  const etaText = (dl.speed > 0 && remainingBytes > 0) ? formatETA(remainingBytes / dl.speed) : 'Calculating...';
  const etaCell = row.cells[5];
  if (etaCell) etaCell.textContent = etaText;
}

// Monitor standard chrome downloads progress dynamically
function startGlobalUpdateLoop() {
  setInterval(() => {
    // Check if we need to sync standard chrome downloads
    const stdDownloads = downloads.filter(dl => dl.method === 'standard' && dl.status === 'downloading');
    
    stdDownloads.forEach(dl => {
      if (dl.chromeDownloadId) {
        chrome.downloads.search({ id: dl.chromeDownloadId }, (results) => {
          if (results && results.length > 0) {
            const item = results[0];
            
            // Sync status
            if (item.state === 'complete') {
              dl.status = 'completed';
              dl.progress = 100;
              dl.speed = 0;
              saveDownloadsQueue();
              renderDownloadsList();
              processQueue();
            } else if (item.state === 'interrupted') {
              dl.status = 'failed';
              dl.speed = 0;
              saveDownloadsQueue();
              renderDownloadsList();
              processQueue();
            } else if (item.state === 'in_progress') {
              // Calculate progress
              dl.size = item.totalBytes || dl.size;
              dl.downloadedBytes = item.bytesReceived;
              dl.progress = dl.size ? (item.bytesReceived / dl.size) * 100 : 0;
              dl.speed = item.estimatedSpeed || 0;
              updateDOMRowProgress(dl);
            }
          }
        });
      }
    });
  }, 1000);
}
