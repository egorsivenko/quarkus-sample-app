package org.acme.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.auth.exception.EmailAlreadyTakenException;
import org.acme.auth.request.RegisterRequest;
import org.acme.user.User;
import org.acme.user.repository.UserRepository;
import org.acme.user.UserRole;

import java.util.UUID;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository<User, UUID> userRepository;

    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyTakenException(request.email());
        }
        User user = new User(request.fullName(), request.email(), request.password(), UserRole.USER);
        userRepository.save(user);
    }
}
