package org.acme.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("id_token") String idToken,
        @JsonProperty("expires_in") int expiresInSecs,
        @JsonProperty("token_type") String tokenType
) {
}
