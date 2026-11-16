package com.example.myauthapp.audit.log.dto;

import org.springframework.jdbc.core.RowMapper;
import java.sql.Timestamp;

import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;

public final class AuditLogDTODiscoverySql {

    public static final String COLUMN_NAMES = """
        id,
        occurred_at,
        created_at,
        audit_event_type,
        audit_result,
        actor_id,
        subject_id,
        client_id,
        ip_address,
        user_agent,
        request_id,
        metadata
        """;

    public static final RowMapper<AuditLogDTO> ROW_MAPPER =
        (rs, rowNum) -> {

            Timestamp occurredAt =
                rs.getTimestamp("occurred_at");

            Timestamp createdAt =
                rs.getTimestamp("created_at");

            return new AuditLogDTO(
                rs.getLong("id"),
                occurredAt != null
                    ? occurredAt.toLocalDateTime()
                    : null,
                createdAt != null
                    ? createdAt.toLocalDateTime()
                    : null,
                AuditEventType.valueOf(
                    rs.getString("audit_event_type")
                ),
                AuditResult.valueOf(
                    rs.getString("audit_result")
                ),
                rs.getObject("actor_id", Long.class),
                rs.getObject("subject_id", Long.class),
                rs.getString("client_id"),
                rs.getString("ip_address"),
                rs.getString("user_agent"),
                rs.getString("request_id"),
                rs.getString("metadata")
            );
        };

    private AuditLogDTODiscoverySql() {
    }
}
