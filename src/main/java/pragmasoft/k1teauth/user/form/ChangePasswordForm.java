package pragmasoft.k1teauth.user.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

public class ChangePasswordForm {

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String currentPassword;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String newPassword;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String confirmPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
