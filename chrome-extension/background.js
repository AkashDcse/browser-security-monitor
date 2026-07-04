chrome.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {
  if (changeInfo.status === "complete" && tab.url && tab.url.startsWith("http")) {
    console.log("Sending URL to backend:", tab.url);
    
    fetch("http://localhost:8080/check", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ url: tab.url, _: Date.now() })
    })
    .then(res => res.json())
    .then(data => {
      console.log("Response:", data);
      
      if (data.dangerous) {
        // Red badge
        chrome.action.setBadgeText({ text: "!", tabId });
        chrome.action.setBadgeBackgroundColor({ color: "#FF0000" });
        
        // Show system notification (popup)
        chrome.notifications.create({
          type: "basic",
          iconUrl: "icon.png",
          title: "⚠️ SECURITY ALERT",
          message: `Dangerous site blocked!\nRisk: ${data.risk_score}%\n${data.reasons.join(", ")}`,
          priority: 2
        });
      } else {
        // Green checkmark for safe sites
        chrome.action.setBadgeText({ text: "✓", tabId });
        chrome.action.setBadgeBackgroundColor({ color: "#4CAF50" });
      }
    })
    .catch(err => console.error("Fetch error:", err));
  }
});

// ========== NEW: Extension Inventory Scanner ==========
function scanExtensions() {
  chrome.management.getAll((extensions) => {
    extensions.forEach(ext => {
      if (ext.id !== chrome.runtime.id) {
        fetch('http://localhost:8080/extension-inventory', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            name: ext.name,
            id: ext.id,
            version: ext.version,
            enabled: ext.enabled,
            permissions: ext.permissions || []
          })
        }).catch(err => console.error('Inventory error:', err));
      }
    });
  });
}

// Initial scan
scanExtensions();
// Scan every hour
setInterval(scanExtensions, 3600000);