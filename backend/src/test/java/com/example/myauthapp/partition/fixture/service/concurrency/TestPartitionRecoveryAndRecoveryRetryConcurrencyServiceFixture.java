package com.example.myauthapp.partition.fixture.service.concurrency;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.enums.type.PartitionConcurrencyType;
import com.example.myauthapp.partition.fixture.tool.create.TestPartitionRecoveryFailedOperationCreateServiceFixture;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionConcurrencyTaskFixture;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.assertion.discovery.discovery.count.TestPartitionCountGroupDiscoveryAssertion;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.service.debug.PartitionDebugCountJdbcService;

@Component
public class TestPartitionRecoveryAndRecoveryRetryConcurrencyServiceFixture {

    private static final int MAX_RECORD_COUNT = 1000;
    private static final int TIMEOUT_SECONDS = 30;
    private static final int MAX_CONCURRENT_TASK_COUNT = 1000;

    private final PartitionMetadataService partitionMetadataService;

    private final PartitionDebugCountJdbcService
        partitionDebugCountJdbcService;

    private final TestPartitionCountGroupDiscoveryAssertion testPartitionCountGroupDiscoveryAssertion;

    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;

    private final TestPartitionRecoveryFailedOperationCreateServiceFixture
        testPartitionRecoveryFailedOperationCreateServiceFixture;

    private final TestPartitionConcurrencyTaskFixture
        testPartitionConcurrencyTaskFixture;

    TestPartitionRecoveryAndRecoveryRetryConcurrencyServiceFixture(
        PartitionMetadataService partitionMetadataService,
        PartitionDebugCountJdbcService partitionDebugCountJdbcService,
        TestPartitionCountGroupDiscoveryAssertion testPartitionCountGroupDiscoveryAssertion,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionRecoveryFailedOperationCreateServiceFixture
            testPartitionRecoveryFailedOperationCreateServiceFixture,
        TestPartitionConcurrencyTaskFixture testPartitionConcurrencyTaskFixture
    ) {
        this.partitionMetadataService =
            partitionMetadataService;

        this.partitionDebugCountJdbcService =
            partitionDebugCountJdbcService;

        this.testPartitionCountGroupDiscoveryAssertion =
            testPartitionCountGroupDiscoveryAssertion;

        this.testPartitionInsertRecordFixture =
            testPartitionInsertRecordFixture;

        this.testPartitionRecoveryFailedOperationCreateServiceFixture =
            testPartitionRecoveryFailedOperationCreateServiceFixture;

        this.testPartitionConcurrencyTaskFixture =
            testPartitionConcurrencyTaskFixture;

    }


    public void onRecoveryAndRetryShouldNotCorruptPartitionData(
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

        // ② DEFAULTに1000件投入

        testPartitionInsertRecordFixture
            .insertTestPartitionDefaultInsertRecord(
                partitionTablePropertiesContext,
                testPartitionDefaultInsertRecord,
                MAX_RECORD_COUNT
            );

        // ③ FAILED Operationを1件作る

        PartitionOperationDTO failedPartitionOperationDTO =
            testPartitionRecoveryFailedOperationCreateServiceFixture
                .createFailedPartitionOperationDTO(
                    partitionContextGroup,
                    targetMonth
                );

        UUID failedPublicPartitionOperationId =
            Objects.requireNonNull(
                failedPartitionOperationDTO
                    .getPublicPartitionOperationId(),
                "publicPartitionOperationId must not be null"
            );

        // ④ Recovery × Retryを同時実行

        List<Throwable> throwableErrors =
            testPartitionConcurrencyTaskFixture.executeConcurrent(
                partitionContextGroup,
                partitionRangeDTO,
                PartitionConcurrencyType.RECOVERY_AND_RECOVERY_RETRY,
                failedPublicPartitionOperationId,
                MAX_CONCURRENT_TASK_COUNT,
                TIMEOUT_SECONDS,
                null
            );

        assertThat(throwableErrors).isEmpty();

        // ⑤ 最終状態確認

        testPartitionCountGroupDiscoveryAssertion
            .assertThatPartitionRecoveryIsCompletedByPublicPartitionOperationId(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                failedPublicPartitionOperationId,
                MAX_RECORD_COUNT
            );

    }
}