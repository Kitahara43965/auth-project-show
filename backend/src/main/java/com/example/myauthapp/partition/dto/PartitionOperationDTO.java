package com.example.myauthapp.partition.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Objects;

import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;

public class PartitionOperationDTO {

    private final Long id;
    private final UUID publicPartitionOperationId;
    private final Long partitionTableId;
    private final Long partitionRangeId;
    private final LocalDateTime createdAt;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;
    private final LocalDateTime failedAt;
    private final String partitionOperationType;
    private final PartitionOperationStatus partitionOperationStatus;
    private final String errorMessage;
    private final Long defaultPartitionRowCount;
    private final Long stagingRowCount;

    public PartitionOperationDTO(
        Long id,
        UUID publicPartitionOperationId,
        Long partitionTableId,
        Long partitionRangeId,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDateTime failedAt,
        String partitionOperationType,
        PartitionOperationStatus partitionOperationStatus,
        String errorMessage,
        Long defaultPartitionRowCount,
        Long stagingRowCount
    ) {
        this.id = Objects.requireNonNull(
            id,
            "id must not be null"
        );
        this.publicPartitionOperationId = Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );
        this.partitionTableId = Objects.requireNonNull(
            partitionTableId,
            "partitionTableId must not be null"
        );
        this.partitionRangeId = Objects.requireNonNull(
            partitionRangeId,
            "partitionRangeId must not be null"
        );
        this.createdAt = Objects.requireNonNull(
            createdAt,
            "createdAt must not be null"
        );
        this.startedAt = Objects.requireNonNull(
            startedAt,
            "startedAt must not be null"
        );
        this.completedAt = completedAt;
        this.failedAt = failedAt;
        this.partitionOperationType = Objects.requireNonNull(
            partitionOperationType,
            "partitionOperationType must not be null"
        );
        this.partitionOperationStatus = Objects.requireNonNull(
            partitionOperationStatus,
            "partitionOperationStatus must not be null"
        );
        this.errorMessage = errorMessage;
        this.defaultPartitionRowCount = defaultPartitionRowCount;
        this.stagingRowCount = stagingRowCount;
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicPartitionOperationId() {
        return publicPartitionOperationId;
    }

    public Long getPartitionTableId() {
        return partitionTableId;
    }

    public Long getPartitionRangeId() {
        return partitionRangeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public String getPartitionOperationType() {
        return partitionOperationType;
    }

    public PartitionOperationStatus getPartitionOperationStatus() {
        return partitionOperationStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Long getDefaultPartitionRowCount() {
        return defaultPartitionRowCount;
    }

    public Long getStagingRowCount() {
        return stagingRowCount;
    }
}
