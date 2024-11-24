package org.acme.oauth.client;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import org.acme.oauth.CodeGenerator;
import org.acme.oauth.form.EditClientForm;
import org.acme.oauth.form.RegisterClientForm;
import org.acme.user.User;
import org.acme.user.UserService;
import org.acme.util.CsrfTokenValidator;
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
        return Templates.clients(user.getClients());
    }

    @GET
    @Path("/new")
    public TemplateInstance registerClientTemplate() {
        return Templates.registerClient();
    }

    @POST
    @Path("/new")
    @Transactional
    public void registerClient(@BeanParam @Valid RegisterClientForm form,
                               @CookieParam("csrf-token") Cookie csrfTokenCookie,
                               @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        if (validationFailed()) {
            registerClientTemplate();
        }
        if (OAuthClient.findByNameOptional(form.getClientName()).isPresent()) {
            flash(ERROR, CLIENT_NAME_ALREADY_REGISTERED);
            registerClientTemplate();
        }
        OAuthClient client = new OAuthClient();
        client.clientId = codeGenerator.generate(30);
        client.clientSecret = codeGenerator.generate(40);

        client.name = form.getClientName();
        client.homepageUrl = form.getHomepageUrl();
        client.callbackUrl = form.getCallbackUrl();

        Set<String> scopeSet = form.getScopes().isBlank()
                ? new HashSet<>()
                : new HashSet<>(Arrays.asList(form.getScopes().split(",")));
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
    public void editClient(@BeanParam @Valid EditClientForm form,
                           @CookieParam("csrf-token") Cookie csrfTokenCookie,
                           @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        if (validationFailed()) {
            registerClientTemplate();
        }
        OAuthClient client = OAuthClient.findByClientIdOptional(form.getClientId()).orElseThrow();

        if (!client.name.equals(form.getClientName())
                && OAuthClient.findByNameOptional(form.getClientName()).isPresent()) {
            flash(ERROR, CLIENT_NAME_ALREADY_REGISTERED);
            editClientTemplate(form.getClientId());
        }
        client.name = form.getClientName();
        client.homepageUrl = form.getHomepageUrl();
        client.callbackUrl = form.getCallbackUrl();

        clients();
    }

    @POST
    @Path("/delete")
    @Transactional
    public void deleteClient(@RestForm String clientId,
                             @CookieParam("csrf-token") Cookie csrfTokenCookie,
                             @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        OAuthClient.deleteByClientId(clientId);
        clients();
    }
}
