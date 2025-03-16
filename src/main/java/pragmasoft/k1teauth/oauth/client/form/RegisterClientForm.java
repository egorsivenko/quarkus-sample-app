package pragmasoft.k1teauth.oauth.client.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputCheckbox;
import io.micronaut.views.fields.annotations.Select;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pragmasoft.k1teauth.common.fetcher.ScopesFetcher;

import java.util.List;

@Serdeable
public record RegisterClientForm(
        @NotBlank @Size(min = 3, max = 100) String clientName,
        @NotBlank @Size(max = 1000) String callbackUrls,
        @Select(fetcher = ScopesFetcher.class) @NotNull List<String> scopes,
        @InputCheckbox boolean isConfidential
) implements ClientFormData {
}
