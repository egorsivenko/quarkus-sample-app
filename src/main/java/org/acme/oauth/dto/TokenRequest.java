package org.acme.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenRequest(
        @JsonProperty("grant_type") String grantType,
        @JsonProperty("code") String code,
        @JsonProperty("client_id") String clientId,
        @JsonProperty("client_secret") String clientSecret
) {
}
