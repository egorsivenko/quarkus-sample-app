package pragmasoft.k1teauth.auth.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

public class ForgotPasswordForm {

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
    String email;

    public String getEmail() {
        return email;
    }
}
