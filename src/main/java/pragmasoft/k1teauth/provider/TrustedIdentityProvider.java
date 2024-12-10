package pragmasoft.k1teauth.provider;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.credential.PasswordCredential;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TrustedAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.util.EmailValidator;

@ApplicationScoped
public class TrustedIdentityProvider implements IdentityProvider<TrustedAuthenticationRequest> {

    private final UserService userService;

    public TrustedIdentityProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Class<TrustedAuthenticationRequest> getRequestType() {
        return TrustedAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(TrustedAuthenticationRequest request,
                                              AuthenticationRequestContext context) {
        return context.runBlocking(() -> {
            String principal = request.getPrincipal();

            if (EmailValidator.isValid(principal)) {
                User user = userService.getByEmail(principal);

                return QuarkusSecurityIdentity.builder()
                        .setPrincipal(new QuarkusPrincipal(principal))
                        .addCredential(new PasswordCredential(user.getPassword().toCharArray()))
                        .addRole(user.getRole().toString())
                        .build();
            }
            OAuthClient client = OAuthClient.findByClientIdOptional(principal)
                    .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

            return QuarkusSecurityIdentity.builder()
                    .setPrincipal(new QuarkusPrincipal(principal))
                    .addCredential(new PasswordCredential(client.clientSecret.toCharArray()))
                    .build();
        });
    }
}
