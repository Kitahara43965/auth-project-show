package com.example.myauthapp.partition.dto;

import java.util.Objects;
import java.util.UUID;

import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;

public class PartitionRecoverStalePartitionOperationResult {

    private final Long partitionRangeId;
    private final UUID publicPartitionOperationId;
    private final PartitionOperationStatus partitionOperationStatus;

    public PartitionRecoverStalePartitionOperationResult(
        Long partitionRangeId,
        UUID publicPartitionOperationId,
        PartitionOperationStatus partitionOperationStatus
    ) {

        this.partitionRangeId =
            Objects.requireNonNull(
                partitionRangeId,
                "partitionRangeId must not be null"
            );

        this.publicPartitionOperationId =
            Objects.requireNonNull(
                publicPartitionOperationId,
                "publicPartitionOperationId must not be null"
            );

        this.partitionOperationStatus =
            Objects.requireNonNull(
                partitionOperationStatus,
                "partitionOperationStatus must not be null"
            );
    }

    public Long getPartitionRangeId() {
        return partitionRangeId;
    }

    public UUID getPublicPartitionOperationId() {
        return publicPartitionOperationId;
    }

    public PartitionOperationStatus getPartitionOperationStatus() {
        return partitionOperationStatus;
    }
}