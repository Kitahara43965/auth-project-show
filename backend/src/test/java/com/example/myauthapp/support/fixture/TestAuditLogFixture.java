package com.example.myauthapp.support.fixture;

import com.example.myauthapp.support.util.TestTimeUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;

@Component
public class TestAuditLogFixture {

    private final JdbcTemplate jdbcTemplate;

    public TestAuditLogFixture(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertDefaultAuditLog(
        LocalDateTime occurredAt,
        LocalDateTime createdAt,
        AuditEventType auditEventType,
        Long actorId,
        AuditResult auditResult
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO audit_logs_default (
                occurred_at,
                created_at,
                audit_event_type,
                actor_id,
                audit_result
            )
            VALUES (?, ?, ?, ?, ?)
            """,
            occurredAt,
            createdAt,
            auditEventType.name(),
            actorId,
            auditResult.name()
        );
    }

    public int countDefaultLogs(
        LocalDateTime startTimeAt,
        LocalDateTime endTimeAt
    ) {

        return jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM audit_logs_default
            WHERE occurred_at >= ?
              AND occurred_at < ?
            """,
            Integer.class,
            startTimeAt,
            endTimeAt
        );
    }

    public int countPartitionLogs(
        LocalDateTime startTimeAt,
        LocalDateTime endTimeAt
    ) {
        int year = startTimeAt.getYear();
        int month = startTimeAt.getMonthValue();
        String partitionName =
            "audit_logs_" + year + "_" + String.format("%02d", month);

        return jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM %s
            WHERE occurred_at >= ?
              AND occurred_at < ?
            """.formatted(partitionName),
            Integer.class,
            startTimeAt,
            endTimeAt
        );
    }
}