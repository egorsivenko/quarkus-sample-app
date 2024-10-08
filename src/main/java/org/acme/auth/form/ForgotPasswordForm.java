package org.acme.auth.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

import static org.acme.util.ValidationConstraints.EMAIL_NOT_BLANK_MESSAGE;
import static org.acme.util.ValidationConstraints.EMAIL_SIZE_MESSAGE;

public class ForgotPasswordForm {

    @RestForm
    @NotBlank(message = EMAIL_NOT_BLANK_MESSAGE)
    @Size(min = 6, max = 50, message = EMAIL_SIZE_MESSAGE)
    String email;

    public String getEmail() {
        return email;
    }
}
