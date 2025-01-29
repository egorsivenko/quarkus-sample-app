package pragmasoft.k1teauth.ratelimiter;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.annotation.RequestFilter;
import io.micronaut.http.annotation.ServerFilter;
import jakarta.validation.constraints.NotNull;

import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;

@ServerFilter(Filter.MATCH_ALL_PATTERN)
public class RateLimitFilter {

    private final RateLimiter rateLimiter;

    public RateLimitFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @RequestFilter
    public CompletableFuture<@Nullable HttpResponse<?>> filter(@NotNull HttpRequest<?> request) {
        String clientIp = getClientIpAddress(request);
        if (!rateLimiter.tryAcquire(clientIp)) {
            return CompletableFuture.completedFuture(HttpResponse.status(HttpStatus.TOO_MANY_REQUESTS));
        }
        return CompletableFuture.completedFuture(null);
    }

    private String getClientIpAddress(HttpRequest<?> request) {
        String xForwardedForHeader = request.getHeaders().findFirst("X-Forwarded-For").orElse(null);
        if (xForwardedForHeader == null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        return new StringTokenizer(xForwardedForHeader, ",").nextToken().strip();
    }
}
