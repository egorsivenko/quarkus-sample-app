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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.acme.util.FlashScopeConstants.CLIENT_NAME_ALREADY_REGISTERED;
import static org.acme.util.FlashScopeConstants.ERROR;

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
                               @RestForm String callbackUrl,
                               @RestForm String scopes) {
        if (OAuthClient.findByNameOptional(clientName).isPresent()) {
            flash(ERROR, CLIENT_NAME_ALREADY_REGISTERED);
            registerClientTemplate();
        }
        OAuthClient client = new OAuthClient();
        client.clientId = codeGenerator.generate(30);
        client.clientSecret = codeGenerator.generate(40);

        client.name = clientName;
        client.homepageUrl = homepageUrl;
        client.callbackUrl = callbackUrl;

        Set<String> scopeSet = scopes.isBlank()
                ? new HashSet<>()
                : new HashSet<>(Arrays.asList(scopes.split(",")));
        scopeSet.add("openid");
        client.scopes = scopeSet;

        String email = identityAssociation.getIdentity().getPrincipal().getName();
        client.user = userService.getByEmail(email);

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

        if (!client.name.equals(clientName) && OAuthClient.findByNameOptional(clientName).isPresent()) {
            flash(ERROR, CLIENT_NAME_ALREADY_REGISTERED);
            editClientTemplate(clientId);
        }
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
