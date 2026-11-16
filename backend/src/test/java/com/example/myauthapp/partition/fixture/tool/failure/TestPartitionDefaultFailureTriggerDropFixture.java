package com.example.myauthapp.partition.fixture.tool.failure;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.config.TestPartitionRecoveryFailureProperties;
import com.example.myauthapp.partition.repository.failure.TestPartitionBaseDropFailureTriggerRepository;
import com.example.myauthapp.partition.resolver.PartitionNamingResolver;

@Repository
public class TestPartitionDefaultFailureTriggerDropFixture {

    private final JdbcTemplate auditJdbcTemplate;
    private final PartitionNamingResolver partitionNamingResolver;
    private final TestPartitionRecoveryFailureProperties
        testPartitionRecoveryFailureProperties;
    private final TestPartitionBaseDropFailureTriggerRepository
        testPartitionBaseDropFailureTriggerRepository;

    public TestPartitionDefaultFailureTriggerDropFixture(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate,
        PartitionNamingResolver
            partitionNamingResolver,
        TestPartitionRecoveryFailureProperties
            testPartitionRecoveryFailureProperties,
        TestPartitionBaseDropFailureTriggerRepository
            testPartitionBaseDropFailureTriggerRepository
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
        this.partitionNamingResolver =
            partitionNamingResolver;
        this.testPartitionRecoveryFailureProperties =
            testPartitionRecoveryFailureProperties;
        this.testPartitionBaseDropFailureTriggerRepository =
            testPartitionBaseDropFailureTriggerRepository;
    }


    public void dropDeleteDefaultFailureTrigger(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        String functionName =
            Objects.requireNonNull(
                testPartitionRecoveryFailureProperties
                    .getDeleteDefaultFailureFunctionName(),
                "deleteDefaultFailureFunctionName must not be null"
            );

        String triggerName =
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

        testPartitionBaseDropFailureTriggerRepository
            .dropDeleteFailureTrigger(
                auditJdbcTemplate,
                functionName,
                triggerName,
                defaultPartitionName
            );
    }

}