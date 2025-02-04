package pragmasoft.k1teauth.oauth.client;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import io.micronaut.serde.annotation.Serdeable;
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
import java.util.Set;

@Serdeable
@Entity
@Table(name = "oauth_clients")
public class OAuthClient {

    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret", nullable = false, unique = true)
    private String clientSecret;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Type(ListArrayType.class)
    @Column(
            name = "callback_urls",
            nullable = false,
            columnDefinition = "text[]"
    )
    private Set<String> callbackUrls;

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
    private Set<Scope> scopes = new HashSet<>();

    public OAuthClient() {}

    public OAuthClient(String clientId, String clientSecret, String name, Set<String> callbackUrls, Set<Scope> scopes) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.name = name;
        this.callbackUrls = callbackUrls;
        this.scopes = scopes;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getCallbackUrls() {
        return callbackUrls;
    }

    public void setCallbackUrls(Set<String> callbackUrls) {
        this.callbackUrls = callbackUrls;
    }

    public Set<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(Set<Scope> scopes) {
        this.scopes = scopes;
    }
}
