package com.example.myauthapp.partition.repository.failure;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.resolver.PartitionNamingResolver;
import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.config.TestPartitionRecoveryFailureProperties;

@Repository
public class TestPartitionBaseDropFailureTriggerRepository {

    private final PartitionNamingResolver partitionNamingResolver;
    private final PartitionProperties partitionProperties;
    private final TestPartitionRecoveryFailureProperties
        testPartitionRecoveryFailureProperties;

    public TestPartitionBaseDropFailureTriggerRepository(
        PartitionNamingResolver partitionNamingResolver,
        PartitionProperties partitionProperties,
        TestPartitionRecoveryFailureProperties testPartitionRecoveryFailureProperties
    ) {
        this.partitionNamingResolver = partitionNamingResolver;
        this.partitionProperties = partitionProperties;
        this.testPartitionRecoveryFailureProperties =
            testPartitionRecoveryFailureProperties;
    }

    public void dropDeleteFailureTrigger(
        JdbcTemplate jdbcTemplate,
        String functionName,
        String triggerName,
        String tableName
    ) {
        jdbcTemplate.execute(
            """
            DROP TRIGGER IF EXISTS %s ON %s
            """.formatted(
                triggerName,
                tableName
            )
        );

        jdbcTemplate.execute(
            """
            DROP FUNCTION IF EXISTS %s()
            """.formatted(functionName)
        );
    }
}