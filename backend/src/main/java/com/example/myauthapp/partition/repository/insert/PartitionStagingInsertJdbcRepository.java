package com.example.myauthapp.partition.repository.insert;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.dto.PartitionRecordSource;
import com.example.myauthapp.partition.enums.type.PartitionTableType;

import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.util.PartitionBatchUpdateCountUtils;

@Repository
public class PartitionStagingInsertJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionStagingInsertJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public long insertPartitionRecordSourcesIntoStaging(
        UUID publicPartitionOperationId,
        List<PartitionRecordSource> partitionRecordSources
    ) {
        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        Objects.requireNonNull(
            partitionRecordSources,
            "partitionRecordSources must not be null"
        );

        if (partitionRecordSources.isEmpty()) {
            return 0L;
        }

        String insertSql = """
            INSERT INTO %s (
                public_partition_operation_id,
                record_id,
                partition_local_date_time,
                record_data
            )
            VALUES (?, ?, ?, ?::jsonb)
            """.formatted(
                partitionProperties.getPartitionStagingTableName()
            );

        List<Object[]> batchArgs = partitionRecordSources.stream()
            .map(source -> new Object[] {
                publicPartitionOperationId,
                source.getRecordId(),
                Timestamp.valueOf(source.getPartitionLocalDateTime()),
                source.getRecordData().toString()
            })
            .toList();

        int[] batchUpdateCounts = partitionJdbcTemplate.batchUpdate(
            insertSql,
            batchArgs
        );

        long insertedRowCount = 
            PartitionBatchUpdateCountUtils.sumBatchUpdateCounts(
                batchUpdateCounts,
                PartitionTableType.STAGING
            );

        return insertedRowCount;
    }
}