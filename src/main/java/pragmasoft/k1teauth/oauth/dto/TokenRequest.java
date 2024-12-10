package pragmasoft.k1teauth.oauth.dto;

import jakarta.ws.rs.FormParam;

public class TokenRequest {

    @FormParam("grant_type")
    String grantType;

    @FormParam("code")
    String code;

    public String getGrantType() {
        return grantType;
    }

    public String getCode() {
        return code;
    }
}
