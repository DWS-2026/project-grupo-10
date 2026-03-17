// Optional custom JavaScript for Olympo Academy.
// This file exists to avoid 404 errors when templates reference /js/scripts.js.

document.querySelector(".cart-link").onclick = function(){
    document.querySelector(".cart-container")
        .classList.toggle("show");
}