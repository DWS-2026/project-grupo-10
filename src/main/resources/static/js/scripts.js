// Custom JavaScript for Olympo Academy
// This file is loaded on every page (via footer partial) and contains shared UI helpers.

function initUserSidePanel() {
    const userMenuBtn = document.getElementById('userMenuBtn');
    const closePanelBtn = document.getElementById('closePanelBtn');

    if (userMenuBtn && !userMenuBtn.dataset.listenerAttached) {
        userMenuBtn.addEventListener('click', () => {
            document.getElementById('userSidePanel')?.classList.add('open');
        });
        userMenuBtn.dataset.listenerAttached = 'true';
    }

    if (closePanelBtn && !closePanelBtn.dataset.listenerAttached) {
        closePanelBtn.addEventListener('click', () => {
            document.getElementById('userSidePanel')?.classList.remove('open');
        });
        closePanelBtn.dataset.listenerAttached = 'true';
    }
}

function initAutoHideAlerts() {
    document.querySelectorAll('.auto-hide-alert').forEach((alertEl) => {
        // Avoid attaching multiple timers if script loads twice.
        if (alertEl.dataset.autoHideStarted) return;
        alertEl.dataset.autoHideStarted = 'true';

        const delay = Number(alertEl.dataset.hideDelay) || 30000;
        setTimeout(() => {
            alertEl.style.transition = 'opacity 0.3s ease';
            alertEl.style.opacity = '0';
            setTimeout(() => alertEl.remove(), 300);
        }, delay);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    initUserSidePanel();
    initAutoHideAlerts();
});
