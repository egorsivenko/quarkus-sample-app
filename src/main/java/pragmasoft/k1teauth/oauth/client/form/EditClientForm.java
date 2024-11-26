package pragmasoft.k1teauth.oauth.client.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.jboss.resteasy.reactive.RestForm;

import static pragmasoft.k1teauth.util.ValidationConstraints.CALLBACK_URL_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.CLIENT_NAME_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.CLIENT_NAME_SIZE_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.HOMEPAGE_URL_NOT_BLANK_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.URL_FORMAT_MESSAGE;
import static pragmasoft.k1teauth.util.ValidationConstraints.URL_MAX_LENGTH_MESSAGE;

public class EditClientForm {

    @RestForm
    String clientId;

    @RestForm
    @NotBlank(message = CLIENT_NAME_NOT_BLANK_MESSAGE)
    @Size(min = 3, max = 100, message = CLIENT_NAME_SIZE_MESSAGE)
    String clientName;

    @RestForm
    @NotBlank(message = HOMEPAGE_URL_NOT_BLANK_MESSAGE)
    @URL(message = URL_FORMAT_MESSAGE)
    @Size(max = 255, message = URL_MAX_LENGTH_MESSAGE)
    String homepageUrl;

    @RestForm
    @NotBlank(message = CALLBACK_URL_NOT_BLANK_MESSAGE)
    @URL(message = URL_FORMAT_MESSAGE)
    @Size(max = 255, message = URL_MAX_LENGTH_MESSAGE)
    String callbackUrl;

    public String getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }
}
