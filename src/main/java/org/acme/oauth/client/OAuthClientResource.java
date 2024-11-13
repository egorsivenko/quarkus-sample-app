package org.acme.oauth.client;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.oauth.CodeGenerator;
import org.acme.user.User;
import org.acme.user.UserService;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.Set;

@Path("/oauth2/clients")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@Authenticated
public class OAuthClientResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance clients(Set<OAuthClient> clients);

        public static native TemplateInstance registerClient();

        public static native TemplateInstance editClient(OAuthClient client);
    }

    private final UserService userService;
    private final CurrentIdentityAssociation identityAssociation;
    private final CodeGenerator codeGenerator;

    public OAuthClientResource(UserService userService,
                               CurrentIdentityAssociation identityAssociation,
                               CodeGenerator codeGenerator) {
        this.userService = userService;
        this.identityAssociation = identityAssociation;
        this.codeGenerator = codeGenerator;
    }

    @GET
    @Path("/")
    public TemplateInstance clients() {
        String email = identityAssociation.getIdentity().getPrincipal().getName();
        User user = userService.getByEmail(email);

        var clients = user.getClients();
        return Templates.clients(clients);
    }

    @GET
    @Path("/new")
    public TemplateInstance registerClientTemplate() {
        return Templates.registerClient();
    }

    @POST
    @Path("/new")
    @Transactional
    public void registerClient(@RestForm String clientName,
                               @RestForm String homepageUrl,
                               @RestForm String callbackUrl) {
        String email = identityAssociation.getIdentity().getPrincipal().getName();
        User user = userService.getByEmail(email);

        OAuthClient client = new OAuthClient();
        client.clientId = codeGenerator.generate(30);
        client.clientSecret = codeGenerator.generate(40);

        client.name = clientName;
        client.homepageUrl = homepageUrl;
        client.callbackUrl = callbackUrl;
        client.user = user;

        client.persist();
        clients();
    }

    @GET
    @Path("/edit")
    public TemplateInstance editClientTemplate(@RestQuery String clientId) {
        return Templates.editClient(OAuthClient.findByClientIdOptional(clientId).orElseThrow());
    }

    @POST
    @Path("/edit")
    @Transactional
    public void editClient(@RestForm String clientId,
                           @RestForm String clientName,
                           @RestForm String homepageUrl,
                           @RestForm String callbackUrl) {
        OAuthClient client = OAuthClient.findByClientIdOptional(clientId).orElseThrow();
        client.name = clientName;
        client.homepageUrl = homepageUrl;
        client.callbackUrl = callbackUrl;

        clients();
    }

    @POST
    @Path("/delete")
    @Transactional
    public void deleteClient(@RestForm String clientId) {
        OAuthClient.deleteByClientId(clientId);
        clients();
    }
}
