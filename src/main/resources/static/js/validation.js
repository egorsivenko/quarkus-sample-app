function initializePasswordValidation() {
    const password = document.querySelector('#password, #newPassword');
    const confirmPassword = document.getElementById('confirmPassword');
    const submitButton = document.querySelector('input[type=submit]');

    if (!password || !confirmPassword || !submitButton) return;
    if (document.getElementById('invalidHelper')) return;

    const invalidHelper = document.createElement('small');
    invalidHelper.id = 'invalidHelper';
    invalidHelper.textContent = 'Passwords don’t match';
    invalidHelper.style.display = 'none';

    confirmPassword.insertAdjacentElement("afterend", invalidHelper);

    function validatePasswords() {
        if (password.value && confirmPassword.value) {
            if (password.value === confirmPassword.value) {
                submitButton.disabled = false;
                invalidHelper.style.display = 'none';
                confirmPassword.removeAttribute('aria-invalid');
                confirmPassword.removeAttribute('aria-describedby');
            } else {
                submitButton.disabled = true;
                invalidHelper.style.display = 'block';
                confirmPassword.setAttribute('aria-invalid', 'true');
                confirmPassword.setAttribute('aria-describedby', 'invalidHelper');
            }
        }
    }

    password.addEventListener('input', validatePasswords);
    confirmPassword.addEventListener('input', validatePasswords);
}

document.addEventListener('DOMContentLoaded', initializePasswordValidation);
document.addEventListener('turbo:render', initializePasswordValidation);