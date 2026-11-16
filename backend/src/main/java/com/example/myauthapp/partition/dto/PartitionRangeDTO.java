package com.example.myauthapp.partition.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionTimePartitionError;

public class PartitionRangeDTO {

    private final Long id;
    private final Long partitionTableId;
    private final LocalDateTime createdAt;
    private final LocalDateTime deactivatedAt;
    private final LocalDateTime rangeStartAt;
    private final LocalDateTime rangeEndAt;
    private final String targetPartitionName;

    public PartitionRangeDTO(
        Long id,
        Long partitionTableId,
        LocalDateTime createdAt,
        LocalDateTime deactivatedAt,
        LocalDateTime rangeStartAt,
        LocalDateTime rangeEndAt,
        String targetPartitionName
    ) {
        this.id = Objects.requireNonNull(
            id,
            "id must not be null"
        );

        this.partitionTableId = Objects.requireNonNull(
            partitionTableId,
            "partitionTableId must not be null"
        );

        this.createdAt = Objects.requireNonNull(
            createdAt,
            "createdAt must not be null"
        );

        this.deactivatedAt = deactivatedAt;

        this.rangeStartAt = Objects.requireNonNull(
            rangeStartAt,
            "rangeStartAt must not be null"
        );

        this.rangeEndAt = Objects.requireNonNull(
            rangeEndAt,
            "rangeEndAt must not be null"
        );

        this.targetPartitionName = Objects.requireNonNull(
            targetPartitionName,
            "targetPartitionName must not be null"
        );

        if (!rangeStartAt.isBefore(rangeEndAt)) {
            throw new PartitionException(
                PartitionTimePartitionError.INVALID_PARTITION_TIME_RANGE
            );
        }
    }

    public Long getId() {
        return id;
    }

    public Long getPartitionTableId() {
        return partitionTableId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    public LocalDateTime getRangeStartAt() {
        return rangeStartAt;
    }

    public LocalDateTime getRangeEndAt() {
        return rangeEndAt;
    }

    public String getTargetPartitionName() {
        return targetPartitionName;
    }

    public boolean isDeactivated() {
        return deactivatedAt != null;
    }
}