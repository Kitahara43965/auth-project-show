package com.example.myauthapp.partition.fixture.tool.database.cleanup;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionDefaultFailureTriggerDropFixture;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionStagingFailureTriggerDropFixture;

@Component
public class TestPartitionDatabaseTriggerCleanupFixture {

    private final TestPartitionDefaultFailureTriggerDropFixture
        testPartitionDefaultFailureTriggerDropFixture;
    private final TestPartitionStagingFailureTriggerDropFixture
        testPartitionStagingFailureTriggerDropFixture;

    public TestPartitionDatabaseTriggerCleanupFixture(
        TestPartitionDefaultFailureTriggerDropFixture
            testPartitionDefaultFailureTriggerDropFixture,
        TestPartitionStagingFailureTriggerDropFixture
            testPartitionStagingFailureTriggerDropFixture
    ) {
        this.testPartitionDefaultFailureTriggerDropFixture =
            testPartitionDefaultFailureTriggerDropFixture;
        this.testPartitionStagingFailureTriggerDropFixture =
            testPartitionStagingFailureTriggerDropFixture;
    }

    public void cleanup(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        testPartitionDefaultFailureTriggerDropFixture
            .dropDeleteDefaultFailureTrigger(
                partitionTablePropertiesContext
            );

        testPartitionStagingFailureTriggerDropFixture
            .dropDeleteStagingFailureTrigger(
                partitionTablePropertiesContext
            );
    }
}
