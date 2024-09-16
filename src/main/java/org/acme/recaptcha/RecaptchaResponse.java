package org.acme.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecaptchaResponse(
        boolean success,
        @JsonProperty("challenge_ts")
        String challengeTimestamp,
        String hostname,
        double score
) {}
