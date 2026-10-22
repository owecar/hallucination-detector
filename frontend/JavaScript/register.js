const registerForm = document.getElementById("register-form");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const confirmPasswordInput = document.getElementById("confirm-password");
const registerError = document.getElementById("register-error");


function validateRegistration(event) {
    registerError.textContent = "";

    if (usernameInput.value.trim() === "") {
        event.preventDefault();
        registerError.textContent = "Please enter a username.";
        usernameInput.focus();
    } else if (passwordInput.value !== confirmPasswordInput.value) {
        event.preventDefault();
        registerError.textContent = "Passwords do not match.";
        confirmPasswordInput.focus();
    }
}

registerForm.addEventListener("submit", validateRegistration);

function clearRegistrationError() {
    registerError.textContent = "";
}

usernameInput.addEventListener("input", clearRegistrationError);
passwordInput.addEventListener("input", clearRegistrationError);
confirmPasswordInput.addEventListener("input", clearRegistrationError);

const togglePasswordsButton = document.getElementById("toggle-passwords");

function toggleRegistrationPasswords() {
    const showPasswords = passwordInput.type === "password";
    const inputType = showPasswords ? "text" : "password";

    passwordInput.type = inputType;
    confirmPasswordInput.type = inputType;

    togglePasswordsButton.textContent =
        showPasswords ? "Hide passwords" : "Show passwords";

    togglePasswordsButton.setAttribute(
        "aria-pressed", String(showPasswords)
    );
}

togglePasswordsButton.addEventListener("click", toggleRegistrationPasswords);

