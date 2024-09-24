package org.acme.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.acme.admin.request.EditUserRequest;
import org.acme.user.exception.EmailAlreadyTakenException;
import org.acme.user.exception.IncorrectPasswordException;
import org.acme.user.exception.UserNotFoundException;
import org.acme.user.repository.UserRepository;
import org.acme.user.request.ChangePasswordRequest;

import java.util.List;
import java.util.UUID;

@Named("userService")
@ApplicationScoped
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getByEmail(email);

        if (!user.verifyPassword(request.currentPassword())) {
            throw new IncorrectPasswordException();
        }
        user.changePassword(request.newPassword());
    }

    public boolean isUserAdmin(User user) {
        return user.getRole() == UserRole.ADMIN;
    }
}
