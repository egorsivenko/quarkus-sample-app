package pragmasoft.k1teauth.oauth.dto;

import jakarta.ws.rs.FormParam;

public class TokenRequest {

    @FormParam("grant_type")
    String grantType;

    @FormParam("code")
    String code;

    @FormParam("client_id")
    String clientId;

    @FormParam("client_secret")
    String clientSecret;

    public String getGrantType() {
        return grantType;
    }

    public String getCode() {
        return code;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
