const password = document.getElementById('password');
const confirmPassword = document.getElementById('confirm-password');
const submitButton = document.getElementById('submit-button');
const invalidHelper = document.getElementById('invalid-helper-confirm');

function validatePasswords() {
    if (password.value && confirmPassword.value) {
        if (password.value === confirmPassword.value) {
            submitButton.disabled = false;
            invalidHelper.style.display = 'none';
            confirmPassword.removeAttribute('aria-invalid');
        } else {
            submitButton.disabled = true;
            invalidHelper.style.display = 'block';
            confirmPassword.setAttribute('aria-invalid', 'true');
        }
    }
}

password.addEventListener('input', validatePasswords);
confirmPassword.addEventListener('input', validatePasswords);