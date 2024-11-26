package pragmasoft.k1teauth.oauth.form;

import jakarta.validation.constraints.NotNull;
import org.jboss.resteasy.reactive.RestForm;

import java.util.UUID;

public class ConsentForm {

    @RestForm
    boolean userGaveConsent;

    @RestForm
    @NotNull
    String clientId;

    @RestForm
    @NotNull
    String callbackUrl;

    @RestForm
    @NotNull
    String state;

    @RestForm
    @NotNull
    UUID userId;

    public boolean userGaveConsent() {
        return userGaveConsent;
    }

    public String getClientId() {
        return clientId;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getState() {
        return state;
    }

    public UUID getUserId() {
        return userId;
    }
}
