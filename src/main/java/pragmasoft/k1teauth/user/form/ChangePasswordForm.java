package pragmasoft.k1teauth.user.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record ChangePasswordForm(
        @InputPassword @NotBlank @Size(min = 6, max = 50) String currentPassword,
        @InputPassword @NotBlank @Size(min = 6, max = 50) String newPassword,
        @InputPassword @NotBlank @Size(min = 6, max = 50) String confirmPassword
) {
}
