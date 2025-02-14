package pragmasoft.k1teauth.oauth.dto;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@Introspected
public class AuthRequest {

    @QueryValue("client_id")
    private String clientId;

    @QueryValue("redirect_uri")
    private String redirectUri;

    @QueryValue("response_type")
    private String responseType;

    @QueryValue("code_challenge")
    @Nullable
    private String codeChallenge;

    @QueryValue("code_challenge_method")
    @Nullable
    private String codeChallengeMethod;

    @QueryValue("nonce")
    @Nullable
    private String nonce;

    @QueryValue("scope")
    private String scope;

    @QueryValue("state")
    private String state;

    public AuthRequest() {}

    @Creator
    public AuthRequest(String clientId, String redirectUri, String responseType,
                       String codeChallenge, String codeChallengeMethod,
                       String nonce, String scope, String state) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.responseType = responseType;
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
        this.nonce = nonce;
        this.scope = scope;
        this.state = state;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
