package org.acme.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.acme.admin.form.EditUserForm;
import org.acme.user.exception.EmailAlreadyTakenException;
import org.acme.user.exception.UserNotFoundException;
import org.acme.user.repository.UserRepository;

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

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException(user.getEmail());
        }
        userRepository.save(user);
    }

    public void edit(EditUserForm form) {
        User user = getById(form.getId());
        String newEmail = form.getEmail();

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyTakenException(newEmail);
        }
        user.setEmail(newEmail);
        user.setFullName(form.getFullName());
        user.setRole(UserRole.valueOf(form.getRole().toUpperCase()));

        userRepository.update(form.getId(), user);
    }

    public void delete(UUID id) {
        userRepository.delete(id);
    }

    public boolean isUserAdmin(User user) {
        return user.getRole() == UserRole.ADMIN;
    }
}
