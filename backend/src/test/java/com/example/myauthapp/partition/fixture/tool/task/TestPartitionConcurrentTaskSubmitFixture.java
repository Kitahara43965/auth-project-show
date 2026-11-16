package com.example.myauthapp.partition.fixture.tool.task;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.service.cleanup.PartitionCleanupService;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionConcurrencySynchronizationFixture;
@Component
public class TestPartitionConcurrentTaskSubmitFixture {

    private final PartitionOperationJdbcRepository partitionOperationJdbcRepository;
    private final PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService;
    private final PartitionCleanupService partitionCleanupService;
    private final TestPartitionErrorCollectionFixture testPartitionErrorCollectionFixture;
    private final TestPartitionConcurrencySynchronizationFixture
        testPartitionConcurrencySynchronizationFixture;

    public TestPartitionConcurrentTaskSubmitFixture(
        PartitionOperationJdbcRepository partitionOperationJdbcRepository,
        PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService,
        PartitionCleanupService partitionCleanupService,
        TestPartitionErrorCollectionFixture testPartitionErrorCollectionFixture,
        TestPartitionConcurrencySynchronizationFixture
            testPartitionConcurrencySynchronizationFixture
    ) {
        this.partitionOperationJdbcRepository = partitionOperationJdbcRepository;
        this.partitionRecoveryFacadeJdbcService = partitionRecoveryFacadeJdbcService;
        this.partitionCleanupService = partitionCleanupService;
        this.testPartitionErrorCollectionFixture = testPartitionErrorCollectionFixture;
        this.testPartitionConcurrencySynchronizationFixture =
            testPartitionConcurrencySynchronizationFixture;
    }

    public Future<Void> submitConcurrentRecoveryTask(
        PartitionContextGroup partitionContextGroup,
        PartitionRangeDTO partitionRangeDTO,
        ExecutorService executorService,
        CountDownLatch readyCountDownLatch,
        CountDownLatch startCountDownLatch
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );
        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );
        Objects.requireNonNull(
            executorService,
            "executorService must not be null"
        );
        Objects.requireNonNull(
            startCountDownLatch,
            "startCountDownLatch must not be null"
        );
        Objects.requireNonNull(
            readyCountDownLatch,
            "readyCountDownLatch must not be null"
        );

        return executorService.submit(() -> {

            UUID publicPartitionOperationId = null;

            try {

                PartitionOperationDTO partitionOperationDTO =
                    partitionOperationJdbcRepository.create(
                        partitionContextGroup
                            .getPartitionOperationPropertiesContext(),
                        partitionRangeDTO,
                        PartitionOperationStatus.RUNNING
                    );

                publicPartitionOperationId =
                    partitionOperationDTO.getPublicPartitionOperationId();
            } finally {
                readyCountDownLatch.countDown();
            }

            testPartitionConcurrencySynchronizationFixture.awaitConcurrentStart(
                startCountDownLatch,
                "Interrupted while waiting for concurrent recovery and retry"
            );

            partitionRecoveryFacadeJdbcService.executeRecoveryAndRecoveryRetry(
                partitionContextGroup,
                partitionRangeDTO,
                publicPartitionOperationId
            );

            return null;
        });
    }

    public Future<Void> submitConcurrentRetryTask(
        PartitionContextGroup partitionContextGroup,
        PartitionRangeDTO partitionRangeDTO,
        UUID failedPublicPartitionOperationId,
        ExecutorService executorService,
        CountDownLatch readyCountDownLatch,
        CountDownLatch startCountDownLatch
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );
        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );
        Objects.requireNonNull(
            failedPublicPartitionOperationId,
            "failedPublicPartitionOperationId must not be null"
        );
        Objects.requireNonNull(
            executorService,
            "executorService must not be null"
        );
        Objects.requireNonNull(
            startCountDownLatch,
            "startCountDownLatch must not be null"
        );
        Objects.requireNonNull(
            readyCountDownLatch,
            "readyCountDownLatch must not be null"
        );

        return executorService.submit(() -> {

            readyCountDownLatch.countDown();

            testPartitionConcurrencySynchronizationFixture.awaitConcurrentStart(
                startCountDownLatch,
                "Interrupted while waiting for concurrent retry"
            );

            partitionRecoveryFacadeJdbcService.executeRecoveryAndRecoveryRetry(
                partitionContextGroup,
                partitionRangeDTO,
                failedPublicPartitionOperationId
            );

            return null;
        });
    }

    public Future<Void> submitConcurrentCleanupTask(
        PartitionContextGroup partitionContextGroup,
        LocalDateTime cleanupBefore,
        ExecutorService executorService,
        CountDownLatch readyCountDownLatch,
        CountDownLatch startCountDownLatch
    ){
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );
        Objects.requireNonNull(
            executorService,
            "executorService must not be null"
        );
        Objects.requireNonNull(
            cleanupBefore,
            "cleanupBefore must not be null"
        );
        Objects.requireNonNull(
            startCountDownLatch,
            "startCountDownLatch must not be null"
        );
        Objects.requireNonNull(
            readyCountDownLatch,
            "readyCountDownLatch must not be null"
        );

        return executorService.submit(() -> {

            readyCountDownLatch.countDown();

            testPartitionConcurrencySynchronizationFixture.awaitConcurrentStart(
                startCountDownLatch,
                "Interrupted while waiting for concurrent cleanup"
            );

            partitionCleanupService.cleanupPartitionBefore(
                partitionContextGroup.getPartitionTablePropertiesContext(),
                cleanupBefore
            );

            return null;

        });
    }

}