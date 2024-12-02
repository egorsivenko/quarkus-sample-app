package pragmasoft.k1teauth.oauth.consent;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.oauth.scope.Scope;
import pragmasoft.k1teauth.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "consents")
public class Consent extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @ManyToOne
    @JoinColumn(name = "resource_owner_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public User resourceOwner;

    @ManyToOne
    @JoinColumn(name = "oauth_client_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public OAuthClient client;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "consent_scopes",
            joinColumns = @JoinColumn(
                    name = "consent_id",
                    foreignKey = @ForeignKey(
                            foreignKeyDefinition = "foreign key (consent_id) references consents on delete cascade"
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
}
