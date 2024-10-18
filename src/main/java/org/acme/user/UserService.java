package org.acme.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.acme.admin.form.EditUserForm;
import org.acme.user.exception.EmailAlreadyTakenException;
import org.acme.user.exception.UserNotFoundException;
import org.acme.user.repository.PanacheUserRepository;

import java.util.List;
import java.util.UUID;

@Named("userService")
@ApplicationScoped
@Transactional
public class UserService {

    private final PanacheUserRepository userRepository;

    public UserService(PanacheUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> listAll() {
        return userRepository.listAll();
    }

    public User getById(UUID id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailOptional(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmailOptional(email)
                .isPresent();
    }

    public void create(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException(user.getEmail());
        }
        userRepository.persist(user);
    }

    public void edit(EditUserForm form) {
        User user = getById(form.getId());
        String newEmail = form.getEmail();

        if (!user.getEmail().equals(newEmail) && existsByEmail(newEmail)) {
            throw new EmailAlreadyTakenException(newEmail);
        }
        user.setEmail(newEmail);
        user.setFullName(form.getFullName());
        user.setRole(UserRole.valueOf(form.getRole().toUpperCase()));

        userRepository.persist(user);
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    public User verifyUser(UUID id) {
        User user = getById(id);
        user.setVerified(true);

        userRepository.persist(user);
        return user;
    }

    public boolean isUserAdmin(User user) {
        return user.getRole() == UserRole.ADMIN;
    }
}
