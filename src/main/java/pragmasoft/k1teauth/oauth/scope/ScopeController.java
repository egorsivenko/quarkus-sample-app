package pragmasoft.k1teauth.oauth.scope;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.server.exceptions.NotFoundException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.oauth.scope.form.AddScopeForm;
import pragmasoft.k1teauth.oauth.scope.form.EditScopeForm;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Hidden
@Controller("/oauth2/scopes")
@Secured("ADMIN")
public class ScopeController {

    private final ScopeRepository scopeRepository;

    public ScopeController(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository;
    }

    @View("oauth/scopes")
    @Get(produces = MediaType.TEXT_HTML)
    public HttpResponse<?> scopes() {
        return HttpResponse.ok(Map.of("scopes", scopeRepository.findAll()));
    }

    @View("oauth/addScope")
    @Get(uri = "/new", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> addScopeTemplate() {
        return HttpResponse.ok(Collections.emptyMap());
    }

    @Post(uri = "/new", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public HttpResponse<?> addScope(@Valid @Body AddScopeForm form) {
        if (scopeRepository.existsById(form.getName())) {
            return HttpResponse.badRequest(new ModelAndView<>("oauth/addScope",
                    Map.of("errors", List.of("Scope with this name is already registered"))));
        }
        scopeRepository.save(form.mapToScope());
        return HttpResponse.seeOther(URI.create("/oauth2/scopes"));
    }

    @View("oauth/editScope")
    @Get(uri = "/edit", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> editScopeTemplate(@QueryValue String name) {
        Scope scope = scopeRepository.findById(name).orElseThrow(NotFoundException::new);
        return HttpResponse.ok(Map.of("scope", scope));
    }

    @Post(uri = "/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public HttpResponse<?> editScope(@Valid @Body EditScopeForm form) {
        if (form.isNameChanged()) {
            if (scopeRepository.existsById(form.getName())) {
                Scope scope = scopeRepository.findById(form.getPreviousName()).orElseThrow(NotFoundException::new);
                return HttpResponse.badRequest(new ModelAndView<>("oauth/editScope",
                        Map.of("scope", scope, "errors", List.of("Scope with this name is already registered"))));
            }
            scopeRepository.deleteById(form.getPreviousName());
            scopeRepository.save(form.mapToScope());
        }
        scopeRepository.update(form.mapToScope());
        return HttpResponse.seeOther(URI.create("/oauth2/scopes"));
    }

    @Post(uri = "/delete", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public HttpResponse<?> deleteScope(String name) {
        scopeRepository.deleteById(name);
        return HttpResponse.seeOther(URI.create("/oauth2/scopes"));
    }

    @Error(exception = ConstraintViolationException.class)
    public HttpResponse<?> handleError() {
        return HttpResponse.seeOther(URI.create("/oauth2/scopes"));
    }
}
