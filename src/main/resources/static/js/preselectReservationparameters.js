function preselectEditReservationValues(popupId) {
    const popup = document.getElementById(popupId);
    if (!popup) return;

    // Hour
    const startTimeSelect = popup.querySelector('select[name="startTime"]');
    if (startTimeSelect) {
        const current = startTimeSelect.dataset.currentValues?.trim();
        Array.from(startTimeSelect.options).forEach(opt => {
            opt.selected = opt.value.trim() === current;
        });
    }

    // Duration
    const durationSelect = popup.querySelector('select[name="duration"]');
    if (durationSelect) {
        const current = parseInt(durationSelect.dataset.currentDuration);
        Array.from(durationSelect.options).forEach(opt => {
            opt.selected = parseInt(opt.value) === current;
        });
    }

    // Material
    const materialInput = popup.querySelector('input[name="material"]');
    if (materialInput.dataset.currentMaterial === "true") {
        materialInput.checked = true;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('a[href^="#edit-reservation-"]').forEach(link => {
        link.addEventListener('click', () => {
            const popupId = link.getAttribute('href').substring(1);
            setTimeout(() => preselectEditReservationValues(popupId), 100);
        });
    });
});