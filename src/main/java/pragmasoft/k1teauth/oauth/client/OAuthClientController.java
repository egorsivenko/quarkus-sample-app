package pragmasoft.k1teauth.oauth.client;

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
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.common.generator.CodeGenerator;
import pragmasoft.k1teauth.oauth.client.form.EditClientForm;
import pragmasoft.k1teauth.oauth.client.form.RegisterClientForm;
import pragmasoft.k1teauth.oauth.scope.Scope;
import pragmasoft.k1teauth.oauth.scope.ScopeRepository;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller("/oauth2/clients")
@Secured("ADMIN")
public class OAuthClientController {

    private final OAuthClientRepository clientRepository;
    private final ScopeRepository scopeRepository;

    public OAuthClientController(OAuthClientRepository clientRepository,
                                 ScopeRepository scopeRepository) {
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
    }

    @View("oauth/clients")
    @Get(produces = MediaType.TEXT_HTML)
    public HttpResponse<?> clients() {
        return HttpResponse.ok(Map.of("clients", clientRepository.findAll()));
    }

    @View("oauth/registerClient")
    @Get(uri = "/new", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> registerClientTemplate() {
        return HttpResponse.ok(Map.of("scopes", scopeRepository.findAll()));
    }

    @Post(uri = "/new", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public HttpResponse<?> registerClient(@Valid @Body RegisterClientForm form) {
        if (clientRepository.findByName(form.getClientName()).isPresent()) {
            return HttpResponse.badRequest(new ModelAndView<>("oauth/registerClient",
                    Map.of("scopes", scopeRepository.findAll(),
                            "errors", List.of("OAuth client name is already registered"))));
        }
        OAuthClient client = new OAuthClient();
        client.setClientId(CodeGenerator.generate(30));
        client.setClientSecret(CodeGenerator.generate(40));
        assignFormDataToClient(client, form);

        clientRepository.save(client);
        return HttpResponse.seeOther(URI.create("/oauth2/clients"));
    }

    @View("oauth/editClient")
    @Get(uri = "/edit", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> editClientTemplate(@QueryValue String clientId) {
        OAuthClient client = clientRepository.findById(clientId).orElseThrow(NotFoundException::new);
        return HttpResponse.ok(Map.of("client", client, "scopes", scopeRepository.findAll()));
    }

    @Post(uri = "/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public HttpResponse<?> editClient(@Valid @Body EditClientForm form) {
        OAuthClient client = clientRepository.findById(form.getClientId()).orElseThrow(NotFoundException::new);
        if (!client.getName().equals(form.getClientName())
                && clientRepository.findByName(form.getClientName()).isPresent()) {
            return HttpResponse.badRequest(new ModelAndView<>("oauth/editClient",
                    Map.of("client", client, "scopes", scopeRepository.findAll(),
                            "errors", List.of("OAuth client name is already registered"))));
        }
        assignFormDataToClient(client, form);

        clientRepository.update(client);
        return HttpResponse.seeOther(URI.create("/oauth2/clients"));
    }

    @Post(uri = "/delete", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public HttpResponse<?> deleteClient(String clientId) {
        clientRepository.deleteById(clientId);
        return HttpResponse.seeOther(URI.create("/oauth2/clients"));
    }

    @Error(exception = ConstraintViolationException.class)
    public HttpResponse<?> handleError() {
        return HttpResponse.seeOther(URI.create("/oauth2/clients"));
    }

    private void assignFormDataToClient(OAuthClient client, RegisterClientForm form) {
        client.setName(form.getClientName());
        client.setCallbackUrls(parseCallbackUrls(form.getCallbackUrls()));
        client.setScopes(mapScopes(form.getScopes()));
    }

    private Set<String> parseCallbackUrls(String callbackUrls) {
        return Arrays.stream(callbackUrls.split(","))
                .map(String::strip)
                .filter(url -> !url.isEmpty())
                .collect(Collectors.toSet());
    }

    private Set<Scope> mapScopes(List<String> scopes) {
        return scopes.stream()
                .map(scope -> scopeRepository.findById(scope).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
