package pragmasoft.k1teauth.auth.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.InputPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Serdeable
public record ResetPasswordForm(
        @InputHidden @NotNull UUID userId,
        @InputPassword @NotBlank @Size(min = 6, max = 50) String newPassword,
        @InputPassword @NotBlank @Size(min = 6, max = 50) String confirmPassword
) {
    public ResetPasswordForm(UUID userId) {
        this(userId, "", "");
    }
}
