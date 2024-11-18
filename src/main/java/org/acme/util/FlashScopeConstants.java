package org.acme.util;

public final class FlashScopeConstants {

    private FlashScopeConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ERROR = "error";
    public static final String RATE_LIMITED = "rateLimited";
    public static final String PASSWORDS_MATCH = "passwordsMatch";

    public static final String RATE_LIMITED_MESSAGE = "Rate limit exceeded. Please try again soon.";
    public static final String TURNSTILE_MESSAGE = "Turnstile verification failed.";
    public static final String EMAIL_ALREADY_REGISTERED = "Email address is already registered.";
    public static final String EMAIL_NOT_REGISTERED_MESSAGE = "Account with this email is not registered.";
    public static final String PASSWORDS_MATCH_MESSAGE = "Passwords must match.";
    public static final String INCORRECT_PASSWORD_MESSAGE = "Incorrect current password.";
    public static final String CLIENT_NAME_ALREADY_REGISTERED = "OAuth client name is already registered.";
}
