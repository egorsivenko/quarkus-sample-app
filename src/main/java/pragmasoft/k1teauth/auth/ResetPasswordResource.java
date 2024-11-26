package pragmasoft.k1teauth.auth;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pragmasoft.k1teauth.auth.form.ResetPasswordForm;
import pragmasoft.k1teauth.email.EmailSender;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.util.CsrfTokenValidator;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import static pragmasoft.k1teauth.util.FlashScopeConstants.PASSWORDS_MATCH;
import static pragmasoft.k1teauth.util.FlashScopeConstants.PASSWORDS_MATCH_MESSAGE;

@Path("/auth")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
public class ResetPasswordResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance resetPassword(UUID userId);

        public static native TemplateInstance resetPasswordConfirmation(UUID userId);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordResource.class);

    private final UserService userService;
    private final EmailSender emailSender;

    public ResetPasswordResource(UserService userService,
                                 EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @GET
    @Path("/reset-password/{userId}")
    public TemplateInstance resetPasswordTemplate(@PathParam("userId") UUID userId) {
        return Templates.resetPassword(userId);
    }

    @POST
    @Path("/reset-password")
    public Response resetPassword(@BeanParam @Valid ResetPasswordForm form,
                                  @CookieParam("csrf-token") Cookie csrfTokenCookie,
                                  @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            validation.addError(PASSWORDS_MATCH, PASSWORDS_MATCH_MESSAGE);
        }
        if (validationFailed()) {
            resetPasswordTemplate(form.getUserId());
        }
        User user = userService.getById(form.getUserId());
        user.changePassword(form.getPassword());

        LOGGER.info("Successful password reset for email `{}`", user.getEmail());
        return Response.seeOther(URI.create("/auth/login")).build();
    }

    @GET
    @Path("/reset-password-confirmation")
    public TemplateInstance resetPasswordConfirmation(@RestQuery UUID userId) {
        return Templates.resetPasswordConfirmation(userId);
    }

    @POST
    @Path("/resend-reset-password-email")
    public void resendResetPasswordEmail(@RestForm UUID userId,
                                         @CookieParam("csrf-token") Cookie csrfTokenCookie,
                                         @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        User user = userService.getById(userId);
        emailSender.sendResetPasswordEmail(user);
        resetPasswordConfirmation(userId);
    }
}
