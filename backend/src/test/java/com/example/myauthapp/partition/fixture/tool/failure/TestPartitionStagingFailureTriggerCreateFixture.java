package com.example.myauthapp.partition.fixture.tool.failure;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.config.TestPartitionRecoveryFailureProperties;
import com.example.myauthapp.partition.repository.failure
    .TestPartitionBaseCreateFailureTriggerRepository;

@Component
public class TestPartitionStagingFailureTriggerCreateFixture {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;
    private final TestPartitionRecoveryFailureProperties
        testPartitionRecoveryFailureProperties;
    private final TestPartitionBaseCreateFailureTriggerRepository
        testPartitionBaseCreateFailureTriggerRepository;

    public TestPartitionStagingFailureTriggerCreateFixture(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties,
        TestPartitionRecoveryFailureProperties
            testPartitionRecoveryFailureProperties,
        TestPartitionBaseCreateFailureTriggerRepository
            testPartitionBaseCreateFailureTriggerRepository
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
        this.testPartitionRecoveryFailureProperties =
            testPartitionRecoveryFailureProperties;
        this.testPartitionBaseCreateFailureTriggerRepository =
            testPartitionBaseCreateFailureTriggerRepository;
    }


    public void createDeleteStagingFailureTrigger(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        String deleteStagingFailureFunctionName =
            Objects.requireNonNull(
                testPartitionRecoveryFailureProperties
                    .getDeleteStagingFailureFunctionName(),
                "deleteStagingFailureFunctionName must not be null"
            );

        String deleteStagingFailureTriggerName =
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

        testPartitionBaseCreateFailureTriggerRepository
            .createDeleteFailureTrigger(
                partitionJdbcTemplate,
                deleteStagingFailureFunctionName,
                deleteStagingFailureTriggerName,
                partitionStagingTableName,
                "Test failure: DELETE STAGING"
            );
    }
}