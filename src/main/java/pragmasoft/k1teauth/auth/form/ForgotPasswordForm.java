package pragmasoft.k1teauth.auth.form;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public class ForgotPasswordForm {

    @NotBlank
    @Size(min = 6, max = 50)
    private String email;

    public ForgotPasswordForm() {}

    @Creator
    public ForgotPasswordForm(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
