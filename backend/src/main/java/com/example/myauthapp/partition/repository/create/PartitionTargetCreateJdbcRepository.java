package com.example.myauthapp.partition.repository.create;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;

@Repository
public class PartitionTargetCreateJdbcRepository {

    private final JdbcTemplate auditJdbcTemplate;

    public PartitionTargetCreateJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
    }

    public void createPartition(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO,
        UUID publicPartitionOperationId
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext, 
            "partitionTablePropertiesContext must not be null"
        );
        Objects.requireNonNull(
            partitionRangeDTO, 
            "partitionRangeDTO must not be null"
        );

        auditJdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS %s
            PARTITION OF %s
            FOR VALUES FROM ('%s') TO ('%s')
            """.formatted(
                partitionRangeDTO.getTargetPartitionName(),
                partitionTablePropertiesContext.getParentTableName(),
                Objects.requireNonNull(
                    partitionRangeDTO.getRangeStartAt(),
                    "rangeStartAt must not be null"
                ),
                Objects.requireNonNull(
                    partitionRangeDTO.getRangeEndAt(),
                    "rangeEndAt must not be null"
                )
            )
        );
    }
}
