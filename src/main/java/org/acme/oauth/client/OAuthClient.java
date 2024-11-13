package org.acme.oauth.client;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
}
