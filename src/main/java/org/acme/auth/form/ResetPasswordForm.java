package org.acme.auth.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

import java.util.UUID;

import static org.acme.util.ValidationConstraints.PASSWORD_SIZE_MESSAGE;

public class ResetPasswordForm {

    @RestForm
    UUID userId;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50, message = PASSWORD_SIZE_MESSAGE)
    String password;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50, message = PASSWORD_SIZE_MESSAGE)
    String confirmPassword;

    public UUID getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
