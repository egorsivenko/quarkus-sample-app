package pragmasoft.k1teauth.oauth.scope;

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
import pragmasoft.k1teauth.oauth.scope.form.ScopeForm;
import pragmasoft.k1teauth.util.CsrfTokenValidator;

import java.util.List;

import static pragmasoft.k1teauth.util.FlashScopeConstants.ERROR;
import static pragmasoft.k1teauth.util.FlashScopeConstants.SCOPE_ALREADY_REGISTERED;

@Path("/oauth2/scopes")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@RolesAllowed("admin")
public class ScopeResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance scopes(List<Scope> scopes);

        public static native TemplateInstance addScope();

        public static native TemplateInstance editScope(Scope scope);
    }

    @GET
    @Path("/")
    public TemplateInstance scopes() {
        return Templates.scopes(Scope.listAll());
    }

    @GET
    @Path("/new")
    public TemplateInstance addScopeTemplate() {
        return Templates.addScope();
    }

    @POST
    @Path("/new")
    @Transactional
    public void addScope(@BeanParam @Valid ScopeForm form,
                         @CookieParam("csrf-token") Cookie csrfTokenCookie,
                         @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        if (validationFailed()) {
            addScopeTemplate();
        }
        if (Scope.findByName(form.getName()).isPresent()) {
            flash(ERROR, SCOPE_ALREADY_REGISTERED);
            addScopeTemplate();
        }
        form.mapToScope().persist();
        scopes();
    }

    @GET
    @Path("/edit")
    public TemplateInstance editScopeTemplate(@RestQuery String name) {
        return Templates.editScope(Scope.findByName(name).orElseThrow());
    }

    @POST
    @Path("/edit")
    @Transactional
    public void editScope(@BeanParam @Valid ScopeForm form,
                          @CookieParam("csrf-token") Cookie csrfTokenCookie,
                          @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        if (validationFailed()) {
            editScopeTemplate(form.getPreviousName());
        }
        if (form.isNameChanged() && Scope.findByName(form.getName()).isPresent()) {
            flash(ERROR, SCOPE_ALREADY_REGISTERED);
            editScopeTemplate(form.getPreviousName());
        }
        Scope.deleteById(form.getPreviousName());

        form.mapToScope().persist();
        scopes();
    }

    @POST
    @Path("/delete")
    @Transactional
    public void deleteScope(@RestForm String name,
                            @CookieParam("csrf-token") Cookie csrfTokenCookie,
                            @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        Scope.deleteById(name);
        scopes();
    }
}
