package pragmasoft.k1teauth.auth.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record LoginForm(
        @InputEmail @Email @NotBlank @Size(max = 50) String username,
        @InputPassword @NotBlank @Size(max = 50) String password
) {
}
