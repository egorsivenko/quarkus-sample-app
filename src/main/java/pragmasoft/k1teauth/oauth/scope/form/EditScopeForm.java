package pragmasoft.k1teauth.oauth.scope.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.InputUrl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pragmasoft.k1teauth.oauth.scope.Scope;

import java.util.Objects;

@Serdeable
public record EditScopeForm(
        @NotBlank @Size(min = 3, max = 255) String name,
        @NotBlank @Size(min = 3, max = 255) String description,
        @InputUrl @NotBlank @Size(max = 255) String audience,
        @InputHidden @NotBlank String previousName
) {
    public static EditScopeForm from(Scope scope) {
        return new EditScopeForm(scope.getName(),
                scope.getDescription(), scope.getAudience(), scope.getName());
    }

    public Scope mapToScope() {
        return new Scope(name, description, audience);
    }

    public boolean hasNameChanged() {
        return !Objects.equals(name, previousName);
    }
}
