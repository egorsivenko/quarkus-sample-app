package org.acme.recaptcha;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class RecaptchaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaService.class);

    private static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @ConfigProperty(name = "recaptcha.secret.key")
    String secretKey;

    private final ObjectMapper mapper;

    public RecaptchaService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public boolean verifyToken(String token) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder builder = new URIBuilder(SITE_VERIFY_URL)
                    .setParameter("secret", secretKey)
                    .setParameter("response", token);

            HttpPost post = new HttpPost(builder.build());
            post.setEntity(new StringEntity(""));

            String responseBody = httpClient.execute(post, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));

            RecaptchaResponse response = mapper.readValue(responseBody, RecaptchaResponse.class);
            return response.success();

        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Error verifying a token", e);
            return false;
        }
    }
}
