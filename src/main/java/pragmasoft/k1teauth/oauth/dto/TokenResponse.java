package pragmasoft.k1teauth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@JsonInclude(Include.NON_NULL)
public record TokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("token_type") String tokenType
) {}
