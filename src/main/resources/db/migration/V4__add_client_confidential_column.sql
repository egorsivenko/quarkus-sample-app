ALTER TABLE oauth_clients
    ADD COLUMN is_confidential BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE oauth_clients
    ALTER COLUMN is_confidential DROP DEFAULT;

ALTER TABLE oauth_clients
    ALTER COLUMN client_secret DROP NOT NULL;