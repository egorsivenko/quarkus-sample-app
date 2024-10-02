package org.acme.auth;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/auth")
@Produces(MediaType.TEXT_HTML)
public class LoginResource {

    @CheckedTemplate
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance login(String siteKey, boolean error);
    }

    @ConfigProperty(name = "turnstile.site.key")
    String siteKey;

    @GET
    @Path("/login")
    public TemplateInstance login(@RestQuery boolean error) {
        return Templates.login(siteKey, error);
    }
}
