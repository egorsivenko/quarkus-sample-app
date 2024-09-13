package org.acme;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.acme.user.User;
import org.acme.user.UserRole;
import org.acme.user.UserService;

@ApplicationScoped
public class Startup {

    @Inject
    UserService userService;

    public void saveAdminUser(@Observes StartupEvent event) {
        userService.create(new User("Egor", "egor@gmail.com", "password", UserRole.ADMIN, true));
    }

    public void saveRegularUser(@Observes StartupEvent event) {
        userService.create(new User("John", "john@gmail.com", "password", UserRole.USER, true));
    }
}
