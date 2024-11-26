package pragmasoft.k1teauth.user;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriBuilder;
import pragmasoft.k1teauth.email.EmailSender;
import pragmasoft.k1teauth.user.form.ChangePasswordForm;
import pragmasoft.k1teauth.util.CookieUtils;
import pragmasoft.k1teauth.util.CsrfTokenValidator;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.util.Objects;

import static pragmasoft.k1teauth.util.FlashScopeConstants.ERROR;
import static pragmasoft.k1teauth.util.FlashScopeConstants.INCORRECT_PASSWORD_MESSAGE;
import static pragmasoft.k1teauth.util.FlashScopeConstants.PASSWORDS_MATCH;
import static pragmasoft.k1teauth.util.FlashScopeConstants.PASSWORDS_MATCH_MESSAGE;

@Path("/profile")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@Authenticated
public class UserProfileResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance profile(User user);

        public static native TemplateInstance changePassword();
    }

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String cookieName;

    private final UserService userService;
    private final EmailSender emailSender;

    public UserProfileResource(UserService userService,
                               EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @GET
    @Path("/")
    public Response profile(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        User user = userService.getByEmail(email);

        if (!user.isVerified()) {
            emailSender.sendRegistrationEmail(user);

            URI uri = UriBuilder.fromPath("/auth/registration-confirmation")
                    .queryParam("userId", user.getId())
                    .build();

            return Response.seeOther(uri)
                    .cookie(CookieUtils.buildRemoveCookie(cookieName))
                    .build();
        }
        return Response.ok(Templates.profile(user)).build();
    }

    @GET
    @Path("/change-password")
    public TemplateInstance changePasswordTemplate() {
        return Templates.changePassword();
    }

    @POST
    @Path("/change-password")
    public Response changePassword(@Context SecurityContext securityContext,
                                   @BeanParam @Valid ChangePasswordForm form,
                                   @CookieParam("csrf-token") Cookie csrfTokenCookie,
                                   @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        if (!Objects.equals(form.getNewPassword(), form.getConfirmPassword())) {
            validation.addError(PASSWORDS_MATCH, PASSWORDS_MATCH_MESSAGE);
        }
        if (validationFailed()) {
            changePasswordTemplate();
        }
        String email = securityContext.getUserPrincipal().getName();
        User user = userService.getByEmail(email);

        if (!user.verifyPassword(form.getCurrentPassword())) {
            flash(ERROR, INCORRECT_PASSWORD_MESSAGE);
            changePasswordTemplate();
        }
        user.changePassword(form.getNewPassword());
        return Response.seeOther(URI.create("/profile")).build();
    }
}
