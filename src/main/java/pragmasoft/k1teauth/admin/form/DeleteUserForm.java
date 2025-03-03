package pragmasoft.k1teauth.admin.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Serdeable
public record DeleteUserForm(@InputHidden @NotNull UUID id) {
}
