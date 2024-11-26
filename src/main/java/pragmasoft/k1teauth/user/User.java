package pragmasoft.k1teauth.user;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import pragmasoft.k1teauth.oauth.client.OAuthClient;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

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
    private UserRole role;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<OAuthClient> clients = new HashSet<>();

    public User() {
    }

    public User(String fullName, String email, String password) {
        this(fullName, email, password, UserRole.USER, false);
    }

    public User(String fullName, String email, String password, UserRole role, boolean isVerified) {
        this.fullName = fullName;
        this.email = email;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = role;
        this.isVerified = isVerified;
        this.createdAt = LocalDateTime.now();
    }

    public boolean verifyPassword(String password) {
        return BcryptUtil.matches(password, this.password);
    }

    public void changePassword(String newPassword) {
        this.password = BcryptUtil.bcryptHash(newPassword);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
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

    public UserRole getRole() {
        return role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<OAuthClient> getClients() {
        return clients;
    }
}
