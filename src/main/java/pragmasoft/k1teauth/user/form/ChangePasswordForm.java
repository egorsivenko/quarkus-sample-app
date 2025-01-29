package pragmasoft.k1teauth.user.form;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public class ChangePasswordForm {

    @NotBlank
    @Size(min = 6, max = 50)
    private String currentPassword;

    @NotBlank
    @Size(min = 6, max = 50)
    private String newPassword;

    @NotBlank
    @Size(min = 6, max = 50)
    private String confirmPassword;

    public ChangePasswordForm() {}

    @Creator
    public ChangePasswordForm(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
