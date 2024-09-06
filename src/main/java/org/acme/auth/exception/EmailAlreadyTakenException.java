package org.acme.auth.exception;

public class EmailAlreadyTakenException extends RuntimeException {

    private static final String MESSAGE = "Email '%s' is already taken.";

    public EmailAlreadyTakenException(String email) {
        super(String.format(MESSAGE, email));
    }
}
