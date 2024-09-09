package org.acme.user.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    private static final String ID_MESSAGE = "User with ID '%s' not found.";
    private static final String EMAIL_MESSAGE = "User with email '%s' not found.";

    public UserNotFoundException(UUID id) {
        super(String.format(ID_MESSAGE, id));
    }

    public UserNotFoundException(String email) {
        super(String.format(EMAIL_MESSAGE, email));
    }
}
