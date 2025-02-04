package pragmasoft.k1teauth.oauth.code;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pragmasoft.k1teauth.oauth.consent.Consent;
import pragmasoft.k1teauth.util.HashUtil;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "auth_codes")
public class AuthCode extends PanacheEntityBase {

    @Id
    @Column(nullable = false, unique = true)
    public String code;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "consent_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Consent consent;

    @Column(name = "code_challenge", nullable = false)
    public String codeChallenge;

    @Column(name = "code_challenge_method", nullable = false)
    public String codeChallengeMethod;

    @Column(name = "expires_at", nullable = false)
    public LocalDateTime expiresAt;

    public static Optional<AuthCode> findByCodeOptional(String code) {
        return find("code", HashUtil.hashWithSHA256(code)).firstResultOptional();
    }

    public static Optional<AuthCode> findByConsentOptional(Consent consent) {
        return find("consent", consent).firstResultOptional();
    }

    public static void deleteByCode(String code) {
        delete("code", HashUtil.hashWithSHA256(code));
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void setCode(String code) {
        this.code = HashUtil.hashWithSHA256(code);
    }
}
