package com.example.myauthapp.partition.util;

import java.util.Objects;

import com.example.myauthapp.partition.enums.type.PartitionTableType;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;

public final class PartitionBatchUpdateCountUtils {

    private PartitionBatchUpdateCountUtils() {
    }

    public static long sumBatchUpdateCounts(
        int[] batchUpdateCounts,
        PartitionTableType partitionTableType
    ) {
        Objects.requireNonNull(
            batchUpdateCounts,
            "batchUpdateCounts must not be null"
        );
        Objects.requireNonNull(
            partitionTableType,
            "partitionTableType must not be null"
        );

        long insertedRowCount = 0L;

        for (int updateCount : batchUpdateCounts) {
            if (updateCount == java.sql.Statement.EXECUTE_FAILED) {
                throw new PartitionException(
                    PartitionRecoveryPartitionError.INSERT_FAILED,
                    partitionTableType.name()
                );
            }

            if (updateCount == java.sql.Statement.SUCCESS_NO_INFO) {
                throw new PartitionException(
                    PartitionRecoveryPartitionError.INSERT_COUNT_UNKNOWN,
                    partitionTableType.name()
                );
            }

            insertedRowCount = insertedRowCount + updateCount;
        }

        return insertedRowCount;
    }
}