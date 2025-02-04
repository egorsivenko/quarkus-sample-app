package pragmasoft.k1teauth.admin.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

import java.util.UUID;

public class EditUserForm {

    @RestForm
    UUID id;

    @RestForm
    @NotBlank
    @Size(min = 4, max = 50)
    String fullName;

    @RestForm
    @NotBlank
    @Size(min = 6, max = 50)
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