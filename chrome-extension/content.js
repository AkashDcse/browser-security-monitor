// Monitor copy events
document.addEventListener('copy', function(e) {
    let selectedText = window.getSelection().toString();
    if (selectedText && selectedText.length > 0) {
        fetch('http://localhost:8080/dlp-event', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                url: window.location.href,
                event_type: 'copy',
                content_preview: selectedText.substring(0, 100)
            })
        }).catch(err => console.error('DLP copy error:', err));
    }
});

// Monitor file uploads
document.addEventListener('change', function(e) {
    if (e.target.type === 'file' && e.target.files.length > 0) {
        fetch('http://localhost:8080/dlp-event', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                url: window.location.href,
                event_type: 'file_upload',
                content_preview: e.target.files[0].name
            })
        }).catch(err => console.error('DLP upload error:', err));
    }
});