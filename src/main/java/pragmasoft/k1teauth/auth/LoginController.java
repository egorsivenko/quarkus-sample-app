package pragmasoft.k1teauth.auth;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.messages.Message;
import io.swagger.v3.oas.annotations.Hidden;
import pragmasoft.k1teauth.auth.form.LoginForm;
import pragmasoft.k1teauth.common.jte.JteTemplateRenderer;

import java.util.Map;

@Hidden
@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
public class LoginController {

    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_TEMPLATE = "auth/login.jte";

    @Property(name = "turnstile.siteKey")
    private String siteKey;

    private final FormGenerator formGenerator;
    private final JteTemplateRenderer jteTemplateRenderer;

    public LoginController(FormGenerator formGenerator,
                           JteTemplateRenderer jteTemplateRenderer) {
        this.formGenerator = formGenerator;
        this.jteTemplateRenderer = jteTemplateRenderer;
    }

    @Get(uri = "/login", produces = MediaType.TEXT_HTML)
    public String login(@QueryValue(defaultValue = "false") boolean failed) {
        return jteTemplateRenderer.render(LOGIN_TEMPLATE,
                Map.of("form", formGenerator.generate(LOGIN_PATH, LoginForm.class, Message.of("Login")),
                        "siteKey", siteKey, "failed", failed));
    }
}
