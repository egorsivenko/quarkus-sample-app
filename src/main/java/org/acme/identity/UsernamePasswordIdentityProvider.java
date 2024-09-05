package org.acme.identity;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.user.User;
import org.acme.user.UserRepository;

import java.util.UUID;

@ApplicationScoped
public class UsernamePasswordIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    @Inject
    UserRepository<User, UUID> userRepository;

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(UsernamePasswordAuthenticationRequest request,
                                              AuthenticationRequestContext authenticationRequestContext) {
        User user = userRepository.findByEmail(request.getUsername()).orElse(null);
        String password = String.valueOf(request.getPassword().getPassword());

        if (user != null && user.verifyPassword(password)) {
            return Uni.createFrom().item(QuarkusSecurityIdentity.builder()
                    .setPrincipal(new QuarkusPrincipal(request.getUsername()))
                    .addCredential(request.getPassword())
                    .addRole(user.getRole())
                    .build());
        }
        throw new AuthenticationFailedException("Invalid credentials");
    }
}
