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
import pragmasoft.k1teauth.oauth.client.form.EditClientForm;
import pragmasoft.k1teauth.oauth.client.form.RegisterClientForm;
import pragmasoft.k1teauth.util.CsrfTokenValidator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        public static native TemplateInstance registerClient();

        public static native TemplateInstance editClient(OAuthClient client);
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
        client.callbackUrls = parseCallbackUrls(form.getCallbackUrls());

        Set<String> scopeSet = form.getScopes().isBlank()
                ? new HashSet<>()
                : new HashSet<>(Arrays.asList(form.getScopes().split(",")));
        scopeSet.add("openid");
        client.scopes = scopeSet;

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
            editClientTemplate(form.getClientId());
        }
        OAuthClient client = OAuthClient.findByClientIdOptional(form.getClientId()).orElseThrow();

        if (!client.name.equals(form.getClientName())
                && OAuthClient.findByNameOptional(form.getClientName()).isPresent()) {
            flash(ERROR, CLIENT_NAME_ALREADY_REGISTERED);
            editClientTemplate(form.getClientId());
        }
        client.name = form.getClientName();
        client.callbackUrls = parseCallbackUrls(form.getCallbackUrls());

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

    private Set<String> parseCallbackUrls(String callbackUrls) {
        return Arrays.stream(callbackUrls.split(","))
                .map(String::strip)
                .filter(url -> !url.isBlank())
                .collect(Collectors.toSet());
    }
}
