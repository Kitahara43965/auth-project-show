package com.example.myauthapp.partition.repository.discovery.source;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tools.jackson.databind.ObjectMapper;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRecordSource;

@Repository
public class PartitionTargetPartitionRecordSourceDiscoveryJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final ObjectMapper objectMapper;

    public PartitionTargetPartitionRecordSourceDiscoveryJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        ObjectMapper objectMapper
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public List<PartitionRecordSource> findTargetPartitionRecordSources(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRecoveryTableExpressionProviderContext
                partitionRecoveryTableExpressionProviderContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionRecoveryTableExpressionProviderContext,
            "partitionRecoveryTableExpressionProviderContext must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        String targetPartitionName =
            Objects.requireNonNull(
                partitionRangeDTO.getTargetPartitionName(),
                "targetPartitionName must not be null"
            );

        String partitionIdColumnName =
            partitionTablePropertiesContext.getPartitionIdColumnName();

        String partitionLocalDateTimeColumnName =
            partitionTablePropertiesContext.getPartitionLocalDateTimeColumnName();

        String sql = """
            SELECT
                %s,
                %s AS partition_local_date_time,
                %s AS record_data
            FROM %s
            WHERE %s >= ?
            AND %s < ?
            ORDER BY %s
            """.formatted(
                partitionIdColumnName,
                partitionLocalDateTimeColumnName,
                partitionRecoveryTableExpressionProviderContext.recordDataExpression(),
                targetPartitionName,
                partitionLocalDateTimeColumnName,
                partitionLocalDateTimeColumnName,
                partitionIdColumnName
            );

            return auditJdbcTemplate.query(
                sql,
                (rs, rowNum) -> new PartitionRecordSource(
                    rs.getLong(partitionIdColumnName),
                    rs.getTimestamp("partition_local_date_time").toLocalDateTime(),
                    objectMapper.readTree(rs.getString("record_data"))
                ),
                partitionRangeDTO.getRangeStartAt(),
                partitionRangeDTO.getRangeEndAt()
            );
    }
}