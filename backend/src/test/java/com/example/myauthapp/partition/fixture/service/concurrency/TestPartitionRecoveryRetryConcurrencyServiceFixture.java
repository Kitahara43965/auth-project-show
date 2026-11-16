package com.example.myauthapp.partition.fixture.service.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.assertion.discovery.discovery.count
    .TestPartitionCountGroupDiscoveryAssertion;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.enums.type.PartitionConcurrencyType;
import com.example.myauthapp.partition.fixture.tool.create
    .TestPartitionRecoveryFailedOperationCreateServiceFixture;
import com.example.myauthapp.partition.fixture.tool.insert
    .TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionConcurrencyTaskFixture;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.web.partition.exception.PartitionException;

@Component
public class TestPartitionRecoveryRetryConcurrencyServiceFixture {

    private static final int MAX_RECORD_COUNT = 1000;
    private static final int MAX_CONCURRENT_TASK_COUNT = 1000;
    private static final int TIMEOUT_SECONDS = 30;

    private final PartitionRecoveryFacadeJdbcService
        partitionRecoveryFacadeJdbcService;

    private final PartitionMetadataService
        partitionMetadataService;

    private final TestPartitionCountGroupDiscoveryAssertion
        testPartitionCountGroupDiscoveryAssertion;

    private final TestPartitionInsertRecordFixture
        testPartitionInsertRecordFixture;

    private final TestPartitionRecoveryFailedOperationCreateServiceFixture
        testPartitionRecoveryFailedOperationCreateServiceFixture;

    private final TestPartitionConcurrencyTaskFixture
        testPartitionConcurrencyTaskFixture;

    TestPartitionRecoveryRetryConcurrencyServiceFixture(
        PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService,
        PartitionMetadataService partitionMetadataService,
        TestPartitionRecoveryFailedOperationCreateServiceFixture
            testPartitionRecoveryFailedOperationCreateServiceFixture,
        TestPartitionCountGroupDiscoveryAssertion testPartitionCountGroupDiscoveryAssertion,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionConcurrencyTaskFixture
            testPartitionConcurrencyTaskFixture
    ) {
        this.partitionRecoveryFacadeJdbcService =
            partitionRecoveryFacadeJdbcService;

        this.partitionMetadataService =
            partitionMetadataService;

        this.testPartitionRecoveryFailedOperationCreateServiceFixture =
            testPartitionRecoveryFailedOperationCreateServiceFixture;

        this.testPartitionCountGroupDiscoveryAssertion =
            testPartitionCountGroupDiscoveryAssertion;

        this.testPartitionInsertRecordFixture =
            testPartitionInsertRecordFixture;

        this.testPartitionConcurrencyTaskFixture =
            testPartitionConcurrencyTaskFixture;
    }


    public void onPartitionConcurrentRetryExecutionTest(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ) throws PartitionException {

        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        Objects.requireNonNull(
            testPartitionDefaultInsertRecord,
            "testPartitionDefaultInsertRecord must not be null"
        );

        PartitionTablePropertiesContext
            partitionTablePropertiesContext =
                partitionContextGroup
                    .getPartitionTablePropertiesContext();

        PartitionOperationPropertiesContext
            partitionOperationPropertiesContext =
                partitionContextGroup
                    .getPartitionOperationPropertiesContext();

        PartitionRecoveryTableExpressionProviderContext
            partitionRecoveryTableExpressionProviderContext =
                partitionContextGroup
                    .getPartitionRecoveryTableExpressionProviderContext();

        LocalDateTime targetMonth =
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();


        // ① Partitionを準備
        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                targetMonth
            );


        // ② DEFAULTに1000件投入
        testPartitionInsertRecordFixture.insertTestPartitionDefaultInsertRecord(
            partitionTablePropertiesContext,
            testPartitionDefaultInsertRecord,
            MAX_RECORD_COUNT
        );

        // ③ Recoveryを失敗させて
        //    新しいFAILED Operationを作る
        PartitionOperationDTO failedPartitionOperationDTO =
            testPartitionRecoveryFailedOperationCreateServiceFixture
                .createFailedPartitionOperationDTO(
                    partitionContextGroup,
                    targetMonth
                );

        UUID failedPublicPartitionOperationId =
            failedPartitionOperationDTO
                .getPublicPartitionOperationId();

        // ④ 同じFAILED Operationに対して
        //    1000並列Retry
        List<Throwable> throwableErrors =
            testPartitionConcurrencyTaskFixture.executeConcurrent(
                partitionContextGroup,
                partitionRangeDTO,
                PartitionConcurrencyType.RECOVERY_RETRY,
                failedPublicPartitionOperationId,
                MAX_CONCURRENT_TASK_COUNT,
                TIMEOUT_SECONDS,
                null
            );

        assertThat(throwableErrors).isEmpty();

        testPartitionCountGroupDiscoveryAssertion
            .assertThatPartitionRecoveryIsCompletedByPublicPartitionOperationId(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                failedPublicPartitionOperationId,
                MAX_RECORD_COUNT
            );

        partitionRecoveryFacadeJdbcService.executeRecoveryAndRecoveryRetry(
            partitionContextGroup,
            partitionRangeDTO,
            failedPublicPartitionOperationId
        );

        testPartitionCountGroupDiscoveryAssertion
            .assertThatPartitionRecoveryIsCompletedByPublicPartitionOperationId(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                failedPublicPartitionOperationId,
                MAX_RECORD_COUNT
            );

    }
}