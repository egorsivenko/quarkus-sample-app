package pragmasoft.k1teauth.oauth.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Serdeable
public class ConsentForm {

    private boolean userGaveConsent;

    @NotBlank
    private String callbackUrl;

    @Nullable
    private String clientName;

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

    @Nullable
    private String nonce;

    public ConsentForm() {}

    @Creator
    public ConsentForm(String callbackUrl, String clientName, String state, UUID userId,
                       String scopeNames, String scopeDescriptions,
                       String codeChallenge, String codeChallengeMethod, String nonce) {
        this.callbackUrl = callbackUrl;
        this.clientName = clientName;
        this.state = state;
        this.userId = userId;
        this.scopeNames = scopeNames;
        this.scopeDescriptions = scopeDescriptions;
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
        this.nonce = nonce;
    }

    public boolean userGaveConsent() {
        return userGaveConsent;
    }

    public void setUserGaveConsent(boolean userGaveConsent) {
        this.userGaveConsent = userGaveConsent;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
