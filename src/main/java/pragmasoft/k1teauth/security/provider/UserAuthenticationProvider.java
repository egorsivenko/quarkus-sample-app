package pragmasoft.k1teauth.security.provider;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestExecutorAuthenticationProvider;
import jakarta.inject.Singleton;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserRepository;

import java.util.Optional;
import java.util.Set;

@Singleton
public class UserAuthenticationProvider<B> implements HttpRequestExecutorAuthenticationProvider<B> {

    private final UserRepository userRepository;

    public UserAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticationResponse authenticate(HttpRequest<B> requestContext,
                                               AuthenticationRequest<String, String> authRequest) {
        Optional<User> userOptional = userRepository.findByEmail(authRequest.getIdentity());
        return userOptional.isPresent() && userOptional.get().verifyPassword(authRequest.getSecret())
                ? AuthenticationResponse.success(authRequest.getIdentity(), Set.of(userOptional.get().getRole().name()))
                : AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
    }
}
