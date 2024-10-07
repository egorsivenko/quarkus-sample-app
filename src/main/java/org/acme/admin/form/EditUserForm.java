package org.acme.admin.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

import java.util.UUID;

import static org.acme.util.ValidationConstraints.EMAIL_SIZE_MESSAGE;
import static org.acme.util.ValidationConstraints.FULL_NAME_SIZE_MESSAGE;

public class EditUserForm {

    @RestForm
    UUID id;

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
    String role;

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}