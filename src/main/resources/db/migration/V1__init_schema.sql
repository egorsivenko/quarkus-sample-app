CREATE TABLE users
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    full_name   VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(255) NOT NULL,
    is_verified BOOLEAN      NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE scopes
(
    name        VARCHAR(255) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    audience    VARCHAR(255) NOT NULL
);

CREATE TABLE oauth_clients
(
    client_id     VARCHAR(255) PRIMARY KEY,
    client_secret VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL UNIQUE,
    callback_urls TEXT[]       NOT NULL
);

CREATE TABLE client_scopes
(
    client_id  VARCHAR(255) NOT NULL,
    scope_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (client_id, scope_name),
    FOREIGN KEY (client_id) REFERENCES oauth_clients ON DELETE CASCADE,
    FOREIGN KEY (scope_name) REFERENCES scopes ON DELETE CASCADE
);

CREATE TABLE consents
(
    id                UUID PRIMARY KEY,
    resource_owner_id UUID         NOT NULL,
    oauth_client_id   VARCHAR(255) NOT NULL,
    granted_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_owner_id) REFERENCES users ON DELETE CASCADE,
    FOREIGN KEY (oauth_client_id) REFERENCES oauth_clients ON DELETE CASCADE,
    UNIQUE (resource_owner_id, oauth_client_id)
);

CREATE TABLE consent_scopes
(
    consent_id UUID         NOT NULL,
    scope_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (consent_id, scope_name),
    FOREIGN KEY (consent_id) REFERENCES consents ON DELETE CASCADE,
    FOREIGN KEY (scope_name) REFERENCES scopes ON DELETE CASCADE
);

CREATE TABLE auth_codes
(
    code                  VARCHAR(255) PRIMARY KEY,
    consent_id            UUID         NOT NULL,
    code_challenge        VARCHAR(255) NOT NULL,
    code_challenge_method VARCHAR(255) NOT NULL,
    nonce                 VARCHAR(255),
    expires_at            TIMESTAMP    NOT NULL,
    FOREIGN KEY (consent_id) REFERENCES consents ON DELETE CASCADE
);