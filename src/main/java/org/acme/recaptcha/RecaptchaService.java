package org.acme.recaptcha;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/siteverify")
@RegisterRestClient(configKey = "recaptcha-api")
public interface RecaptchaService {

    @POST
    RecaptchaResponse verifyToken(@QueryParam("secret") String secretKey,
                                  @QueryParam("response") String token);
}
