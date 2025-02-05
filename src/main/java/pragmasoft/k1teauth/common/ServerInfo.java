package pragmasoft.k1teauth.common;

import io.micronaut.context.annotation.Value;
import io.netty.handler.codec.http.HttpScheme;
import jakarta.inject.Singleton;

@Singleton
public class ServerInfo {

    @Value("${host.name:}")
    private String hostName;

    @Value("${micronaut.server.context-path:}")
    private String contextPath;

    public String getBaseUrl() {
        return HttpScheme.HTTPS.name() + "://" + hostName + contextPath;
    }
}
