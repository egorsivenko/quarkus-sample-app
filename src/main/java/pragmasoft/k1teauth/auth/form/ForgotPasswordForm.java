package pragmasoft.k1teauth.auth.form;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputHidden;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record ForgotPasswordForm(
        @InputEmail @Email @NotBlank @Size(min = 6, max = 50) String email,
        @InputHidden @Nullable String cfTurnstileResponse
) {
}
