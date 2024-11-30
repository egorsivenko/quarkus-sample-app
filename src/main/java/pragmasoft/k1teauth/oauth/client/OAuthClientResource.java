package pragmasoft.k1teauth.oauth.client;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.RolesAllowed;
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
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import pragmasoft.k1teauth.oauth.CodeGenerator;
import pragmasoft.k1teauth.oauth.client.form.ClientForm;
import pragmasoft.k1teauth.oauth.scope.Scope;
import pragmasoft.k1teauth.util.CsrfTokenValidator;

import java.util.List;

import static pragmasoft.k1teauth.util.FlashScopeConstants.CLIENT_NAME_ALREADY_REGISTERED;
import static pragmasoft.k1teauth.util.FlashScopeConstants.ERROR;

@Path("/oauth2/clients")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@RolesAllowed("admin")
public class OAuthClientResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance clients(List<OAuthClient> clients);

        public static native TemplateInstance registerClient(List<Scope> scopes);

        public static native TemplateInstance editClient(OAuthClient client, List<Scope> scopes);
    }

    private final CodeGenerator codeGenerator;

    public OAuthClientResource(CodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    @GET
    @Path("/")
    public TemplateInstance clients() {
        return Templates.clients(OAuthClient.listAll());
    }

    @GET
    @Path("/new")
    public TemplateInstance registerClientTemplate() {
        return Templates.registerClient(Scope.listAll());
    }

    @POST
    @Path("/new")
    @Transactional
    public void registerClient(@BeanParam @Valid ClientForm form,
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

        form.assignToClient(client);

        client.persist();
        clients();
    }

    @GET
    @Path("/edit")
    public TemplateInstance editClientTemplate(@RestQuery String clientId) {
        return Templates.editClient(OAuthClient.findByClientIdOptional(clientId).orElseThrow(), Scope.listAll());
    }

    @POST
    @Path("/edit")
    @Transactional
    public void editClient(@BeanParam @Valid ClientForm form,
                           @CookieParam("csrf-token") Cookie csrfTokenCookie,
                           @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        if (validationFailed()) {
            editClientTemplate(form.getClientId());
        }
        OAuthClient client = OAuthClient.findByClientIdOptional(form.getClientId()).orElseThrow();

        if (!client.name.equals(form.getClientName())
                && OAuthClient.findByNameOptional(form.getClientName()).isPresent()) {
            flash(ERROR, CLIENT_NAME_ALREADY_REGISTERED);
            editClientTemplate(form.getClientId());
        }
        form.assignToClient(client);

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
