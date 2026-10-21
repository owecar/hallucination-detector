
function toggleEvidence(event) {
    const button = event.currentTarget;
    const details = button.nextElementSibling;

    const isExpanded =
        button.getAttribute("aria-expanded") === "true";

    details.hidden = isExpanded;
    button.setAttribute("aria-expanded", String(!isExpanded));

    button.textContent = isExpanded
        ? "Show Evidence"
        : "Hide Evidence";
}

function setupEvidenceButtons() {
    const buttons =
        document.querySelectorAll(".evidence-toggle");

    buttons.forEach(function(button) {
        button.addEventListener("click", toggleEvidence);
    });
}

document.addEventListener(
    "DOMContentLoaded",
    setupEvidenceButtons
);
