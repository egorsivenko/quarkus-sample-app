package pragmasoft.k1teauth;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.inject.Singleton;

@Singleton
@OpenAPIDefinition(
        info = @Info(
                title = "k1te-auth",
                description = "The OAuth 2.0 and OpenID Connect 1.0 Authorization Server",
                version = "1.0"
        )
)
@SecurityScheme(
        name = "openId",
        type = SecuritySchemeType.OPENIDCONNECT,
        openIdConnectUrl = "/.well-known/openid-configuration"
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}