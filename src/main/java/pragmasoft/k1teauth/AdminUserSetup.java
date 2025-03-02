package pragmasoft.k1teauth;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserRole;
import pragmasoft.k1teauth.user.UserService;
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