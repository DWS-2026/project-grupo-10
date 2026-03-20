// Custom JavaScript for Olympo Academy
// This file is loaded on every page (via footer partial) and contains shared UI helpers.

function initUserSidePanel() {
    const userMenuBtn = document.getElementById('userMenuBtn');
    const closePanelBtn = document.getElementById('closePanelBtn');
    const cartLink = document.querySelector('.cart-link');

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

    if (cartLink && !cartLink.dataset.listenerAttached) {
        cartLink.addEventListener('click', (event) => {
            event.preventDefault();
            document.getElementById('userSidePanel')?.classList.add('open');
            document.querySelector('.cart-container')?.classList.toggle('show');
        });
        cartLink.dataset.listenerAttached = 'true';
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

function initBookingPopup() {
    const buttons = document.querySelectorAll('.booking-btn');
    if (!buttons.length) return;

    const classIdInput = document.getElementById('booking-class-id');
    const facilityIdInput = document.getElementById('booking-facility-id');
    const nameInput = document.getElementById('booking-name');

    buttons.forEach((btn) => {
        if (btn.dataset.listenerAttached) return;
        btn.addEventListener('click', () => {
            const type = btn.dataset.type;
            const id = btn.dataset.id;
            const name = btn.dataset.name;

            if (nameInput) nameInput.value = name || '';

            if (type === 'class') {
                if (classIdInput) classIdInput.value = id || '';
                if (facilityIdInput) facilityIdInput.value = '';
            } else {
                if (facilityIdInput) facilityIdInput.value = id || '';
                if (classIdInput) classIdInput.value = '';
            }
        });
        btn.dataset.listenerAttached = 'true';
    });
}

document.addEventListener('DOMContentLoaded', () => {
    initUserSidePanel();
    initAutoHideAlerts();
    initBookingPopup();
});
