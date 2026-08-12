CREATE INDEX idx_refresh_tokens_created_at
ON refresh_tokens(created_at);

CREATE INDEX idx_refresh_tokens_expires_at
ON refresh_tokens(expires_at);