INSERT INTO oauth_clients (client_id, name, callback_urls, is_confidential)
VALUES ('swagger-id', 'Swagger UI', '{"${server-url}/swagger-ui/oauth2-redirect.html"}', false);

INSERT INTO client_scopes (client_id, scope_name)
VALUES ('swagger-id', 'openid');