package org.acme.auth.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.acme.user.User;
import org.jboss.resteasy.reactive.RestForm;

import static org.acme.util.ValidationConstraints.EMAIL_SIZE_MESSAGE;
import static org.acme.util.ValidationConstraints.FULL_NAME_SIZE_MESSAGE;
import static org.acme.util.ValidationConstraints.PASSWORD_SIZE_MESSAGE;

public class RegistrationForm {

    @RestForm
    @NotBlank
    @Size(min = 4, max = 50, message = FULL_NAME_SIZE_MESSAGE)
    String fullName;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50, message = EMAIL_SIZE_MESSAGE)
    String email;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50, message = PASSWORD_SIZE_MESSAGE)
    String password;

    public User mapToUser() {
        return new User(fullName, email, password);
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
