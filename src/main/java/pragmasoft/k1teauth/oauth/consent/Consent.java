package pragmasoft.k1teauth.oauth.consent;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
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
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.oauth.scope.Scope;
import pragmasoft.k1teauth.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Serdeable
@Entity
@Table(
        name = "consents",
        uniqueConstraints = @UniqueConstraint(columnNames = {"resource_owner_id", "oauth_client_id"})
)
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "resource_owner_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User resourceOwner;

    @ManyToOne
    @JoinColumn(name = "oauth_client_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private OAuthClient client;

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
    private Set<Scope> scopes = new HashSet<>();

    @CreationTimestamp
    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

    public Consent() {}

    public Consent(User resourceOwner, OAuthClient client, Set<Scope> scopes) {
        this.resourceOwner = resourceOwner;
        this.client = client;
        this.scopes = scopes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getResourceOwner() {
        return resourceOwner;
    }

    public void setResourceOwner(User resourceOwner) {
        this.resourceOwner = resourceOwner;
    }

    public OAuthClient getClient() {
        return client;
    }

    public void setClient(OAuthClient client) {
        this.client = client;
    }

    public Set<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(Set<Scope> scopes) {
        this.scopes = scopes;
    }

    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }
}
