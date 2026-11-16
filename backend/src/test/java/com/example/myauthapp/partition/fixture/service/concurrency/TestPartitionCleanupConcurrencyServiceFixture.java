package com.example.myauthapp.partition.fixture.service.concurrency;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.stereotype.Component;
import org.junit.jupiter.api.Assertions;

import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.enums.type.PartitionConcurrencyType;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionConcurrencyTaskFixture;
import com.example.myauthapp.partition.repository.create.PartitionTargetCreateJdbcRepository;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.assertion.deactivate.TestPartitionRangeDeactivateAssertion;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;

@Component
public class TestPartitionCleanupConcurrencyServiceFixture {

    private static final int MAX_RECORD_COUNT = 1000;
    private static final int TIMEOUT_SECONDS = 30;
    private static final int MAX_CONCURRENT_TASK_COUNT = 1000;

    private final PartitionMetadataService partitionMetadataService;

    private final PartitionTargetCreateJdbcRepository
        partitionTargetCreateJdbcRepository;

    private final TestPartitionInsertRecordFixture
        testPartitionInsertRecordFixture;

    private final TestPartitionConcurrencyTaskFixture
        testPartitionConcurrencyTaskFixture;

    private final TestPartitionTargetPartitionNameDiscoveryAssertion
        testPartitionTargetPartitionNameDiscoveryAssertion;

    private final TestPartitionRangeDeactivateAssertion
        testPartitionRangeDeactivateAssertion;

    public TestPartitionCleanupConcurrencyServiceFixture(
        PartitionMetadataService partitionMetadataService,
        PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionConcurrencyTaskFixture testPartitionConcurrencyTaskFixture,
        TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion,
        TestPartitionRangeDeactivateAssertion testPartitionRangeDeactivateAssertion
    ) {
        this.partitionMetadataService = partitionMetadataService;
        this.partitionTargetCreateJdbcRepository =
            partitionTargetCreateJdbcRepository;
        this.testPartitionInsertRecordFixture =
            testPartitionInsertRecordFixture;
        this.testPartitionConcurrencyTaskFixture =
            testPartitionConcurrencyTaskFixture;
        this.testPartitionTargetPartitionNameDiscoveryAssertion =
            testPartitionTargetPartitionNameDiscoveryAssertion;
        this.testPartitionRangeDeactivateAssertion =
            testPartitionRangeDeactivateAssertion;
    }


    public void onCleanupPartitionBeforeShouldHandleConcurrentCleanup(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord,
        LocalDateTime cleanupBefore
    ){

        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        Objects.requireNonNull(
            testPartitionDefaultInsertRecord,
            "testPartitionDefaultInsertRecord must not be null"
        );

        Objects.requireNonNull(
            cleanupBefore,
            "cleanupBefore must not be null"
        );

        PartitionTablePropertiesContext
            partitionTablePropertiesContext =
                partitionContextGroup.getPartitionTablePropertiesContext();

        PartitionOperationPropertiesContext
            partitionOperationPropertiesContext =
                partitionContextGroup.getPartitionOperationPropertiesContext();

        LocalDateTime targetMonth =
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();

        // ① Partitionを準備

        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                targetMonth
            );

        partitionTargetCreateJdbcRepository.createPartition(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            UUID.randomUUID()
        );

        List<Throwable> errors =
            testPartitionConcurrencyTaskFixture.executeConcurrent(
                partitionContextGroup,
                partitionRangeDTO,
                PartitionConcurrencyType.CLEANUP,
                null,
                MAX_CONCURRENT_TASK_COUNT,
                TIMEOUT_SECONDS,
                cleanupBefore
            );

        Assertions.assertTrue(
            errors.isEmpty(),
            "Concurrent Cleanup should complete without errors"
        );

        // 対象パーティションが削除されたことを確認
        testPartitionTargetPartitionNameDiscoveryAssertion
            .assertThatTargetPartitionDoesNotExist(
                partitionContextGroup.getPartitionTablePropertiesContext(),
                partitionRangeDTO
            );

        assertThat(errors)
            .as("Concurrent cleanup errors")
            .isEmpty();

        // メタデータが無効化されたことも確認
        testPartitionRangeDeactivateAssertion
            .assertThatActualPartitionRangeDTOIsDeactivated(
                partitionContextGroup.getPartitionTablePropertiesContext(),
                partitionRangeDTO
            );
    }
}//TestPartitionCleanupConcurrencyServiceFixture