package pragmasoft.k1teauth.oauth.scope.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputUrl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pragmasoft.k1teauth.oauth.scope.Scope;

@Serdeable
public record AddScopeForm(
        @NotBlank @Size(min = 3, max = 255) String name,
        @NotBlank @Size(min = 3, max = 255) String description,
        @InputUrl @NotBlank @Size(max = 255) String audience
) {
    public Scope mapToScope() {
        return new Scope(name, description, audience);
    }
}
