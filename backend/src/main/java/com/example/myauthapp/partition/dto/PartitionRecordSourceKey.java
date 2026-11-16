package com.example.myauthapp.partition.dto;

import java.util.Objects;
import java.time.LocalDateTime;

public final class PartitionRecordSourceKey {

    private final Long recordId;
    private final LocalDateTime partitionLocalDateTime;

    public PartitionRecordSourceKey(
        Long recordId,
        LocalDateTime partitionLocalDateTime
    ) {
        this.recordId = Objects.requireNonNull(
            recordId,
            "recordId must not be null"
        );
        this.partitionLocalDateTime = Objects.requireNonNull(
            partitionLocalDateTime,
            "partitionLocalDateTime must not be null"
        );
    }

    public Long getRecordId() {
        return recordId;
    }

    public LocalDateTime getPartitionLocalDateTime(){
        return partitionLocalDateTime;
    }

}