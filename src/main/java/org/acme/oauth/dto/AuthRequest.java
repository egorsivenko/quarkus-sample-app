package org.acme.oauth.dto;

import jakarta.ws.rs.QueryParam;

public class AuthRequest {

    @QueryParam("client_id")
    String clientId;

    @QueryParam("redirect_uri")
    String redirectUri;

    @QueryParam("response_type")
    String responseType;

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

    public String getScope() {
        return scope;
    }

    public String getState() {
        return state;
    }
}
