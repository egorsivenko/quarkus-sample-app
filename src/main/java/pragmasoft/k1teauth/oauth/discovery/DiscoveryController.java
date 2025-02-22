package pragmasoft.k1teauth.oauth.discovery;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import pragmasoft.k1teauth.oauth.TokenRequestHandler;
import pragmasoft.k1teauth.oauth.util.CodeChallengeUtil;

import java.util.Set;

@Tag(name = "OpenID Connect Discovery")
@Controller("/.well-known")
@Secured(SecurityRule.IS_ANONYMOUS)
public class DiscoveryController {

    @Property(name = "server.url")
    private String serverUrl;

    @Operation(summary = "Authorization Server Metadata")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Metadata.class)
            )
    )
    @Get(uri = "/openid-configuration", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<?> oidcDiscovery() {
        Metadata metadata = new Metadata();

        metadata.setIssuer(serverUrl);
        metadata.setAuthorizationEndpoint(serverUrl + "/oauth2/auth");
        metadata.setTokenEndpoint(serverUrl + "/oauth2/token");
        metadata.setTokenEndpointAuthMethodsSupported(Set.of("client_secret_basic"));
        metadata.setUserInfoEndpoint(serverUrl + "/oauth2/userinfo");

        metadata.setScopesSupported(Set.of("openid"));
        metadata.setResponseTypesSupported(Set.of("code"));
        metadata.setResponseModesSupported(Set.of("query"));

        metadata.setGrantTypesSupported(TokenRequestHandler.getAvailableAuthorizationGrantTypes());
        metadata.setCodeChallengeMethodsSupported(CodeChallengeUtil.getAvailableCodeChallengeMethods());
        metadata.setUiLocalesSupported(Set.of("en-US"));

        return HttpResponse.ok(metadata);
    }
}
