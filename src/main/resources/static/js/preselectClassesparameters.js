// Function to pre-select values in edit class form
function preselectEditClassValues(popupId) {
    const popup = document.getElementById(popupId);
    if (!popup) {
        console.log('Popup not found:', popupId);
        return;
    }

    // Pre-select difficulty
    const difficultySelect = popup.querySelector('select[name="difficulty"]');
    if (difficultySelect) {
        const currentValues = difficultySelect.getAttribute('data-current-values') || '';
        const values = currentValues.split(',').map(v => v.trim()).filter(v => v.length > 0);
        console.log('Difficulty - Current values:', values);
        Array.from(difficultySelect.options).forEach(option => {
            const optionValue = option.value.trim();
            option.selected = values.includes(optionValue);
        });
    }

    // Pre-select days
    const daySelect = popup.querySelector('select[name="days"]');
    if (daySelect) {
        const currentValues = daySelect.getAttribute('data-current-values') || '';
        const values = currentValues.split(',').map(v => v.trim()).filter(v => v.length > 0);
        console.log('Days - Current values:', values);
        Array.from(daySelect.options).forEach(option => {
            const optionValue = option.value.trim();
            option.selected = values.includes(optionValue);
        });
    }

    // Pre-select start times
    const startTimeSelect = popup.querySelector('select[name="startTime"]');
    if (startTimeSelect) {
        const currentValues = startTimeSelect.getAttribute('data-current-values') || '';
        const values = currentValues.split(',').map(v => v.trim()).filter(v => v.length > 0);
        console.log('Start times - Current values:', values);
        Array.from(startTimeSelect.options).forEach(option => {
            option.selected = values.includes(option.value);
        });
    }

    // Pre-select duration
    const durationSelect = popup.querySelector('select[name="durationRAW"]');
    if (durationSelect) {
        const currentDuration = durationSelect.getAttribute('data-current-duration');
        if (currentDuration) {
            const durationMinutes = parseInt(currentDuration);
            let durationValue = '1h'; // default
            if (durationMinutes === 90) durationValue = '1:30h';
            else if (durationMinutes === 120) durationValue = '2h';

            Array.from(durationSelect.options).forEach(option => {
                option.selected = option.value === durationValue;
            });
        }
    }

    // Pre-select facility (if uncommented in HTML)
    const facilitySelect = popup.querySelector('select[name="facility"]');
    if (facilitySelect) {
        const currentFacilityId = facilitySelect.getAttribute('data-current-facility');
        if (currentFacilityId) {
            Array.from(facilitySelect.options).forEach(option => {
                option.selected = option.value === currentFacilityId;
            });
        }
    }
}

// Add click event listeners to edit class links
document.addEventListener('DOMContentLoaded', function () {
    const editLinks = document.querySelectorAll('a[href^="#edit-class-"]');
    editLinks.forEach(link => {
        link.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            const popupId = href.substring(1); // Remove the #
            // Small delay to ensure popup is visible
            setTimeout(() => preselectEditClassValues(popupId), 100);
        });
    });
});