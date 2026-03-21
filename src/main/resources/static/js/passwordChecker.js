// Custom JavaScript for Olympo Academy
// This file is loaded on every page (via footer partial) and contains shared UI helpers.

/*
document.querySelector(".cart-link").onclick = function() {
    document.querySelector(".cart-container") 
        .classList.toggle("show");
}*/

// Comprueba si las contraseñas coinciden
function checkPasswordMatch() {
    console.log("Comprobando contraseñas..."); // para depuración
    const pass1 = document.getElementById("password");
    const pass2 = document.getElementById("password2");
    const errorDiv = document.getElementById("passwordError");

    if (!pass1 || !pass2) return false; // por seguridad

    if (pass1.value !== pass2.value) {
        errorDiv.style.display = "block";
        return false; // bloquea el envío
    } else {
        errorDiv.style.display = "none";
        return true; // permite el envío
    }
}

// Cuando el DOM está listo
document.addEventListener("DOMContentLoaded", function () {
    console.log("Inicializando validación de contraseñas..."); // para depuración
    const form = document.getElementById("registerForm");
    const pass1 = document.getElementById("password");
    const pass2 = document.getElementById("password2");

    // Validación en tiempo real
    pass1.addEventListener("input", checkPasswordMatch);
    pass2.addEventListener("input", checkPasswordMatch);

    // Evitar envío desde JS por si el onsubmit no se dispara
    form.addEventListener("submit", function (event) {
        if (!checkPasswordMatch()) {
            event.preventDefault();  // bloquea el formulario
        }
    });

});