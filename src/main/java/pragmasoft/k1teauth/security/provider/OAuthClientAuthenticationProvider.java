package pragmasoft.k1teauth.security.provider;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestExecutorAuthenticationProvider;
import jakarta.inject.Singleton;
import pragmasoft.k1teauth.oauth.client.OAuthClientRepository;

@Singleton
public class OAuthClientAuthenticationProvider<B> implements HttpRequestExecutorAuthenticationProvider<B> {

    private final OAuthClientRepository clientRepository;

    public OAuthClientAuthenticationProvider(OAuthClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public AuthenticationResponse authenticate(HttpRequest<B> requestContext,
                                               AuthenticationRequest<String, String> authRequest) {
        return clientRepository.findById(authRequest.getIdentity())
                .filter(client -> !client.isConfidential() || client.getClientSecret().equals(authRequest.getSecret()))
                .map(client -> AuthenticationResponse.success(authRequest.getIdentity()))
                .orElse(AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
    }
}
