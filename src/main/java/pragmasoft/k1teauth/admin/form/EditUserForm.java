package pragmasoft.k1teauth.admin.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.Select;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pragmasoft.k1teauth.common.fetcher.UserRoleFetcher;
import pragmasoft.k1teauth.user.User;

import java.util.UUID;

@Serdeable
public record EditUserForm(
        @InputHidden @NotNull UUID id,
        @NotBlank @Size(min = 4, max = 50) String fullName,
        @InputEmail @Email @NotBlank @Size(min = 6, max = 50) String email,
        @Select(fetcher = UserRoleFetcher.class) @NotBlank String role
) {
    public static EditUserForm from(User user) {
        return new EditUserForm(user.getId(), user.getFullName(), user.getEmail(), user.getRole().toString());
    }
}