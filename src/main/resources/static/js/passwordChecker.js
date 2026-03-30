// Custom JavaScript for Olympo Academy
// This file is loaded on every page (via footer partial) and contains shared UI helpers.

/*
document.querySelector(".cart-link").onclick = function() {
    document.querySelector(".cart-container") 
        .classList.toggle("show");
}*/

// Verifies that the password and confirmation match before allowing form submission
function checkPasswordMatch() {
    console.log("Comprobando contraseñas..."); // for debugging
    const pass1 = document.getElementById("password");
    const pass2 = document.getElementById("password2");
    const errorDiv = document.getElementById("passwordError");

    if (!pass1 || !pass2) return false; // for security

    if (pass1.value !== pass2.value) {
        errorDiv.style.display = "block";
        return false; // blocks form submission
    } else {
        errorDiv.style.display = "none";
        return true; // allows form submission
    }
}

// Initialize password validation when the DOM is fully loaded
document.addEventListener("DOMContentLoaded", function () {
    console.log("Inicializando validación de contraseñas..."); // for debugging
    const form = document.getElementById("registerForm");
    const pass1 = document.getElementById("password");
    const pass2 = document.getElementById("password2");

    // Validate passwords on input
    pass1.addEventListener("input", checkPasswordMatch);
    pass2.addEventListener("input", checkPasswordMatch);

    // Avoid form submission if passwords don't match, in case onsubmit is bypassed
    form.addEventListener("submit", function (event) {
        if (!checkPasswordMatch()) {
            event.preventDefault();  // blocks form submission if passwords don't match
        }
    });

});