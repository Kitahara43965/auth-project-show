package com.example.myauthapp.partition.fixture.tool.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Component;

import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;

@Component
public class TestPartitionErrorCollectionFixture {

    public List<Throwable> waitForConcurrentTasksAndCollectErrors(
        List<Future<Void>> futures,
        long deadline
    ) throws PartitionException {

        Objects.requireNonNull(futures, "futures must not be null");

        List<Throwable> throwableErrors = new ArrayList<>();

        for (Future<Void> future : futures) {

            Objects.requireNonNull(
                future, 
                "future must not be null"
            );

            try {
                long remainingTime = deadline - System.nanoTime();

                if (remainingTime <= 0) {
                    futures.forEach(futureTask -> futureTask.cancel(true));

                    throw new PartitionException(
                        PartitionRecoveryPartitionError
                            .CONCURRENT_TASKS_TIME_OUT
                    );
                }

                future.get(remainingTime, TimeUnit.NANOSECONDS);

            } catch (ExecutionException e) {
                throwableErrors.add(
                    e.getCause() != null ? e.getCause() : e
                );

            } catch (TimeoutException e) {
                futures.forEach(futureTask -> futureTask.cancel(true));

                throw new PartitionException(
                    PartitionRecoveryPartitionError
                        .CONCURRENT_TASKS_TIME_OUT,
                    e
                );

            } catch (InterruptedException e) {
                futures.forEach(futureTask -> futureTask.cancel(true));

                Thread.currentThread().interrupt();

                throw new PartitionException(
                    PartitionRecoveryPartitionError
                        .CONCURRENT_TASKS_INTERRUPTED,
                    e
                );
            }
        }//future

        return throwableErrors;
    }

}