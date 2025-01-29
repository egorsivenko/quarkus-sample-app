package pragmasoft.k1teauth.user;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import pragmasoft.k1teauth.security.hash.BcryptUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Serdeable
@Entity
@Table(name = "users")
public class User {

    public enum Role { ADMIN, USER }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String fullName, String email, String password) {
        this(fullName, email, password, Role.USER, false);
    }

    public User(String fullName, String email, String password, Role role, boolean isVerified) {
        this.fullName = fullName;
        this.email = email;
        this.password = BcryptUtil.encode(password);
        this.role = role;
        this.isVerified = isVerified;
    }

    public boolean verifyPassword(String password) {
        return BcryptUtil.matches(password, this.password);
    }

    public void changePassword(String newPassword) {
        this.password = BcryptUtil.encode(newPassword);
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
