package pragmasoft.k1teauth.provider;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.user.exception.UserNotFoundException;
import pragmasoft.k1teauth.util.EmailValidator;

@ApplicationScoped
public class UsernamePasswordIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsernamePasswordIdentityProvider.class);

    private final UserService userService;

    public UsernamePasswordIdentityProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    @ActivateRequestContext
    public Uni<SecurityIdentity> authenticate(UsernamePasswordAuthenticationRequest request,
                                              AuthenticationRequestContext context) {
        return context.runBlocking(() -> {
            String username = request.getUsername();
            String password = String.valueOf(request.getPassword().getPassword());

            if (EmailValidator.isValid(username)) {
                try {
                    User user = userService.getByEmail(username);

                    if (!user.verifyPassword(password)) {
                        LOGGER.info("Login failure with email due to incorrect password `{}`", username);
                        throw new AuthenticationFailedException("Invalid credentials: password verification failed");
                    }
                    LOGGER.info("Successful login via email `{}`", username);
                    return QuarkusSecurityIdentity.builder()
                            .setPrincipal(new QuarkusPrincipal(username))
                            .addCredential(request.getPassword())
                            .addRole(user.getRole().toString())
                            .build();

                } catch (UserNotFoundException e) {
                    LOGGER.info("Login failure with non-existent email `{}`", username);
                    throw new AuthenticationFailedException("Invalid credentials");
                }
            }

            OAuthClient.findByClientIdAndSecret(username, password).orElseThrow(() -> {
                LOGGER.info("Login failure with client ID `{}`", username);
                return new AuthenticationFailedException("Invalid credentials");
            });
            LOGGER.info("Successful authentication via client ID `{}`", username);
            return QuarkusSecurityIdentity.builder()
                    .setPrincipal(new QuarkusPrincipal(username))
                    .addCredential(request.getPassword())
                    .build();
        });
    }
}
