package pragmasoft.k1teauth.admin.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

import java.util.UUID;

import static pragmasoft.k1teauth.util.ValidationConstraints.EMAIL_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.EMAIL_SIZE_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.FULL_NAME_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.FULL_NAME_SIZE_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.ROLE_NOT_BLANK_MESSAGE;

public class EditUserForm {

    @RestForm
    UUID id;

    @RestForm
    @NotBlank(message = FULL_NAME_NOT_BLANK_MESSAGE)
    @Size(min = 4, max = 50, message = FULL_NAME_SIZE_MESSAGE)
    String fullName;

    @RestForm
    @NotBlank(message = EMAIL_NOT_BLANK_MESSAGE)
    @Size(min = 6, max = 50, message = EMAIL_SIZE_MESSAGE)
    String email;

    @RestForm
    @NotBlank(message = ROLE_NOT_BLANK_MESSAGE)
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