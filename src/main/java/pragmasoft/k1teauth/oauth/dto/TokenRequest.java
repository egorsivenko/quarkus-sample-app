package pragmasoft.k1teauth.oauth.dto;

import jakarta.ws.rs.FormParam;

public class TokenRequest {

    @FormParam("grant_type")
    String grantType;

    @FormParam("code")
    String code;

    @FormParam("code_verifier")
    String codeVerifier;

    public String getGrantType() {
        return grantType;
    }

    public String getCode() {
        return code;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }
}
