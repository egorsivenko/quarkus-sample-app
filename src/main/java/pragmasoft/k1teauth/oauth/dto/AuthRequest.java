package pragmasoft.k1teauth.oauth.dto;

import jakarta.ws.rs.QueryParam;

public class AuthRequest {

    @QueryParam("client_id")
    String clientId;

    @QueryParam("redirect_uri")
    String redirectUri;

    @QueryParam("response_type")
    String responseType;

    @QueryParam("code_challenge")
    String codeChallenge;

    @QueryParam("code_challenge_method")
    String codeChallengeMethod;

    @QueryParam("scope")
    String scope;

    @QueryParam("state")
    String state;

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public String getCodeChallengeMethod() {
        return codeChallengeMethod;
    }

    public String getScope() {
        return scope;
    }

    public String getState() {
        return state;
    }
}
