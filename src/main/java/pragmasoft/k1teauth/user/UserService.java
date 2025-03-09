package pragmasoft.k1teauth.user;

import io.micronaut.context.annotation.Property;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import pragmasoft.k1teauth.admin.form.EditUserForm;
import pragmasoft.k1teauth.user.User.Role;
import pragmasoft.k1teauth.user.exception.EmailAlreadyTakenException;
import pragmasoft.k1teauth.user.exception.UserNotFoundException;

import java.util.UUID;

@Singleton
@Transactional
public class UserService {

    @Property(name = "page.size", defaultValue = "-1")
    private int pageSize;
    private static final Sort SORT = Sort.of(Sort.Order.desc("createdAt"));

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> listAll(Pageable pageable) {
        return userRepository.findAll(Pageable.from(pageable.getNumber(), pageSize, SORT));
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
        return userRepository.findByEmail(email).isPresent();
    }

    public void create(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException(user.getEmail());
        }
        userRepository.save(user);
    }

    public void edit(EditUserForm form) {
        User user = getById(form.id());
        String newEmail = form.email();

        if (!user.getEmail().equals(newEmail) && existsByEmail(newEmail)) {
            throw new EmailAlreadyTakenException(newEmail);
        }
        user.setEmail(newEmail);
        user.setFullName(form.fullName());
        user.setRole(Role.valueOf(form.role().toUpperCase()));

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
