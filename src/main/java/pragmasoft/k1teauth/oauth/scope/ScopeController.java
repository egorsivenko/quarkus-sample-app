package pragmasoft.k1teauth.oauth.scope;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.server.exceptions.NotFoundException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.messages.Message;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.common.jte.JteTemplateRenderer;
import pragmasoft.k1teauth.oauth.scope.form.AddScopeForm;
import pragmasoft.k1teauth.oauth.scope.form.EditScopeForm;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Hidden
@Controller("/oauth2/scopes")
@Secured("ADMIN")
public class ScopeController {

    private static final String SCOPES_PATH = "/oauth2/scopes";
    private static final String ADD_SCOPE_PATH = "/oauth2/scopes/new";
    private static final String ADD_SCOPE_TEMPLATE = "oauth/addScope.jte";
    private static final String EDIT_SCOPE_PATH = "/oauth2/scopes/edit";
    private static final String EDIT_SCOPE_TEMPLATE = "oauth/editScope.jte";

    private final ScopeRepository scopeRepository;
    private final FormGenerator formGenerator;
    private final JteTemplateRenderer jteTemplateRenderer;

    public ScopeController(ScopeRepository scopeRepository,
                           FormGenerator formGenerator,
                           JteTemplateRenderer jteTemplateRenderer) {
        this.scopeRepository = scopeRepository;
        this.formGenerator = formGenerator;
        this.jteTemplateRenderer = jteTemplateRenderer;
    }

    @Get(produces = MediaType.TEXT_HTML)
    public String scopes() {
        return jteTemplateRenderer.render("oauth/scopes.jte",
                Map.of("scopes", scopeRepository.findAll(), "formGenerator", formGenerator));
    }

    @Get(uri = "/new", produces = MediaType.TEXT_HTML)
    public String addScopeTemplate() {
        return jteTemplateRenderer.render(ADD_SCOPE_TEMPLATE,
                Map.of("form", formGenerator.generate(ADD_SCOPE_PATH, AddScopeForm.class)));
    }

    @Post(uri = "/new", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> addScope(@Valid @Body AddScopeForm form) {
        if (scopeRepository.existsById(form.name())) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(ADD_SCOPE_TEMPLATE,
                    Map.of("form", formGenerator.generate(ADD_SCOPE_PATH, form),
                            "errors", List.of(Message.of("Scope with this name is already registered")))));
        }
        scopeRepository.save(form.mapToScope());
        return HttpResponse.seeOther(URI.create(SCOPES_PATH));
    }

    @Get(uri = "/edit", produces = MediaType.TEXT_HTML)
    public String editScopeTemplate(@QueryValue String name) {
        Scope scope = scopeRepository.findById(name).orElseThrow(NotFoundException::new);
        return jteTemplateRenderer.render(EDIT_SCOPE_TEMPLATE,
                Map.of("form", formGenerator.generate(EDIT_SCOPE_PATH, EditScopeForm.from(scope))));
    }

    @Post(uri = "/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> editScope(@Valid @Body EditScopeForm form) {
        if (form.hasNameChanged()) {
            if (scopeRepository.existsById(form.name())) {
                return HttpResponse.badRequest(jteTemplateRenderer.render(EDIT_SCOPE_TEMPLATE,
                        Map.of("form", formGenerator.generate(EDIT_SCOPE_PATH, form),
                                "errors", List.of(Message.of("Scope with this name is already registered")))));
            }
            scopeRepository.deleteById(form.previousName());
            scopeRepository.save(form.mapToScope());
        }
        scopeRepository.update(form.mapToScope());
        return HttpResponse.seeOther(URI.create(SCOPES_PATH));
    }

    @Post(uri = "/delete", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<Void> deleteScope(String name) {
        scopeRepository.deleteById(name);
        return HttpResponse.seeOther(URI.create(SCOPES_PATH));
    }

    @Error(exception = ConstraintViolationException.class)
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse<String> handleError(HttpRequest<?> request, ConstraintViolationException ex) {
        if (request.getPath().equals(ADD_SCOPE_PATH)) {
            Optional<AddScopeForm> formOptional = request.getBody(AddScopeForm.class);
            if (formOptional.isPresent()) {
                return HttpResponse.unprocessableEntity().body(jteTemplateRenderer.render(ADD_SCOPE_TEMPLATE,
                        Map.of("form", formGenerator.generate(ADD_SCOPE_PATH, formOptional.get(), ex))));
            }
        } else if (request.getPath().equals(EDIT_SCOPE_PATH)) {
            Optional<EditScopeForm> formOptional = request.getBody(EditScopeForm.class);
            if (formOptional.isPresent()) {
                return HttpResponse.unprocessableEntity().body(jteTemplateRenderer.render(EDIT_SCOPE_TEMPLATE,
                        Map.of("form", formGenerator.generate(EDIT_SCOPE_PATH, formOptional.get(), ex))));
            }
        }
        return HttpResponse.serverError();
    }
}
