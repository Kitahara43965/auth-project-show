CREATE SEQUENCE audit_logs_id_seq;

CREATE TABLE audit_logs (
    id BIGINT NOT NULL DEFAULT nextval('audit_logs_id_seq'),

    occurred_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,

    audit_event_type VARCHAR(100) NOT NULL,
    audit_result VARCHAR(50) NOT NULL,

    actor_id BIGINT,
    subject_id BIGINT,
    client_id VARCHAR(255),

    ip_address VARCHAR(45),
    user_agent TEXT,
    request_id VARCHAR(255),

    metadata JSONB
) PARTITION BY RANGE (occurred_at);

ALTER SEQUENCE audit_logs_id_seq
OWNED BY audit_logs.id;

CREATE TABLE audit_logs_default
PARTITION OF audit_logs DEFAULT;