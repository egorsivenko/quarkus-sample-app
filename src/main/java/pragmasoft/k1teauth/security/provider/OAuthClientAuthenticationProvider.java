package pragmasoft.k1teauth.security.provider;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestExecutorAuthenticationProvider;
import jakarta.inject.Singleton;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.oauth.client.OAuthClientRepository;

import java.util.Optional;

@Singleton
public class OAuthClientAuthenticationProvider<B> implements HttpRequestExecutorAuthenticationProvider<B> {

    private final OAuthClientRepository clientRepository;

    public OAuthClientAuthenticationProvider(OAuthClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public AuthenticationResponse authenticate(HttpRequest<B> requestContext,
                                               AuthenticationRequest<String, String> authRequest) {
        Optional<OAuthClient> clientOptional = clientRepository
                .findByClientIdAndClientSecret(authRequest.getIdentity(), authRequest.getSecret());
        return clientOptional.isPresent()
                ? AuthenticationResponse.success(authRequest.getIdentity())
                : AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
    }
}
