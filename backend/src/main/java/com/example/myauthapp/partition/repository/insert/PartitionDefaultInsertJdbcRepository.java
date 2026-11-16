package com.example.myauthapp.partition.repository.insert;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRecordSource;
import com.example.myauthapp.partition.enums.type.PartitionTableType;

import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.util.PartitionBatchUpdateCountUtils;


@Repository
public class PartitionDefaultInsertJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionDefaultInsertJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public long insertPartitionRecordSourcesIntoPartition(
        PartitionRecoveryTableExpressionProviderContext
            partitionRecoveryTableExpressionProviderContext,
        PartitionRangeDTO partitionRangeDTO,
        List<PartitionRecordSource> partitionRecordSources
    ) {
        Objects.requireNonNull(
            partitionRecoveryTableExpressionProviderContext,
            "partitionRecoveryTableExpressionProviderContext must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        Objects.requireNonNull(
            partitionRecordSources,
            "partitionRecordSources must not be null"
        );

        if (partitionRecordSources.isEmpty()) {
            return 0L;
        }

        String targetPartitionName =
            Objects.requireNonNull(
                partitionRangeDTO.getTargetPartitionName(),
                "targetPartitionName must not be null"
            );

        String columnNames =
            Objects.requireNonNull(
                partitionRecoveryTableExpressionProviderContext.columnNames(),
                "columnNames must not be null"
            );

        String restoreSelectExpression =
            Objects.requireNonNull(
                partitionRecoveryTableExpressionProviderContext
                    .restoreSelectExpression(),
                "restoreSelectExpression must not be null"
            );

        String insertSql = """
            INSERT INTO %s (
                %s
            )
            SELECT
                %s
            FROM (
                SELECT ?::jsonb AS record_data
            ) staging
            """.formatted(
                targetPartitionName,
                columnNames,
                restoreSelectExpression
            );

        List<Object[]> batchArgs = partitionRecordSources.stream()
            .map(source -> new Object[] {
                source.getRecordData().toString()
            })
            .toList();

        int[] batchUpdateCounts = auditJdbcTemplate.batchUpdate(
            insertSql,
            batchArgs
        );

        long insertedRowCount = 
            PartitionBatchUpdateCountUtils.sumBatchUpdateCounts(
                batchUpdateCounts,
                PartitionTableType.TARGET_PARTITION
            );

        return insertedRowCount;
    }
}