package pragmasoft.k1teauth.oauth.client.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.oauth.scope.Scope;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static pragmasoft.k1teauth.util.ValidationConstraints.CALLBACK_URL_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.CLIENT_NAME_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.CLIENT_NAME_SIZE_MESSAGE;

public class ClientForm {

    @RestForm
    String clientId;

    @RestForm
    @NotBlank(message = CLIENT_NAME_NOT_BLANK_MESSAGE)
    @Size(min = 3, max = 100, message = CLIENT_NAME_SIZE_MESSAGE)
    String clientName;

    @RestForm
    @NotBlank(message = CALLBACK_URL_NOT_BLANK_MESSAGE)
    String callbackUrls;

    @RestForm
    @NotNull
    List<String> scopes;

    public void assignToClient(OAuthClient client) {
        client.name = clientName;
        client.callbackUrls = parseCallbackUrls();
        client.scopes = mapScopes();
    }

    private Set<String> parseCallbackUrls() {
        return Arrays.stream(callbackUrls.split(","))
                .map(String::strip)
                .filter(url -> !url.isBlank())
                .collect(Collectors.toSet());
    }

    private Set<Scope> mapScopes() {
        return scopes.stream()
                .map(scope -> Scope.findByName(scope).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }
}
