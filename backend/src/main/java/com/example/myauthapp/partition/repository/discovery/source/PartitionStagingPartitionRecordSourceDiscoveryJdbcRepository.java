package com.example.myauthapp.partition.repository.discovery.source;

import java.util.Objects;
import java.util.List;
import java.util.UUID;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.dto.PartitionRecordSource;

@Repository
public class PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;
    private final ObjectMapper objectMapper;

    public PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties,
        ObjectMapper objectMapper
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
        this.objectMapper = objectMapper;
    }

    public List<PartitionRecordSource> findStagingPartitionRecordSources(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        UUID publicPartitionOperationId
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        String partitionIdColumnName =
            partitionTablePropertiesContext.getPartitionIdColumnName();

        String partitionLocalDateTimeColumnName =
            partitionTablePropertiesContext.getPartitionLocalDateTimeColumnName();

        String sql = """
            SELECT
                record_id,
                partition_local_date_time,
                record_data
            FROM %s
            WHERE %s = ?
            ORDER BY record_id
            """.formatted(
                partitionProperties.getPartitionStagingTableName(),
                partitionProperties.getPublicPartitionOperationIdColumnName()
            );

        return partitionJdbcTemplate.query(
            sql,
            (rs, rowNum) -> new PartitionRecordSource(
                rs.getLong("record_id"),
                rs.getTimestamp("partition_local_date_time").toLocalDateTime(),
                objectMapper.readTree(rs.getString("record_data"))
            ),
            publicPartitionOperationId
        );
    }
}