package pragmasoft.k1teauth.user.form;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Serdeable
public class EditUserForm {

    @NotNull
    private UUID id;

    @NotBlank
    @Size(min = 4, max = 50)
    private String fullName;

    @NotBlank
    @Size(min = 6, max = 50)
    private String email;

    @NotBlank
    private String role;

    public EditUserForm() {}

    @Creator
    public EditUserForm(UUID id, String fullName, String email, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}