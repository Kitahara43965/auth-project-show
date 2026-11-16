
package com.example.myauthapp.partition.fixture.service.concurrency;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.enums.type.PartitionConcurrencyType;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionConcurrencyTaskFixture;
import com.example.myauthapp.partition.assertion.discovery.discovery.count.TestPartitionCountGroupDiscoveryAssertion;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.repository.discovery.name
    .PartitionTargetPartitionNameDiscoveryJdbcRepository;

@Component
public class TestPartitionRecoveryAndCleanupConcurrencyServiceFixture {

    private static final int MAX_RECORD_COUNT = 1000;
    private static final int TIMEOUT_SECONDS = 30;
    private static final int MAX_CONCURRENT_TASK_COUNT = 1000;

    private final PartitionMetadataService partitionMetadataService;

    private final PartitionTargetPartitionNameDiscoveryJdbcRepository
        partitionTargetPartitionNameDiscoveryJdbcRepository;

    private final TestPartitionInsertRecordFixture
        testPartitionInsertRecordFixture;

    private final TestPartitionConcurrencyTaskFixture
        testPartitionConcurrencyTaskFixture;

    private final TestPartitionCountGroupDiscoveryAssertion
        testPartitionCountGroupDiscoveryAssertion;

    public TestPartitionRecoveryAndCleanupConcurrencyServiceFixture(
        PartitionMetadataService partitionMetadataService,
        PartitionTargetPartitionNameDiscoveryJdbcRepository
            partitionTargetPartitionNameDiscoveryJdbcRepository,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionConcurrencyTaskFixture testPartitionConcurrencyTaskFixture,
        TestPartitionCountGroupDiscoveryAssertion testPartitionCountGroupDiscoveryAssertion
    ) {
        this.partitionMetadataService = partitionMetadataService;
        this.partitionTargetPartitionNameDiscoveryJdbcRepository =
            partitionTargetPartitionNameDiscoveryJdbcRepository;
        this.testPartitionInsertRecordFixture =
            testPartitionInsertRecordFixture;
        this.testPartitionConcurrencyTaskFixture =
            testPartitionConcurrencyTaskFixture;
        this.testPartitionCountGroupDiscoveryAssertion =
            testPartitionCountGroupDiscoveryAssertion;
    }

    public void onRecoveryAndCleanupShouldHandleConcurrentExecutionWithoutDataCorruption(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord,
        LocalDateTime cleanupBefore
    ) throws PartitionException {

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

        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();

        LocalDateTime targetMonth =
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();

        // ① Partitionを準備

        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionContextGroup.getPartitionOperationPropertiesContext(),
                targetMonth
            );

        // ② DEFAULTに1000件投入

        testPartitionInsertRecordFixture
            .insertTestPartitionDefaultInsertRecord(
                partitionTablePropertiesContext,
                testPartitionDefaultInsertRecord,
                MAX_RECORD_COUNT
            );

        // ③ Recovery × Cleanupを同時実行

        List<Throwable> throwableErrors =
            testPartitionConcurrencyTaskFixture.executeConcurrent(
                partitionContextGroup,
                partitionRangeDTO,
                PartitionConcurrencyType.RECOVERY_AND_CLEANUP,
                null,
                MAX_CONCURRENT_TASK_COUNT,
                TIMEOUT_SECONDS,
                cleanupBefore
            );

        assertThat(throwableErrors)
            .allSatisfy(error -> {
                assertThat(error)
                    .isInstanceOf(PartitionException.class);

                PartitionException partitionException =
                    (PartitionException) error;

                assertThat(partitionException.getPartitionError())
                    .isEqualTo(
                        PartitionRecoveryPartitionError
                            .PARTITION_RANGE_IS_ALREADY_DEACTIVATED
                    );
            });

        // ④ 最終状態確認

        // RecoveryのOperation IDを取得する方法は、
        // executeConcurrentの戻り値・Operation検索仕様に合わせて追加する。

        testPartitionCountGroupDiscoveryAssertion
            .assertThatPartitionRecoveryAndCleanupAreCompleted(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );
    }
}
