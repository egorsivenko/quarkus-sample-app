package pragmasoft.k1teauth.oauth.consent.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Serdeable
public record RevokeConsentForm(@InputHidden @NotNull UUID id) {
}
