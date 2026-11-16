package com.example.myauthapp.partition.fixture.tool.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.enums.type.PartitionConcurrencyType;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.service.cleanup.PartitionCleanupService;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionErrorCollectionFixture;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionConcurrencySynchronizationFixture;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionConcurrentTaskSubmitFixture;

@Component
public class TestPartitionConcurrencyTaskFixture {

    private static final long EXECUTOR_TERMINATION_TIMEOUT_SECONDS = 10L;

    private final PartitionOperationJdbcRepository partitionOperationJdbcRepository;
    private final PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService;
    private final PartitionCleanupService partitionCleanupService;
    private final TestPartitionErrorCollectionFixture testPartitionErrorCollectionFixture;
    private final TestPartitionConcurrencySynchronizationFixture
        testPartitionConcurrencySynchronizationFixture;
    private final TestPartitionConcurrentTaskSubmitFixture
        testPartitionConcurrentTaskSubmitFixture;

    public TestPartitionConcurrencyTaskFixture(
        PartitionOperationJdbcRepository partitionOperationJdbcRepository,
        PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService,
        PartitionCleanupService partitionCleanupService,
        TestPartitionErrorCollectionFixture testPartitionErrorCollectionFixture,
        TestPartitionConcurrencySynchronizationFixture
            testPartitionConcurrencySynchronizationFixture,
        TestPartitionConcurrentTaskSubmitFixture
            testPartitionConcurrentTaskSubmitFixture
    ) {
        this.partitionOperationJdbcRepository = partitionOperationJdbcRepository;
        this.partitionRecoveryFacadeJdbcService = partitionRecoveryFacadeJdbcService;
        this.partitionCleanupService = partitionCleanupService;
        this.testPartitionErrorCollectionFixture = testPartitionErrorCollectionFixture;
        this.testPartitionConcurrencySynchronizationFixture =
            testPartitionConcurrencySynchronizationFixture;
        this.testPartitionConcurrentTaskSubmitFixture =
            testPartitionConcurrentTaskSubmitFixture;
    }


    public List<Throwable> executeConcurrent(
        PartitionContextGroup partitionContextGroup,
        PartitionRangeDTO partitionRangeDTO,
        PartitionConcurrencyType partitionConcurrencyType,
        UUID failedPublicPartitionOperationId,
        int maxConcurrentTaskCount,
        int timeoutSeconds,
        LocalDateTime cleanupBefore
    ) throws PartitionException {

        CountDownLatch startCountDownLatch = new CountDownLatch(1);
        CountDownLatch readyCountDownLatch = null;
        ExecutorService executorService = null;
        List<Future<Void>> futures = new ArrayList<>();
        int loopTime = 0;
        int newFixedThreadPoolCount = 0;
        boolean hasRecovery = false;
        boolean hasRecoveryRetry = false;
        boolean hasCleanup = false;
        List<Throwable> throwableErrors = null;
        boolean allTasksReady = false;

        if(maxConcurrentTaskCount <= 0){
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .MAX_CONCURRENCY_TASK_COUNT_MUST_BE_GREATER_THAN_ZERO
            );
        }//maxConcurrentTaskCount&0

