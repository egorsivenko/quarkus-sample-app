package org.acme.oauth.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

import static org.acme.util.ValidationConstraints.EMAIL_NOT_BLANK_MESSAGE;
import static org.acme.util.ValidationConstraints.EMAIL_SIZE_MESSAGE;
import static org.acme.util.ValidationConstraints.PASSWORD_NOT_BLANK_MESSAGE;
import static org.acme.util.ValidationConstraints.PASSWORD_SIZE_MESSAGE;

public class SignInForm {

    @RestForm
    @NotBlank(message = EMAIL_NOT_BLANK_MESSAGE)
    @Size(min = 6, max = 50, message = EMAIL_SIZE_MESSAGE)
    String email;

    @RestForm
    @NotBlank(message = PASSWORD_NOT_BLANK_MESSAGE)
    @Size(min = 6, max = 50, message = PASSWORD_SIZE_MESSAGE)
    String password;

    @RestForm
    @NotNull
    String clientId;

    @RestForm
    @NotNull
    String clientName;

    @RestForm
    @NotNull
    String callbackUrl;

    @RestForm
    @NotNull
    String state;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getState() {
        return state;
    }
}
