package org.acme.oauth;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.acme.oauth.client.OAuthClient;
import org.acme.user.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "auth_codes")
public class AuthCode extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

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
