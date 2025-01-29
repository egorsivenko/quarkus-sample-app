package pragmasoft.k1teauth.user;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import pragmasoft.k1teauth.user.User.Role;
import pragmasoft.k1teauth.user.exception.EmailAlreadyTakenException;
import pragmasoft.k1teauth.user.exception.UserNotFoundException;
import pragmasoft.k1teauth.user.form.EditUserForm;

import java.util.List;
import java.util.UUID;

@Singleton
@Transactional
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

    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void create(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException(user.getEmail());
        }
        userRepository.save(user);
    }

    public void edit(EditUserForm form) {
        User user = getById(form.getId());
        String newEmail = form.getEmail();

        if (!user.getEmail().equals(newEmail) && existsByEmail(newEmail)) {
            throw new EmailAlreadyTakenException(newEmail);
        }
        user.setEmail(newEmail);
        user.setFullName(form.getFullName());
        user.setRole(Role.valueOf(form.getRole().toUpperCase()));

        userRepository.update(user);
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    public void changePassword(UUID id, String newPassword) {
        User user = getById(id);
        changePassword(user, newPassword);
    }

    public void changePassword(User user, String newPassword) {
        user.changePassword(newPassword);
        userRepository.update(user);
    }

    public void verifyUser(UUID id) {
        User user = getById(id);
        user.setVerified(true);
        userRepository.update(user);
    }
}
