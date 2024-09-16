package org.acme.auth.request;

public record ForgotPasswordRequest(String email, String recaptchaToken) {}
