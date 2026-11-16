package com.example.myauthapp.partition.repository.failure;

import java.util.Objects;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.provider.PartitionStagingTableDefinitionProvider;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.config.TestPartitionRecoveryFailureProperties;

@Repository
public class TestPartitionTargetRecoveryFailureRepository {

    private final JdbcTemplate auditJdbcTemplate;
    private final PartitionStagingTableDefinitionProvider
        partitionStagingTableDefinitionProvider;
    private final TestPartitionRecoveryFailureProperties testPartitionRecoveryFailureProperties;

    public TestPartitionTargetRecoveryFailureRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        PartitionStagingTableDefinitionProvider partitionStagingTableDefinitionProvider,
        TestPartitionRecoveryFailureProperties testPartitionRecoveryFailureProperties
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.partitionStagingTableDefinitionProvider =
            partitionStagingTableDefinitionProvider;
        this.testPartitionRecoveryFailureProperties = testPartitionRecoveryFailureProperties;
    }

    public void createRecoveryFailureInducingTable(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        String recoveryFailureInducingTableName =
            Objects.requireNonNull(
                testPartitionRecoveryFailureProperties
                    .getRecoveryFailureInducingTableName(),
                "recoveryFailureInducingTableName must not be null"
            );

        LocalDateTime occurredAtBefore =
            Objects.requireNonNull(
                testPartitionRecoveryFailureProperties
                    .getOccurredAtBefore(),
                "occurredAtBefore must not be null"
            );

        String partitionLocalDateTimeColumnName =
            Objects.requireNonNull(
                partitionTablePropertiesContext
                    .getPartitionLocalDateTimeColumnName(),
                "partitionLocalDateTimeColumnName must not be null"
            );

        String targetPartitionName =
            Objects.requireNonNull(
                partitionRangeDTO.getTargetPartitionName(),
                "targetPartitionName must not be null"
            );

        String columnDefinitions =
            Objects.requireNonNull(
                partitionStagingTableDefinitionProvider
                .columnDefinitions(),
                "columnDefinitions must not be null"
            );

        auditJdbcTemplate.execute(
            """
            CREATE TABLE %s (
                %s,
                %s TIMESTAMP NOT NULL,

                CONSTRAINT %s
                    CHECK (
                        %s < TIMESTAMP '%s'
                    )
            )
            """.formatted(
                targetPartitionName,
                columnDefinitions,
                partitionLocalDateTimeColumnName,
                recoveryFailureInducingTableName,
                partitionLocalDateTimeColumnName,
                occurredAtBefore
            )
        );
    }

    public void dropRecoveryFailureInducingTable(
        PartitionRangeDTO partitionRangeDTO
    ) {
        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        String targetPartitionName =
            Objects.requireNonNull(
                partitionRangeDTO.getTargetPartitionName(),
                "targetPartitionName must not be null"
            );

        auditJdbcTemplate.execute(
            "DROP TABLE IF EXISTS %s"
                .formatted(targetPartitionName)
        );
    }
}