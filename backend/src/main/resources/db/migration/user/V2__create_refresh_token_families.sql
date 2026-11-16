CREATE TABLE refresh_token_families (
    id UUID PRIMARY KEY,

    user_id BIGINT NOT NULL,

    revoked_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL
);

ALTER TABLE refresh_token_families
ADD CONSTRAINT fk_refresh_token_family_user
FOREIGN KEY (user_id)
REFERENCES users(id);