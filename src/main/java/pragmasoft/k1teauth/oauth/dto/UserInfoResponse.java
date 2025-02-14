package pragmasoft.k1teauth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;
import pragmasoft.k1teauth.user.User;

import java.time.LocalDateTime;

@Serdeable
public record UserInfoResponse(
        String sub,
        String name,
        String email,
        @JsonProperty("email_verified") boolean emailVerified,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
    public static UserInfoResponse fromUser(User user) {
        return new UserInfoResponse(
                user.getId().toString(),
                user.getFullName(),
                user.getEmail(),
                user.isVerified(),
                user.getCreatedAt()
        );
    }
}
