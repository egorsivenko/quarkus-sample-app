package pragmasoft.k1teauth.oauth.client.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record DeleteClientForm(@InputHidden @NotBlank String clientId) {
}
