package org.acme.util;

public final class ValidationConstraints {

    private ValidationConstraints() {
        throw new IllegalStateException("Utility class");
    }

    public static final String FULL_NAME_SIZE_MESSAGE = "Full name length must be between {min} and {max} characters.";
    public static final String EMAIL_SIZE_MESSAGE = "Email length must be between {min} and {max} characters.";
    public static final String PASSWORD_SIZE_MESSAGE = "Password length must be between {min} and {max} characters.";
}
