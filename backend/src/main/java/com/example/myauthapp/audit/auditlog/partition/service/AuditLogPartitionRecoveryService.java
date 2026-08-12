package com.example.myauthapp.audit.auditlog.partition.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogPartitionRecoveryService {

    private final AuditLogPartitionService auditPartitionService;
    private final AuditLogDefaultCleanupService auditDefaultCleanupService;
    private final JdbcTemplate jdbcTemplate;

    public AuditLogPartitionRecoveryService(
            AuditLogPartitionService auditPartitionService,
            AuditLogDefaultCleanupService auditDefaultCleanupService,
            JdbcTemplate jdbcTemplate
    ) {
        this.auditPartitionService = auditPartitionService;
        this.auditDefaultCleanupService = auditDefaultCleanupService;
        this.jdbcTemplate = jdbcTemplate;
    }

    private List<YearMonth> findDefaultMonths() {

        return jdbcTemplate.query(
                """
                SELECT DISTINCT
                    EXTRACT(YEAR FROM occurred_at) AS year,
                    EXTRACT(MONTH FROM occurred_at) AS month
                FROM audit_logs_default
                ORDER BY year, month
                """,
                (rs, rowNum) ->
                        YearMonth.of(
                                rs.getInt("year"),
                                rs.getInt("month")
                        )
        );
    }

    @Transactional
    public void recoverAll() {

        System.out.println("① DETACH START");

        jdbcTemplate.execute("""
            ALTER TABLE audit_logs
            DETACH PARTITION audit_logs_default
            """);

        System.out.println("① DETACH SUCCESS");

        List<YearMonth> months = findDefaultMonths();

        System.out.println("② MONTHS = " + months);

        for (YearMonth month : months) {

            String partitionName =
                    "audit_logs_" +
                    month.toString().replace("-", "_");

            LocalDateTime start =
                    month.atDay(1).atStartOfDay();

            LocalDateTime end =
                    month.plusMonths(1).atDay(1).atStartOfDay();

            System.out.println(
                    "③ CREATE PARTITION: " + partitionName
            );

            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS %s
                PARTITION OF audit_logs
                FOR VALUES FROM ('%s') TO ('%s')
                """.formatted(
                    partitionName,
                    start,
                    end
                ));

            System.out.println("③ CREATE SUCCESS");

            System.out.println(
                    "④ INSERT: " + partitionName
            );

            jdbcTemplate.update(
                    """
                    INSERT INTO %s
                    SELECT *
                    FROM audit_logs_default
                    WHERE occurred_at >= ?
                    AND occurred_at < ?
                    """.formatted(partitionName),
                    Timestamp.valueOf(start),
                    Timestamp.valueOf(end)
            );

            System.out.println("④ INSERT SUCCESS");

            System.out.println("⑤ DELETE");

            jdbcTemplate.update(
                    """
                    DELETE FROM audit_logs_default
                    WHERE occurred_at >= ?
                    AND occurred_at < ?
                    """,
                    Timestamp.valueOf(start),
                    Timestamp.valueOf(end)
            );

            System.out.println("⑤ DELETE SUCCESS");
        }

        System.out.println("⑥ ATTACH DEFAULT");

        jdbcTemplate.execute("""
            ALTER TABLE audit_logs
            ATTACH PARTITION audit_logs_default DEFAULT
            """);

        System.out.println("⑥ ATTACH SUCCESS");
    }
}