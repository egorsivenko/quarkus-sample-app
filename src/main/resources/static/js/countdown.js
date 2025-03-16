function initializeCountdown() {
    const resendButton = document.getElementById('resendButton');
    const countdownLabel = document.getElementById('countdown');

    if (!resendButton || !countdownLabel) return;

    let cooldownTime = 60;
    resendButton.disabled = true;
    countdownLabel.innerHTML = "Resend the email after: " + cooldownTime + " seconds.";

    let countdownInterval = setInterval(() => {
        cooldownTime--;
        countdownLabel.innerHTML = "Resend the email after: " + cooldownTime + " seconds.";

        if (cooldownTime <= 0) {
            clearInterval(countdownInterval);
            resendButton.disabled = false;
            countdownLabel.innerHTML = "You can resend the email.";
        }
    }, 1000);
}

document.addEventListener('DOMContentLoaded', initializeCountdown);
document.addEventListener('turbo:render', initializeCountdown);