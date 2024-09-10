package org.acme.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.admin.request.EditUserRequest;
import org.acme.user.exception.EmailAlreadyTakenException;
import org.acme.user.exception.UserNotFoundException;
import org.acme.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException(user.getEmail());
        }
        userRepository.save(user);
    }

    public void edit(EditUserRequest request) {
        User user = getById(request.id());
        String newEmail = request.email();

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyTakenException(newEmail);
        }
        user.setEmail(newEmail);
        user.setFullName(request.fullName());
        user.setRole(UserRole.valueOf(request.role().toUpperCase()));

        userRepository.update(request.id(), user);
    }

    public void delete(UUID id) {
        userRepository.delete(id);
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
