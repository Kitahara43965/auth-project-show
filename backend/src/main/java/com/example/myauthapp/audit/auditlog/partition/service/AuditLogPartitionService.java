package com.example.myauthapp.audit.auditlog.partition.service;
import java.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogPartitionService {

    private static final Logger log =
            LoggerFactory.getLogger(AuditLogPartitionService.class);

    private final JdbcTemplate jdbcTemplate;

    public AuditLogPartitionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void ensurePartition(YearMonth yearMonth) {

        String partitionName =
                "audit_logs_" + yearMonth.toString().replace("-", "_");

        String start = yearMonth.atDay(1).toString();
        String end = yearMonth.plusMonths(1).atDay(1).toString();

        String sql = """
            CREATE TABLE IF NOT EXISTS %s
            PARTITION OF audit_logs
            FOR VALUES FROM ('%s') TO ('%s')
            """.formatted(
                partitionName,
                start,
                end
            );

        log.info("Ensuring audit log partition: {}", partitionName);

        jdbcTemplate.execute(sql);
    }
}