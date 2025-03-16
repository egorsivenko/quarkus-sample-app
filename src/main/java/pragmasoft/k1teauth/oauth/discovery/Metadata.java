package pragmasoft.k1teauth.oauth.discovery;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.util.Set;

@Serdeable
public class Metadata {

    private String issuer;
    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    @JsonProperty("token_endpoint_auth_methods_supported")
    private Set<String> tokenEndpointAuthMethodsSupported;
    @JsonProperty("userinfo_endpoint")
    private String userInfoEndpoint;
    @JsonProperty("scopes_supported")
    private Set<String> scopesSupported;
    @JsonProperty("response_types_supported")
    private Set<String> responseTypesSupported;
    @JsonProperty("response_modes_supported")
    private Set<String> responseModesSupported;
    @JsonProperty("grant_types_supported")
    private Set<String> grantTypesSupported;
    @JsonProperty("code_challenge_methods_supported")
    private Set<String> codeChallengeMethodsSupported;
    @JsonProperty("ui_locales_supported")
    private Set<String> uiLocalesSupported;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public Set<String> getTokenEndpointAuthMethodsSupported() {
        return tokenEndpointAuthMethodsSupported;
    }

    public void setTokenEndpointAuthMethodsSupported(Set<String> tokenEndpointAuthMethodsSupported) {
        this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
    }

    public String getUserInfoEndpoint() {
        return userInfoEndpoint;
    }

    public void setUserInfoEndpoint(String userInfoEndpoint) {
        this.userInfoEndpoint = userInfoEndpoint;
    }

    public Set<String> getScopesSupported() {
        return scopesSupported;
    }

    public void setScopesSupported(Set<String> scopesSupported) {
        this.scopesSupported = scopesSupported;
    }

    public Set<String> getResponseTypesSupported() {
        return responseTypesSupported;
    }

    public void setResponseTypesSupported(Set<String> responseTypesSupported) {
        this.responseTypesSupported = responseTypesSupported;
    }

    public Set<String> getResponseModesSupported() {
        return responseModesSupported;
    }

    public void setResponseModesSupported(Set<String> responseModesSupported) {
        this.responseModesSupported = responseModesSupported;
    }

    public Set<String> getGrantTypesSupported() {
        return grantTypesSupported;
    }

    public void setGrantTypesSupported(Set<String> grantTypesSupported) {
        this.grantTypesSupported = grantTypesSupported;
    }

    public Set<String> getCodeChallengeMethodsSupported() {
        return codeChallengeMethodsSupported;
    }

    public void setCodeChallengeMethodsSupported(Set<String> codeChallengeMethodsSupported) {
        this.codeChallengeMethodsSupported = codeChallengeMethodsSupported;
    }

    public Set<String> getUiLocalesSupported() {
        return uiLocalesSupported;
    }

    public void setUiLocalesSupported(Set<String> uiLocalesSupported) {
        this.uiLocalesSupported = uiLocalesSupported;
    }
}
