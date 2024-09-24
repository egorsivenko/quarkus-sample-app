package org.acme;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.acme.user.User;
import org.acme.user.UserRole;
import org.acme.user.UserService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AdminUserSetup {

    @ConfigProperty(name = "admin.name")
    String adminName;

    @ConfigProperty(name = "admin.email")
    String adminEmail;

    @ConfigProperty(name = "admin.password")
    String adminPassword;

    private final UserService userService;

    public AdminUserSetup(UserService userService) {
        this.userService = userService;
    }

    public void createAdminUser(@Observes StartupEvent event) {
        userService.create(new User(adminName, adminEmail, adminPassword, UserRole.ADMIN, true));
    }
}