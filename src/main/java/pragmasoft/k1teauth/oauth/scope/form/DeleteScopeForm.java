package pragmasoft.k1teauth.oauth.scope.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record DeleteScopeForm(@InputHidden @NotBlank String name) {
}
