package pragmasoft.k1teauth.oauth.client;

import io.micronaut.context.annotation.Property;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
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
import pragmasoft.k1teauth.oauth.client.form.ClientFormData;
import pragmasoft.k1teauth.oauth.client.form.EditClientForm;
import pragmasoft.k1teauth.oauth.client.form.RegisterClientForm;
import pragmasoft.k1teauth.oauth.scope.Scope;
import pragmasoft.k1teauth.oauth.scope.ScopeRepository;
import pragmasoft.k1teauth.security.generator.CodeGenerator;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Hidden
@Controller("/oauth2/clients")
@Secured("ADMIN")
public class OAuthClientController {

    private static final String CLIENTS_PATH = "/oauth2/clients";
    private static final String REGISTER_CLIENT_PATH = "/oauth2/clients/new";
    private static final String REGISTER_CLIENT_TEMPLATE = "oauth/registerClient.jte";
    private static final String EDIT_CLIENT_PATH = "/oauth2/clients/edit";
    private static final String EDIT_CLIENT_TEMPLATE = "oauth/editClient.jte";

    @Property(name = "page.size", defaultValue = "-1")
    private int pageSize;
    private static final Sort SORT = Sort.of(Sort.Order.asc("name"));

    private final OAuthClientRepository clientRepository;
    private final ScopeRepository scopeRepository;
    private final FormGenerator formGenerator;
    private final JteTemplateRenderer jteTemplateRenderer;

    public OAuthClientController(OAuthClientRepository clientRepository,
                                 ScopeRepository scopeRepository,
                                 FormGenerator formGenerator,
                                 JteTemplateRenderer jteTemplateRenderer) {
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.formGenerator = formGenerator;
        this.jteTemplateRenderer = jteTemplateRenderer;
    }

    @Get(produces = MediaType.TEXT_HTML)
    public String clients(Pageable pageable) {
        return jteTemplateRenderer.render("oauth/clients.jte",
                Map.of("page", clientRepository.findAll(Pageable.from(pageable.getNumber(), pageSize, SORT)),
                        "formGenerator", formGenerator));
    }

    @Get(uri = "/new", produces = MediaType.TEXT_HTML)
    public String registerClientTemplate() {
        return jteTemplateRenderer.render(REGISTER_CLIENT_TEMPLATE,
                Map.of("form", formGenerator.generate(REGISTER_CLIENT_PATH, RegisterClientForm.class)));
    }

    @Post(uri = "/new", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> registerClient(@Valid @Body RegisterClientForm form) {
        if (clientRepository.findByName(form.clientName()).isPresent()) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(REGISTER_CLIENT_TEMPLATE,
                    Map.of("form", formGenerator.generate(REGISTER_CLIENT_PATH, form),
                            "errors", List.of(Message.of("OAuth client name is already registered")))));
        }
        OAuthClient client = new OAuthClient();
        client.setClientId(CodeGenerator.generate(40));
        if (form.isConfidential()) {
            client.setClientSecret(CodeGenerator.generate(40));
        }
        assignFormDataToClient(client, form);

        clientRepository.save(client);
        return HttpResponse.seeOther(URI.create(CLIENTS_PATH));
    }

    @Get(uri = "/edit", produces = MediaType.TEXT_HTML)
    public String editClientTemplate(@QueryValue String clientId) {
        OAuthClient client = clientRepository.findById(clientId).orElseThrow(NotFoundException::new);
        return jteTemplateRenderer.render(EDIT_CLIENT_TEMPLATE,
                Map.of("form", formGenerator.generate(EDIT_CLIENT_PATH, EditClientForm.from(client))));
    }

    @Post(uri = "/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> editClient(@Valid @Body EditClientForm form) {
        OAuthClient client = clientRepository.findById(form.clientId()).orElseThrow(NotFoundException::new);
        if (!client.getName().equals(form.clientName())
                && clientRepository.findByName(form.clientName()).isPresent()) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(EDIT_CLIENT_TEMPLATE,
                    Map.of("form", formGenerator.generate(EDIT_CLIENT_PATH, form),
                            "errors", List.of(Message.of("OAuth client name is already registered")))));
        }
        assignFormDataToClient(client, form);

        clientRepository.update(client);
        return HttpResponse.seeOther(URI.create(CLIENTS_PATH));
    }

    @Post(uri = "/delete", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<Void> deleteClient(String clientId) {
        clientRepository.deleteById(clientId);
        return HttpResponse.seeOther(URI.create(CLIENTS_PATH));
    }

    @Error(exception = ConstraintViolationException.class)
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse<String> handleError(HttpRequest<?> request, ConstraintViolationException ex) {
        if (request.getPath().equals(REGISTER_CLIENT_PATH)) {
            Optional<RegisterClientForm> formOptional = request.getBody(RegisterClientForm.class);
            if (formOptional.isPresent()) {
                return HttpResponse.unprocessableEntity().body(jteTemplateRenderer.render(REGISTER_CLIENT_TEMPLATE,
                        Map.of("form", formGenerator.generate(REGISTER_CLIENT_PATH, formOptional.get(), ex))));
            }
        } else if (request.getPath().equals(EDIT_CLIENT_PATH)) {
            Optional<EditClientForm> formOptional = request.getBody(EditClientForm.class);
            if (formOptional.isPresent()) {
                return HttpResponse.unprocessableEntity().body(jteTemplateRenderer.render(EDIT_CLIENT_TEMPLATE,
                        Map.of("form", formGenerator.generate(EDIT_CLIENT_PATH, formOptional.get(), ex))));
            }
        }
        return HttpResponse.serverError();
    }

    private void assignFormDataToClient(OAuthClient client, ClientFormData formData) {
        client.setName(formData.clientName());
        client.setCallbackUrls(parseCallbackUrls(formData.callbackUrls()));
        client.setScopes(mapScopes(formData.scopes()));
        client.setConfidential(formData.isConfidential());
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
