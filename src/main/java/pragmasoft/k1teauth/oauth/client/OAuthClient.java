package pragmasoft.k1teauth.oauth.client;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;
import pragmasoft.k1teauth.oauth.scope.Scope;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "oauth_clients")
public class OAuthClient extends PanacheEntityBase {

    @Id
    @Column(name = "client_id", nullable = false, unique = true)
    public String clientId;

    @Column(name = "client_secret", nullable = false, unique = true)
    public String clientSecret;

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Type(ListArrayType.class)
    @Column(
            name = "callback_urls",
            nullable = false,
            columnDefinition = "text[]"
    )
    public Set<String> callbackUrls;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_scopes",
            joinColumns = @JoinColumn(
                    name = "client_id",
                    foreignKey = @ForeignKey(
                            foreignKeyDefinition = "foreign key (client_id) references oauth_clients on delete cascade"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "scope_name",
                    foreignKey = @ForeignKey(
                            foreignKeyDefinition = "foreign key (scope_name) references scopes on delete cascade"
                    )
            )
    )
    public Set<Scope> scopes = new HashSet<>();

    public static Optional<OAuthClient> findByClientIdOptional(String clientId) {
        return find("clientId", clientId).firstResultOptional();
    }

    public static Optional<OAuthClient> findByClientIdAndSecret(String clientId, String clientSecret) {
        return find("clientId = ?1 and clientSecret = ?2", clientId, clientSecret).firstResultOptional();
    }

    public static Optional<OAuthClient> findByNameOptional(String name) {
        return find("name", name).firstResultOptional();
    }

    public static void deleteByClientId(String clientId) {
        delete("clientId", clientId);
    }
}
