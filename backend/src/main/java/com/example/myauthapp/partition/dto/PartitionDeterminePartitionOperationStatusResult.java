package com.example.myauthapp.partition.dto;

import java.util.UUID;
import java.util.Objects;

import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;

public class PartitionDeterminePartitionOperationStatusResult {

    private final PartitionOperationStatus partitionOperationStatus;
    private final long defaultPartitionRowCount;
    private final long stagingRowCount;
    private final long targetPartitionRowCount;

    public PartitionDeterminePartitionOperationStatusResult(
        PartitionOperationStatus partitionOperationStatus,
        long defaultPartitionRowCount,
        long stagingRowCount,
        long targetPartitionRowCount
    ) {
        this.partitionOperationStatus = 
            Objects.requireNonNull(
                partitionOperationStatus,
                "partitionOperationStatus must not be null"
            );
        this.defaultPartitionRowCount = defaultPartitionRowCount;
        this.stagingRowCount = stagingRowCount;
        this.targetPartitionRowCount = targetPartitionRowCount;
    }

    public PartitionOperationStatus getPartitionOperationStatus() {
        return partitionOperationStatus;
    }

    public long getDefaultPartitionRowCount() {
        return defaultPartitionRowCount;
    }

    public long getStagingRowCount() {
        return stagingRowCount;
    }

    public long countTargetPartitionRowCount() {
        return targetPartitionRowCount;
    }
}