function showError(message){
    const error = document.getElementById("errorMessage");
    error.textContent = message;
    error.hidden = false;
}

function clearError(){
    const error = document.getElementById("errorMessage");
    error.textContent = "";
    error.hidden = true;
}

function showLoading(){
    document.getElementById("loadingMessage").hidden = false;
    document.getElementById("submitButton").disabled = true;
}

function hideLoading(){
    document.getElementById("loadingMessage").hidden = true;
    document.getElementById("submitButton").disabled = false;
}

function validateQuestion(event) {
    const question = document.getElementById("question").value.trim();

    clearError();

    if (question.length === 0) {
        event.preventDefault();
        showError("Please enter a question before submitting.");
        return;
    }

    showLoading();
    // showloading will allow the browser to submit the form to Spring Boot.
}

function resetQuestionForm() {
    clearError();
    hideLoading();
}

function setupQuestionPage() {
    const form = document.getElementById("questionForm");
    const resetButton = document.getElementById("resetButton");
    const question = document.getElementById("question");

    if (!form) return;

    form.addEventListener("submit", validateQuestion);
    resetButton.addEventListener("click", resetQuestionForm);
    question.addEventListener("input", clearError);
}

document.addEventListener("DOMContentLoaded", setupQuestionPage);