package pragmasoft.k1teauth.auth.form;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public class ForgotPasswordForm {

    @NotBlank
    @Size(min = 6, max = 50)
    private String email;

    @Nullable
    private String cfTurnstileResponse;

    public ForgotPasswordForm() {}

    @Creator
    public ForgotPasswordForm(String email, String cfTurnstileResponse) {
        this.email = email;
        this.cfTurnstileResponse = cfTurnstileResponse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCfTurnstileResponse() {
        return cfTurnstileResponse;
    }

    public void setCfTurnstileResponse(String cfTurnstileResponse) {
        this.cfTurnstileResponse = cfTurnstileResponse;
    }
}
