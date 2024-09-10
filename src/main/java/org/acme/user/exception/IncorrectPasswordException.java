package org.acme.user.exception;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException() {
        super("The provided password is incorrect.");
    }
}
