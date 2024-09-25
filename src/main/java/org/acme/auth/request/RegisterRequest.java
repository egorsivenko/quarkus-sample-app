package org.acme.auth.request;

import org.acme.user.User;

public record RegisterRequest(
        String fullName,
        String email,
        String password,
        String token
) {
    public User mapToUser() {
        return new User(fullName, email, password);
    }
}
