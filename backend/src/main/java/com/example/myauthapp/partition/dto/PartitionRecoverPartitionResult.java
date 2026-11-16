package com.example.myauthapp.partition.dto;

import java.util.UUID;

public class PartitionRecoverPartitionResult {

    private final UUID publicPartitionOperationId;
    private final long defaultPartitionRowCount;
    private final long stagingRowCount;

    public PartitionRecoverPartitionResult(
        UUID publicPartitionOperationId,
        long defaultPartitionRowCount,
        long stagingRowCount
    ) {
        this.publicPartitionOperationId = publicPartitionOperationId;
        this.defaultPartitionRowCount = defaultPartitionRowCount;
        this.stagingRowCount = stagingRowCount;
    }

    public UUID getPublicPartitionOperationId() {
        return publicPartitionOperationId;
    }

    public long getDefaultPartitionRowCount() {
        return defaultPartitionRowCount;
    }

    public long getStagingRowCount() {
        return stagingRowCount;
    }
}