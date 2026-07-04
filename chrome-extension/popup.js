let currentTabUrl = null;

// Get current tab URL
chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
  if (tabs[0]) {
    currentTabUrl = tabs[0].url;
    checkCurrentSite(currentTabUrl);
  }
});

function checkCurrentSite(url) {
  const statusDiv = document.getElementById('siteStatus');
  statusDiv.innerHTML = '<span class="scanner" style="width:16px;height:16px;"></span> Scanning...';
  
  fetch('http://localhost:8080/check', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ url: url })
  })
  .then(res => res.json())
  .then(data => {
    if (data.dangerous) {
      statusDiv.innerHTML = '<span class="risk-high">⚠️ DANGEROUS</span>';
      document.getElementById('scanStatus').innerHTML = '⚠️ Threat detected! Check alerts below.';
    } else {
      statusDiv.innerHTML = '<span class="risk-low">✅ SAFE</span>';
      document.getElementById('scanStatus').innerHTML = '✅ No threats detected.';
    }
  })
  .catch(() => {
    statusDiv.innerHTML = '<span class="risk-med">❓ Cannot reach backend</span>';
  });
}

function loadStatsAndAlerts() {
  // Load stats
  fetch('http://localhost:8080/stats')
    .then(res => res.json())
    .then(data => {
      document.getElementById('totalAlerts').innerText = data.total_alerts || 0;
      document.getElementById('avgRisk').innerText = data.average_risk_score || 0;
    })
    .catch(() => {
      document.getElementById('totalAlerts').innerText = '?';
      document.getElementById('avgRisk').innerText = '?';
    });
  
  // Load alerts
  fetch('http://localhost:8080/alerts')
    .then(res => res.json())
    .then(data => {
      const alertsDiv = document.getElementById('alertsList');
      if (data.alerts && data.alerts.length > 0) {
        alertsDiv.innerHTML = '<strong>📋 Recent Alerts:</strong>';
        data.alerts.slice(0, 5).forEach(alert => {
          alertsDiv.innerHTML += `
            <div class="alert-item">
              🔗 ${alert.url.substring(0, 50)}${alert.url.length > 50 ? '...' : ''}<br>
              📋 ${alert.reason}<br>
              📊 Risk Score: ${alert.risk_score}/100
            </div>
          `;
        });
        // Add feedback button after alerts
        const feedbackDiv = document.createElement('div');
        feedbackDiv.style.marginTop = '10px';
        feedbackDiv.innerHTML = `<button id="feedbackBtn" style="background:#4CAF50; color:white; border:none; padding:5px 10px; border-radius:4px; cursor:pointer;">👍 Was this alert helpful?</button>`;
        alertsDiv.appendChild(feedbackDiv);
        document.getElementById('feedbackBtn')?.addEventListener('click', () => {
          fetch('http://localhost:8080/training-feedback', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ helpful: true })
          }).then(() => alert('Thank you for your feedback!')).catch(err => console.error(err));
        });
      } else {
        alertsDiv.innerHTML = '<div style="text-align:center;padding:20px;color:#999;">✅ No threats detected yet</div>';
      }
    })
    .catch(() => {
      document.getElementById('alertsList').innerHTML = '<div style="text-align:center;padding:20px;color:#f44336;">❌ Cannot connect to backend</div>';
    });
}

// Refresh on button click
document.getElementById('refreshBtn').addEventListener('click', () => {
  loadStatsAndAlerts();
  if (currentTabUrl) checkCurrentSite(currentTabUrl);
});

// Initial load
loadStatsAndAlerts();

// Refresh every 30 seconds
setInterval(() => {
  loadStatsAndAlerts();
}, 30000);