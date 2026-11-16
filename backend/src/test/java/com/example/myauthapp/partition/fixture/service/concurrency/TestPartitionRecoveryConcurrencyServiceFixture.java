package com.example.myauthapp.partition.fixture.service.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.partitionoperation.TestPartitionOperationDTODiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.discovery.count.TestPartitionCountGroupDiscoveryAssertion;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionConcurrencyTaskFixture;
import com.example.myauthapp.partition.enums.type.PartitionConcurrencyType;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;

@Component
public class TestPartitionRecoveryConcurrencyServiceFixture {

    private static final int MAX_RECORD_COUNT = 1000;
    private static final int MAX_CONCURRENT_TASK_COUNT = 1000;
    private static final int TIMEOUT_SECONDS = 30;
    private static final int MAX_RECOVERY_TIME_NUMBER = 5;

    private final PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService;
    private final PartitionMetadataService partitionMetadataService;
    private final PartitionOperationJdbcRepository partitionOperationJdbcRepository;
    private final PartitionOperationDTODiscoveryJdbcRepository partitionOperationDTODiscoveryJdbcRepository;
    private final TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion;
    private final TestPartitionCountGroupDiscoveryAssertion testPartitionCountGroupDiscoveryAssertion;
    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;
    private final TestPartitionConcurrencyTaskFixture testPartitionConcurrencyTaskFixture;

    TestPartitionRecoveryConcurrencyServiceFixture(
        PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService,
        PartitionMetadataService partitionMetadataService,
        PartitionOperationJdbcRepository partitionOperationJdbcRepository,
        PartitionOperationDTODiscoveryJdbcRepository partitionOperationDTODiscoveryJdbcRepository,
        TestPartitionCountDiscoveryAssertion
            testPartitionCountDiscoveryAssertion,
        TestPartitionCountGroupDiscoveryAssertion
            testPartitionCountGroupDiscoveryAssertion,
        TestPartitionInsertRecordFixture
            testPartitionInsertRecordFixture,
        TestPartitionConcurrencyTaskFixture
            testPartitionConcurrencyTaskFixture
    ) {
        this.partitionRecoveryFacadeJdbcService =
            partitionRecoveryFacadeJdbcService;
        this.partitionMetadataService =
            partitionMetadataService;
        this.partitionOperationJdbcRepository =
            partitionOperationJdbcRepository;
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;
        this.testPartitionCountDiscoveryAssertion =
            testPartitionCountDiscoveryAssertion;
        this.testPartitionCountGroupDiscoveryAssertion =
            testPartitionCountGroupDiscoveryAssertion;
        this.testPartitionInsertRecordFixture =
            testPartitionInsertRecordFixture;
        this.testPartitionConcurrencyTaskFixture =
            testPartitionConcurrencyTaskFixture;
    }

    public void onExecuteConcurrentRecoveryTest(
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

        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();

        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionContextGroup.getPartitionOperationPropertiesContext();

        PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext =
            partitionContextGroup.getPartitionRecoveryTableExpressionProviderContext();

        LocalDateTime targetMonth =
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();

        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                targetMonth
            );

        PartitionOperationDTO partitionOperationDTO =
            partitionOperationJdbcRepository.create(
                partitionOperationPropertiesContext,
                partitionRangeDTO,
                PartitionOperationStatus.RUNNING
            );

        // ここで取得する
        UUID publicPartitionOperationId =
            partitionOperationDTO.getPublicPartitionOperationId();

        testPartitionInsertRecordFixture.insertTestPartitionDefaultInsertRecord(
            partitionTablePropertiesContext,
            testPartitionDefaultInsertRecord,
            MAX_RECORD_COUNT
        );

        testPartitionCountDiscoveryAssertion.assertDefaultCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            MAX_RECORD_COUNT
        );

        for (int recoveryTimeNumber = 1;
            recoveryTimeNumber <= MAX_RECOVERY_TIME_NUMBER;
            recoveryTimeNumber++) {

            List<PartitionOperationDTO> partitionOperationDTOs =
                partitionOperationDTODiscoveryJdbcRepository
                    .findPartitionOperationDTOsByPartitionRangeId(
                        partitionRangeDTO.getId()
                    );

            List<Throwable> throwableErrors =
                testPartitionConcurrencyTaskFixture.executeConcurrent(
                    partitionContextGroup,
                    partitionRangeDTO,
                    PartitionConcurrencyType.RECOVERY,
                    null,
                    MAX_CONCURRENT_TASK_COUNT,
                    TIMEOUT_SECONDS,
                    null
                );

            assertThat(throwableErrors)
                .as("Recovery concurrency execution: %s", recoveryTimeNumber)
                .isEmpty();

            testPartitionCountGroupDiscoveryAssertion.assertThatPartitionRecoveryIsCompleted(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );

            // 単独Recovery
            partitionRecoveryFacadeJdbcService.executeRecoveryAndRecoveryRetry(
                partitionContextGroup,
                partitionRangeDTO,
                publicPartitionOperationId
            );

            // 単独Recovery完了確認
            testPartitionCountGroupDiscoveryAssertion.assertThatPartitionRecoveryIsCompleted(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );
        }
    }
}