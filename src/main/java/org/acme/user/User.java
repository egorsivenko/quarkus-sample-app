package org.acme.user;

import io.quarkus.elytron.security.common.BcryptUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class User {

    @Setter
    private UUID id;
    private String fullName;
    private String email;
    private String password;
    private UserRole role;
    private LocalDateTime createdAt;

    public User(String fullName, String email, String password) {
        this(fullName, email, password, UserRole.USER);
    }

    public User(String fullName, String email, String password, UserRole role) {
        this.fullName = fullName;
        this.email = email;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public boolean verifyPassword(String password) {
        return BcryptUtil.matches(password, this.password);
    }

    public void changePassword(String newPassword) {
        this.password = BcryptUtil.bcryptHash(newPassword);
    }
}
