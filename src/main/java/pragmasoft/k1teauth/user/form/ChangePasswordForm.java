package pragmasoft.k1teauth.user.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

import static pragmasoft.k1teauth.util.ValidationConstraints.PASSWORD_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.PASSWORD_SIZE_MESSAGE;

public class ChangePasswordForm {

    @RestForm
    @NotBlank(message = PASSWORD_NOT_BLANK_MESSAGE)
    @Size(min = 6, max = 50, message = PASSWORD_SIZE_MESSAGE)
    String currentPassword;

    @RestForm
    @NotBlank(message = PASSWORD_NOT_BLANK_MESSAGE)
    @Size(min = 6, max = 50, message = PASSWORD_SIZE_MESSAGE)
    String newPassword;

    @RestForm
    @NotBlank(message = PASSWORD_NOT_BLANK_MESSAGE)
    @Size(min = 6, max = 50, message = PASSWORD_SIZE_MESSAGE)
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
