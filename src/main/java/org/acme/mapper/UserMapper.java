package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.auth.request.RegisterRequest;
import org.acme.user.User;

@ApplicationScoped
public class UserMapper {

    public User mapToUser(RegisterRequest request) {
        return new User(
                request.fullName(),
                request.email(),
                request.password()
        );
    }
}
