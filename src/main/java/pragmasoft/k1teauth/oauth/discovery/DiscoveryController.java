package pragmasoft.k1teauth.oauth.discovery;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import pragmasoft.k1teauth.common.ServerInfo;
import pragmasoft.k1teauth.oauth.TokenRequestHandler;
import pragmasoft.k1teauth.oauth.util.CodeChallengeUtil;

import java.util.Set;

@Controller("/.well-known")
@Secured(SecurityRule.IS_ANONYMOUS)
public class DiscoveryController {

    private final ServerInfo serverInfo;

    public DiscoveryController(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Get(uri = "/openid-configuration", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<?> oidcDiscovery() {
        Metadata metadata = new Metadata();
        String baseUrl = serverInfo.getBaseUrl();

        metadata.setIssuer(baseUrl);
        metadata.setAuthorizationEndpoint(baseUrl + "/oauth2/auth");
        metadata.setTokenEndpoint(baseUrl + "/oauth2/token");
        metadata.setTokenEndpointAuthMethodsSupported(Set.of("client_secret_basic"));
        metadata.setUserInfoEndpoint(baseUrl + "/oauth2/userinfo");

        metadata.setScopesSupported(Set.of("openid"));
        metadata.setResponseTypesSupported(Set.of("code"));
        metadata.setResponseModesSupported(Set.of("query"));

        metadata.setGrantTypesSupported(TokenRequestHandler.getAvailableAuthorizationGrantTypes());
        metadata.setCodeChallengeMethodsSupported(CodeChallengeUtil.getAvailableCodeChallengeMethods());
        metadata.setUiLocalesSupported(Set.of("en-US"));

        return HttpResponse.ok(metadata);
    }
}
