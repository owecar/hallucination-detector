function setupDeleteButtons() {
    const deleteForms = document.querySelectorAll(".delete-form");

    deleteForms.forEach(function(form) {
        form.addEventListener("submit", confirmDeletion);
    });
}
function confirmDeletion(event) {
    const confirmed = window.confirm(
        "Are you sure you want to delete this question? This cannot be undone."
    );
    if (!confirmed) {
        event.preventDefault();
    }
}
