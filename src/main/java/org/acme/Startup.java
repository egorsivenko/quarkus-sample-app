package org.acme;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.acme.user.User;
import org.acme.user.UserRepository;
import org.acme.user.UserRole;

import java.util.UUID;

@ApplicationScoped
public class Startup {

    @Inject
    UserRepository<User, UUID> userRepository;

    public void saveAdminUser(@Observes StartupEvent event) {
        userRepository.save(new User("Egor", "egor@gmail.com", "password", UserRole.ADMIN));
    }
}
