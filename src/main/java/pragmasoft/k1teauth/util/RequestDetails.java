package pragmasoft.k1teauth.util;

import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Context;

import java.util.StringTokenizer;

@RequestScoped
public class RequestDetails {

    private final HttpServerRequest request;

    public RequestDetails(@Context HttpServerRequest request) {
        this.request = request;
    }

    public String getClientIpAddress() {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.remoteAddress().hostAddress();
        }
        return new StringTokenizer(xForwardedForHeader, ",").nextToken().strip();
    }
}
