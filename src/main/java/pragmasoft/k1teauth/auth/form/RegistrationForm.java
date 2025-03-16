package pragmasoft.k1teauth.auth.form;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.InputPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pragmasoft.k1teauth.user.User;

@Serdeable
public record RegistrationForm(
        @NotBlank @Size(min = 4, max = 50) String fullName,
        @InputEmail @Email @NotBlank @Size(min = 6, max = 50) String email,
        @InputPassword @NotBlank @Size(min = 6, max = 50) String password,
        @InputPassword @NotBlank @Size(min = 6, max = 50) String confirmPassword,
        @InputHidden @Nullable String cfTurnstileResponse
) {
    public User mapToUser() {
        return new User(fullName, email, password);
    }
}
