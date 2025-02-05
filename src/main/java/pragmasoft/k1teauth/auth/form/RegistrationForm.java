package pragmasoft.k1teauth.auth.form;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pragmasoft.k1teauth.user.User;

@Serdeable
public class RegistrationForm {

    @NotBlank
    @Size(min = 4, max = 50)
    private String fullName;

    @NotBlank
    @Size(min = 6, max = 50)
    private String email;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    @NotBlank
    @Size(min = 6, max = 50)
    private String confirmPassword;

    @Nullable
    private String cfTurnstileResponse;

    public RegistrationForm() {}

    @Creator
    public RegistrationForm(String fullName, String email, String password, String confirmPassword, String cfTurnstileResponse) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.cfTurnstileResponse = cfTurnstileResponse;
    }

    public User mapToUser() {
        return new User(fullName, email, password);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCfTurnstileResponse() {
        return cfTurnstileResponse;
    }

    public void setCfTurnstileResponse(String cfTurnstileResponse) {
        this.cfTurnstileResponse = cfTurnstileResponse;
    }
}
