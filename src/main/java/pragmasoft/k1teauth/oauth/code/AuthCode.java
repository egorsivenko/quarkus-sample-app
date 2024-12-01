package pragmasoft.k1teauth.oauth.code;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.user.User;

import java.util.Optional;

@Entity
@Table(name = "auth_codes")
public class AuthCode extends PanacheEntityBase {

    @Id
    @Column(nullable = false, unique = true)
    public String code;

    @ManyToOne
    @JoinColumn(name = "oauth_client_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public OAuthClient client;

    @ManyToOne
    @JoinColumn(name = "resource_owner_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public User resourceOwner;

    public static Optional<AuthCode> findByCodeOptional(String code) {
        return find("code", code).firstResultOptional();
    }

    public static void deleteByCode(String code) {
        delete("code", code);
    }
}
