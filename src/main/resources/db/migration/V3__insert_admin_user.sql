CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users(full_name, email, password, role, is_verified)
VALUES ('${admin-name}', '${admin-email}', crypt('${admin-password}', gen_salt('bf')), 'ADMIN', true);