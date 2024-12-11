package pragmasoft.k1teauth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("access_token") String token,
        @JsonProperty("expires_in") long expiresInSecs,
        @JsonProperty("token_type") String tokenType
) {
}
