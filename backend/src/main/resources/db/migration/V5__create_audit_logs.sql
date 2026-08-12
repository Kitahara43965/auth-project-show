CREATE SEQUENCE audit_logs_id_seq;

CREATE TABLE audit_logs (
    id BIGINT NOT NULL DEFAULT nextval('audit_logs_id_seq'),
    occurred_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,

    audit_event_type VARCHAR(100) NOT NULL,

    actor_id BIGINT,
    subject_id BIGINT,

    client_id VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    request_id VARCHAR(255),

    audit_result VARCHAR(50) NOT NULL,

    metadata JSONB
) PARTITION BY RANGE (occurred_at);

CREATE TABLE audit_logs_default
PARTITION OF audit_logs DEFAULT;