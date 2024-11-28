package pragmasoft.k1teauth.oauth.client.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

import static pragmasoft.k1teauth.util.ValidationConstraints.CALLBACK_URL_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.CLIENT_NAME_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.CLIENT_NAME_SIZE_MESSAGE;

public class RegisterClientForm {

    @RestForm
    @NotBlank(message = CLIENT_NAME_NOT_BLANK_MESSAGE)
    @Size(min = 3, max = 100, message = CLIENT_NAME_SIZE_MESSAGE)
    String clientName;

    @RestForm
    @NotBlank(message = CALLBACK_URL_NOT_BLANK_MESSAGE)
    String callbackUrls;

    @RestForm
    @NotNull
    String scopes;

    public String getClientName() {
        return clientName;
    }

    public String getCallbackUrls() {
        return callbackUrls;
    }

    public String getScopes() {
        return scopes;
    }
}
