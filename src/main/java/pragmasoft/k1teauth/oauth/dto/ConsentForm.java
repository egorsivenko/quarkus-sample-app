package pragmasoft.k1teauth.oauth.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Serdeable
public class ConsentForm {

    private boolean userGaveConsent;

    @Nullable
    private String clientId;

    @NotNull
    private String callbackUrl;

    @Nullable
    private String state;

    @Nullable
    private UUID userId;

    @Nullable
    private String scopes;

    @Nullable
    private String codeChallenge;

    @Nullable
    private String codeChallengeMethod;

    public ConsentForm() {}

    @Creator
    public ConsentForm(boolean userGaveConsent, String clientId, String callbackUrl, String state,
                       UUID userId, String scopes, String codeChallenge, String codeChallengeMethod) {
        this.userGaveConsent = userGaveConsent;
        this.clientId = clientId;
        this.callbackUrl = callbackUrl;
        this.state = state;
        this.userId = userId;
        this.scopes = scopes;
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
    }

    public boolean userGaveConsent() {
        return userGaveConsent;
    }

    public void setUserGaveConsent(boolean userGaveConsent) {
        this.userGaveConsent = userGaveConsent;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public void setCodeChallenge(String codeChallenge) {
        this.codeChallenge = codeChallenge;
    }

    public String getCodeChallengeMethod() {
        return codeChallengeMethod;
    }

    public void setCodeChallengeMethod(String codeChallengeMethod) {
        this.codeChallengeMethod = codeChallengeMethod;
    }
}
