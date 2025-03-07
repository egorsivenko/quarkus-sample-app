package pragmasoft.k1teauth.oauth.client.form;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.Select;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pragmasoft.k1teauth.common.fetcher.ScopesFetcher;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.oauth.scope.Scope;

import java.util.List;

@Serdeable
public record EditClientForm(
        @InputHidden @NotBlank String clientId,
        @NotBlank @Size(min = 3, max = 100) String clientName,
        @NotBlank @Size(max = 1000) String callbackUrls,
        @Select(fetcher = ScopesFetcher.class) @NotNull List<String> scopes,
        @InputHidden boolean isConfidential
) implements ClientFormData {
    public static EditClientForm from(OAuthClient client) {
        return new EditClientForm(
                client.getClientId(),
                client.getName(),
                String.join(", ", client.getCallbackUrls()),
                client.getScopes().stream().map(Scope::getName).toList(),
                client.isConfidential()
        );
    }
}
