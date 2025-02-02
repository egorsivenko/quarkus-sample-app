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
    private String clientName;

    @NotNull
    private String callbackUrl;

    @Nullable
    private String state;

    @Nullable
    private UUID userId;

    @Nullable
    private String scopeNames;

    @Nullable
    private String scopeDescriptions;

    @Nullable
    private String codeChallenge;

    @Nullable
    private String codeChallengeMethod;

    public ConsentForm() {}

    @Creator
    public ConsentForm(String clientName, String callbackUrl, String state, UUID userId,
                       String scopeNames, String scopeDescriptions,
                       String codeChallenge, String codeChallengeMethod) {
        this.clientName = clientName;
        this.callbackUrl = callbackUrl;
        this.state = state;
        this.userId = userId;
        this.scopeNames = scopeNames;
        this.scopeDescriptions = scopeDescriptions;
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
    }

    public boolean userGaveConsent() {
        return userGaveConsent;
    }

    public void setUserGaveConsent(boolean userGaveConsent) {
        this.userGaveConsent = userGaveConsent;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public String getScopeNames() {
        return scopeNames;
    }

    public void setScopeNames(String scopeNames) {
        this.scopeNames = scopeNames;
    }

    public String getScopeDescriptions() {
        return scopeDescriptions;
    }

    public void setScopeDescriptions(String scopeDescriptions) {
        this.scopeDescriptions = scopeDescriptions;
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
