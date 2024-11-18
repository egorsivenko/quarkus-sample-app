package org.acme.oauth.client;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.acme.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "oauth_clients")
public class OAuthClient extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "client_id", nullable = false, unique = true)
    public String clientId;

    @Column(name = "client_secret", nullable = false, unique = true)
    public String clientSecret;

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Column(name = "homepage_url", nullable = false)
    public String homepageUrl;

    @Column(name = "callback_url", nullable = false)
    public String callbackUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public User user;

    @Type(ListArrayType.class)
    @Column(
            name = "scopes",
            nullable = false,
            columnDefinition = "varchar(500)[]"
    )
    public Set<String> scopes;

    public static Optional<OAuthClient> findByClientIdOptional(String clientId) {
        return find("clientId", clientId).firstResultOptional();
    }

    public static void deleteByClientId(String clientId) {
        delete("clientId", clientId);
    }
}
