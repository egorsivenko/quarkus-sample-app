package pragmasoft.k1teauth.security.jwt;

import com.nimbusds.jwt.proc.BadJWTException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import pragmasoft.k1teauth.common.dto.ErrorResponse;

@Singleton
@Requires(classes = {BadJWTException.class, ExceptionHandler.class})
public class BadJWTExceptionHandler implements ExceptionHandler<BadJWTException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, BadJWTException exception) {
        return HttpResponse.badRequest(new ErrorResponse(exception.getMessage()));
    }
}
