package com.example.myauthapp.partition.fixture.tool.failure;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.resolver.PartitionNamingResolver;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.config.TestPartitionRecoveryFailureProperties;
import com.example.myauthapp.partition.repository.failure
    .TestPartitionBaseCreateFailureTriggerRepository;

@Component
public class TestPartitionDefaultFailureTriggerCreateFixture {

    private final JdbcTemplate auditJdbcTemplate;
    private final PartitionNamingResolver partitionNamingResolver;
    private final TestPartitionRecoveryFailureProperties
        testPartitionRecoveryFailureProperties;
    private final TestPartitionBaseCreateFailureTriggerRepository
        testPartitionBaseCreateFailureTriggerRepository;

    public TestPartitionDefaultFailureTriggerCreateFixture(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        PartitionNamingResolver partitionNamingResolver,
        TestPartitionRecoveryFailureProperties testPartitionRecoveryFailureProperties,
        TestPartitionBaseCreateFailureTriggerRepository
            testPartitionBaseCreateFailureTriggerRepository
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.partitionNamingResolver = partitionNamingResolver;
        this.testPartitionRecoveryFailureProperties =
            testPartitionRecoveryFailureProperties;
        this.testPartitionBaseCreateFailureTriggerRepository =
            testPartitionBaseCreateFailureTriggerRepository;
    }


    public void createDeleteDefaultFailureTrigger(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        
        String deleteDefaultFailureFunctionName=
            Objects.requireNonNull(
                testPartitionRecoveryFailureProperties
                    .getDeleteDefaultFailureFunctionName(),
                "deleteDefaultFailureFunctionName must not be null"
            );

        String deleteDefaultFailureTriggerName =
            Objects.requireNonNull(
                testPartitionRecoveryFailureProperties
                    .getDeleteDefaultFailureTriggerName(),
                "deleteDefaultFailureTriggerName must not be null"
            );

        String defaultPartitionName =
            Objects.requireNonNull(
                partitionNamingResolver.getDefaultPartitionName(
                    partitionTablePropertiesContext
                ),
                "defaultPartitionName must not be null"
            );

        testPartitionBaseCreateFailureTriggerRepository
            .createDeleteFailureTrigger(
                auditJdbcTemplate,
                deleteDefaultFailureFunctionName,
                deleteDefaultFailureTriggerName,
                defaultPartitionName,
                "Test failure: DELETE DEFAULT"
            );
    }
    
}