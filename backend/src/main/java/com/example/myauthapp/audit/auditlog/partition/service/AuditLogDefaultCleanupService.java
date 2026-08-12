package com.example.myauthapp.audit.auditlog.partition.service;

import java.time.YearMonth;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogDefaultCleanupService {

    private final JdbcTemplate jdbcTemplate;

    public AuditLogDefaultCleanupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void cleanup(YearMonth yearMonth) {

        String partitionName =
            "audit_logs_" + yearMonth.toString().replace("-", "_");

        String start = yearMonth.atDay(1).toString();
        String end = yearMonth.plusMonths(1).atDay(1).toString();

        // DEFAULT → 新Partitionへ直接移動
        String insertSql = """
            INSERT INTO %s
            SELECT *
            FROM audit_logs_default
            WHERE occurred_at >= ?
            AND occurred_at < ?
            """.formatted(partitionName);

        jdbcTemplate.update(
            insertSql,
            start,
            end
        );

        String deleteSql = """
            DELETE FROM audit_logs_default
            WHERE occurred_at >= ?
            AND occurred_at < ?
            """;

        jdbcTemplate.update(
            deleteSql,
            start,
            end
        );
    }

    @Transactional
    public void backup(YearMonth yearMonth) {

        String start = yearMonth.atDay(1).toString();
        String end = yearMonth.plusMonths(1).atDay(1).toString();

        jdbcTemplate.update(
            """
            CREATE TEMP TABLE audit_logs_recovery_backup
            AS
            SELECT *
            FROM audit_logs_default
            WHERE occurred_at >= ?
            AND occurred_at < ?
            """,
            start,
            end
        );
    }

    @Transactional
    public void remove(YearMonth yearMonth) {

        String start = yearMonth.atDay(1).toString();
        String end = yearMonth.plusMonths(1).atDay(1).toString();

        jdbcTemplate.update(
            """
            DELETE FROM audit_logs_default
            WHERE occurred_at >= ?
            AND occurred_at < ?
            """,
            start,
            end
        );
    }

    @Transactional
    public void restore(YearMonth yearMonth) {

        String partitionName =
            "audit_logs_" +
            yearMonth.toString().replace("-", "_");

        jdbcTemplate.update(
            """
            INSERT INTO %s
            SELECT *
            FROM audit_logs_recovery_backup
            """.formatted(partitionName)
        );

        jdbcTemplate.execute(
            "DROP TABLE audit_logs_recovery_backup"
        );
    }


    public List<YearMonth> findDefaultMonths() {

        String sql = """
            SELECT DISTINCT
                EXTRACT(YEAR FROM occurred_at) AS year,
                EXTRACT(MONTH FROM occurred_at) AS month
            FROM audit_logs_default
            ORDER BY year, month
            """;

        return jdbcTemplate.query(
            sql,
            (resultSet, rowNumber) -> YearMonth.of(
                resultSet.getInt("year"),
                resultSet.getInt("month")
            )
        );
    }
}