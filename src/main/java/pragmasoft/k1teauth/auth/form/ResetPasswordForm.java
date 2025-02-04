package pragmasoft.k1teauth.auth.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

import java.util.UUID;

public class ResetPasswordForm {

    @RestForm
    UUID userId;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String password;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String confirmPassword;

    public UUID getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