        if(timeoutSeconds <= 0){
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .TIMEOUT_SECONDS_MUST_BE_GREATER_THAN_ZERO
            );
        }//timeoutSeconds

        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeoutSeconds);
        long remainingTime = deadline - System.nanoTime();

        if(partitionConcurrencyType == PartitionConcurrencyType.RECOVERY){
            hasRecovery = true;
            hasRecoveryRetry = false;
            hasCleanup = false;
        }else if(partitionConcurrencyType == PartitionConcurrencyType.RECOVERY_RETRY){
            hasRecovery = false;
            hasRecoveryRetry = true;
            hasCleanup = false;
        }else if(partitionConcurrencyType == PartitionConcurrencyType.RECOVERY_AND_RECOVERY_RETRY){
            hasRecovery = true;
            hasRecoveryRetry = true;
            hasCleanup = false;
        }else if(partitionConcurrencyType == PartitionConcurrencyType.CLEANUP){
            hasRecovery = false;
            hasRecoveryRetry = false;
            hasCleanup = true;
        }else if(partitionConcurrencyType == PartitionConcurrencyType.RECOVERY_AND_CLEANUP){
            hasRecovery = true;
            hasRecoveryRetry = false;
            hasCleanup = true;
        }else if(partitionConcurrencyType == PartitionConcurrencyType.RECOVERY_RETRY_AND_CLEANUP){
            hasRecovery = false;
            hasRecoveryRetry = true;
            hasCleanup = true;
        }else if(partitionConcurrencyType == PartitionConcurrencyType.RECOVERY_AND_RECOVERY_RETRY_AND_CLEANUP){
            hasRecovery = true;
            hasRecoveryRetry = true;
            hasCleanup = true;
        }else{
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .UNSUPPORTED_PARTITION_CONCURRENCY_TYPE
            );
        }//partitionConcurrencyType

        try {
            for(loopTime = 1; loopTime <= 2; loopTime++){
                newFixedThreadPoolCount = 0;
                for (
                    int concurrentTaskCount = 1;
                    concurrentTaskCount <= maxConcurrentTaskCount;
                    concurrentTaskCount++
                ) {
                    // Recovery
                    if(hasRecovery){
                        newFixedThreadPoolCount++;
                        if(loopTime == 2){
                            futures.add(
                                testPartitionConcurrentTaskSubmitFixture
                                    .submitConcurrentRecoveryTask(
                                        partitionContextGroup,
                                        partitionRangeDTO,
                                        executorService,
                                        readyCountDownLatch,
                                        startCountDownLatch
                                    )
                            );
                        }//loopTime
                    }//hasRecovery

                    // RecoveryRetry
                    if(hasRecoveryRetry){
                        newFixedThreadPoolCount++;
                        if(loopTime == 2){
                            futures.add(
                                testPartitionConcurrentTaskSubmitFixture.
                                    submitConcurrentRetryTask(
                                        partitionContextGroup,
                                        partitionRangeDTO,
                                        failedPublicPartitionOperationId,
                                        executorService,
                                        readyCountDownLatch,
                                        startCountDownLatch
                                    )
                            );
                        }//loopTime
                    }//hasRecoveryRetry


                    // Cleanup
                    if(hasCleanup){
                        newFixedThreadPoolCount++;
                        if(loopTime == 2){
                            futures.add(
                                testPartitionConcurrentTaskSubmitFixture
                                    .submitConcurrentCleanupTask(
                                        partitionContextGroup,
                                        cleanupBefore,
                                        executorService,
                                        readyCountDownLatch,
                                        startCountDownLatch
                                    )
                            );
                        }//loopTime
                    }//hasRecoveryRetry


                }//concurrentTaskCount

                if(loopTime == 1){
                    if(newFixedThreadPoolCount >= 1){
                        executorService = Executors.newFixedThreadPool(newFixedThreadPoolCount);
                        readyCountDownLatch = new CountDownLatch(newFixedThreadPoolCount);
                    }//newFixedThreadPoolCount&1

                }else if(loopTime == 2){

                    testPartitionConcurrencySynchronizationFixture.
                        awaitConcurrentTasksReady(
                            readyCountDownLatch,
                            startCountDownLatch,
                            futures,
                            deadline
                        );

                    throwableErrors = 
                        testPartitionErrorCollectionFixture
                            .waitForConcurrentTasksAndCollectErrors(
                                futures,
                                deadline
                            );

                }//loopTime
            }//loopTime

            return(throwableErrors);

        } finally {

            if (executorService != null) {
                executorService.shutdownNow();

                try {
                    boolean isTerminated =
                        executorService.awaitTermination(
                            EXECUTOR_TERMINATION_TIMEOUT_SECONDS,
                            TimeUnit.SECONDS
                        );

                    if (!isTerminated) {
                        throw new PartitionException(
                            PartitionRecoveryPartitionError
                                .EXECUTOR_SERVICE_DID_NOT_TERMINATE,
                            EXECUTOR_TERMINATION_TIMEOUT_SECONDS
                        );
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        }
    }
}