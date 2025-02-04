package pragmasoft.k1teauth.auth.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;
import pragmasoft.k1teauth.user.User;

public class RegistrationForm {

    @RestForm
    @NotBlank
    @Size(min = 4, max = 50)
    String fullName;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String email;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String password;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String confirmPassword;

    public User mapToUser() {
        return new User(fullName, email, password);
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
