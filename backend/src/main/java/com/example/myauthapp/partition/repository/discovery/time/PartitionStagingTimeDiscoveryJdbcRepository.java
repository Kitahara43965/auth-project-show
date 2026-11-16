package com.example.myauthapp.partition.repository.discovery.time;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class PartitionStagingTimeDiscoveryJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;

    public PartitionStagingTimeDiscoveryJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
    }

    public List<LocalDateTime> findTargetMonthsFromStaging(
        String partitionStagingTableName,
        String localDateTimeColumnName
    ) {
        String sql = """
            SELECT DISTINCT
                date_trunc(
                    'month',
                    NULLIF(record_data ->> '%s', '')::timestamp
                ) AS target_month
            FROM %s
            WHERE NULLIF(record_data ->> '%s', '') IS NOT NULL
            ORDER BY target_month ASC
            """.formatted(
                localDateTimeColumnName,
                partitionStagingTableName,
                localDateTimeColumnName
            );

        return partitionJdbcTemplate.query(
            sql,
            (rs, rowNum) ->
                rs.getTimestamp("target_month").toLocalDateTime()
        );
    }
}