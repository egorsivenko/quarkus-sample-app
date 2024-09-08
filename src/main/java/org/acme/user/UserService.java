package org.acme.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository<User, UUID> userRepository;

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public boolean verifyPassword(String email, String password) {
        User user = getByEmail(email);
        return user.verifyPassword(password);
    }

    public void changePassword(String email, String newPassword) {
        User user = getByEmail(email);
        user.changePassword(newPassword);
    }
}
