const passwordInput = document.getElementById('password');
const togglePasswordButton = document.getElementById('toggle-password');

function togglePasswordVisibility() {
    const showPassword = passwordInput.type === "password";

    passwordInput.type = showPassword ? "text" : "password";
    togglePasswordButton.textContent = showPassword ? "Hide Password" : "Show Password";

    togglePasswordButton.setAttribute('aria-pressed', String(showPassword));
}

togglePasswordButton.addEventListener('click', togglePasswordVisibility);

const loginForm = document.getElementById("login-form");
const usernameInput = document.getElementById("username");
const loginError = document.getElementById("login-error");

function validateLoginForm(event) {
    if (usernameInput.value.trim() === "") {
        event.preventDefault();
        loginError.textContent = "Please enter your username.";
        usernameInput.focus();
        return;
    }

    if (passwordInput.value.trim() === "") {
        event.preventDefault();
        loginError.textContent = "Please enter your password.";
        passwordInput.focus();
        return;
    }
}

function clearLoginError() {
    loginError.textContent = "";
}

usernameInput.addEventListener("input", clearLoginError);
