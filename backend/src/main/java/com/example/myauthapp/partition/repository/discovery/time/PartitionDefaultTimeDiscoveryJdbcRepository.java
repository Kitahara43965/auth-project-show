package com.example.myauthapp.partition.repository.discovery.time;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PartitionDefaultTimeDiscoveryJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;

    public PartitionDefaultTimeDiscoveryJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
    }


    public List<LocalDateTime> findTargetMonthsFromDefaultPartition(
        String defaultPartitionName,
        String localDateTimeColumnName
    ) {
        String sql = """
            SELECT DISTINCT
                date_trunc('month', %s) AS target_month
            FROM %s
            WHERE %s IS NOT NULL
            ORDER BY target_month ASC
            """.formatted(
                localDateTimeColumnName,
                defaultPartitionName,
                localDateTimeColumnName
            );

        return auditJdbcTemplate.query(
            sql,
            (rs, rowNum) ->
                rs.getTimestamp("target_month").toLocalDateTime()
        );
    }
}