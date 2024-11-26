package pragmasoft.k1teauth.provider;

import io.quarkus.security.credential.PasswordCredential;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TrustedAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;

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
            User user = userService.getByEmail(request.getPrincipal());

            return QuarkusSecurityIdentity.builder()
                    .setPrincipal(new QuarkusPrincipal(user.getEmail()))
                    .addCredential(new PasswordCredential(user.getPassword().toCharArray()))
                    .addRole(user.getRole().toString())
                    .build();
        });
    }
}
