package com.example.myauthapp.partition.util;

import java.util.Objects;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;

public final class PartitionLockKeyUtils {

    private PartitionLockKeyUtils() {
    }

    public static String getPartitionLockKey(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        return partitionTablePropertiesContext.getParentTableName()
            + ":"
            + partitionRangeDTO.getTargetPartitionName();
    }

}