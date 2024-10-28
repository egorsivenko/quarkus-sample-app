package org.acme.provider;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.user.User;
import org.acme.user.UserService;
import org.acme.user.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Uni<SecurityIdentity> authenticate(UsernamePasswordAuthenticationRequest request,
                                              AuthenticationRequestContext context) {
        return context.runBlocking(() -> {
            try {
                User user = userService.getByEmail(request.getUsername());
                String password = String.valueOf(request.getPassword().getPassword());

                if (!user.verifyPassword(password)) {
                    LOGGER.info("Login failure with email `{}` due to incorrect password", user.getEmail());
                    throw new AuthenticationFailedException("Invalid credentials: password verification failed.");
                }
                LOGGER.info("Successful login via email `{}`", user.getEmail());
                return QuarkusSecurityIdentity.builder()
                        .setPrincipal(new QuarkusPrincipal(request.getUsername()))
                        .addCredential(request.getPassword())
                        .addRole(user.getRole().toString())
                        .build();

            } catch (UserNotFoundException e) {
                LOGGER.info("Login failure with non-existent email `{}`", request.getUsername());
                throw new AuthenticationFailedException("Invalid credentials.", e);
            }
        });
    }
}
