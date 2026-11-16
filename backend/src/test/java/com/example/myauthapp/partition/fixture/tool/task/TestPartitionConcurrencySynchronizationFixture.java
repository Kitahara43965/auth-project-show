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
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.enums.type.PartitionConcurrencyType;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;

@Component
public class TestPartitionConcurrencySynchronizationFixture {
    

    public void awaitConcurrentStart(
        CountDownLatch startCountDownLatch,
        String errorMessage
    ) {
        Objects.requireNonNull(
            startCountDownLatch,
            "startCountDownLatch must not be null"
        );

        Objects.requireNonNull(
            errorMessage,
            "errorMessage must not be null"
        );

        try {
            startCountDownLatch.await();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();

            throw new PartitionException(
                PartitionRecoveryPartitionError.CONCURRENCY_PARTITION_CREATION_FAILED,
                interruptedException,
                errorMessage
            );
        }
    }

    public void awaitConcurrentTasksReady(
        CountDownLatch readyCountDownLatch,
        CountDownLatch startCountDownLatch,
        List<Future<Void>> futures,
        long deadline
    ) throws PartitionException {

        Objects.requireNonNull(
            readyCountDownLatch,
            "readyCountDownLatch must not be null"
        );
        Objects.requireNonNull(
            startCountDownLatch,
            "startCountDownLatch must not be null"
        );
        Objects.requireNonNull(
            futures,
            "futures must not be null"
        );

        try {
            long remainingTime = deadline - System.nanoTime();

            if (remainingTime <= 0) {
                futures.forEach(future -> future.cancel(true));

                throw new PartitionException(
                    PartitionRecoveryPartitionError
                        .NOT_ALL_TASKS_BECAME_READY
                );
            }

            boolean allTasksReady = readyCountDownLatch.await(
                remainingTime,
                TimeUnit.NANOSECONDS
            );

            if (!allTasksReady) {
                futures.forEach(future -> future.cancel(true));

                throw new PartitionException(
                    PartitionRecoveryPartitionError
                        .NOT_ALL_TASKS_BECAME_READY
                );
            }

        } catch (InterruptedException e) {
            futures.forEach(future -> future.cancel(true));

            Thread.currentThread().interrupt();

            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .CONCURRENT_TASKS_INTERRUPTED,
                e
            );

        } finally {
            // Ready待機の成否にかかわらず、待機中のタスクを解放する
            startCountDownLatch.countDown();
        }
    }

}