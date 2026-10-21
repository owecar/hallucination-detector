"use strict";

document.addEventListener("DOMContentLoaded", initializeDbadmin);

function initializeDbadmin() {
    setupDbadminNavigation();
}

function setupDbadminNavigation() {
    const cards = document.querySelectorAll(".dbadmin-card");
    cards.forEach(function (card) {
        card.addEventListener("click", handleDbadminNavigation);
    });
}

function handleDbadminNavigation(event) {
    const title = event.currentTarget.querySelector("h2");
    const status = document.getElementById("dbadmin-status");
    if (title && status) {
        status.textContent = "Opening " + title.textContent.trim() + "…";
    }
}
