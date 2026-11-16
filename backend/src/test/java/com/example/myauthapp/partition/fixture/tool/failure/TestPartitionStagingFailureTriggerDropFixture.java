package com.example.myauthapp.partition.fixture.tool.failure;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.config.TestPartitionRecoveryFailureProperties;
import com.example.myauthapp.partition.repository.failure.TestPartitionBaseDropFailureTriggerRepository;

@Repository
public class TestPartitionStagingFailureTriggerDropFixture {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;
    private final TestPartitionRecoveryFailureProperties
        testPartitionRecoveryFailureProperties;
    private final TestPartitionBaseDropFailureTriggerRepository
        testPartitionBaseDropFailureTriggerRepository;

    public TestPartitionStagingFailureTriggerDropFixture(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties,
        TestPartitionRecoveryFailureProperties
            testPartitionRecoveryFailureProperties,
        TestPartitionBaseDropFailureTriggerRepository
            testPartitionBaseDropFailureTriggerRepository
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
        this.testPartitionRecoveryFailureProperties =
            testPartitionRecoveryFailureProperties;
        this.testPartitionBaseDropFailureTriggerRepository =
            testPartitionBaseDropFailureTriggerRepository;
    }

    public void dropDeleteStagingFailureTrigger(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        String functionName =
            Objects.requireNonNull(
                testPartitionRecoveryFailureProperties
                    .getDeleteStagingFailureFunctionName(),
                "deleteStagingFailureFunctionName must not be null"
            );

        String triggerName =
            Objects.requireNonNull(
                testPartitionRecoveryFailureProperties
                    .getDeleteStagingFailureTriggerName(),
                "deleteStagingFailureTriggerName must not be null"
            );

        String partitionStagingTableName =
            Objects.requireNonNull(
                partitionProperties.getPartitionStagingTableName(),
                "partitionStagingTableName must not be null"
            );

        testPartitionBaseDropFailureTriggerRepository
            .dropDeleteFailureTrigger(
                partitionJdbcTemplate,
                functionName,
                triggerName,
                partitionStagingTableName
            );
    }
}