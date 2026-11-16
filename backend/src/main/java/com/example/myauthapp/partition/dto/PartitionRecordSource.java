package com.example.myauthapp.partition.dto;

import java.util.Objects;
import java.time.LocalDateTime;

import tools.jackson.databind.JsonNode;

public final class PartitionRecordSource {

    private final Long recordId;
    private final LocalDateTime partitionLocalDateTime;
    private final JsonNode recordData;

    public PartitionRecordSource(
        Long recordId,
        LocalDateTime partitionLocalDateTime,
        JsonNode recordData
    ) {
        this.recordId = Objects.requireNonNull(
            recordId,
            "recordId must not be null"
        );
        this.partitionLocalDateTime = Objects.requireNonNull(
            partitionLocalDateTime,
            "partitionLocalDateTime must not be null"
        );
        this.recordData = Objects.requireNonNull(
            recordData,
            "recordData must not be null"
        );
    }

    public Long getRecordId() {
        return recordId;
    }

    public LocalDateTime getPartitionLocalDateTime(){
        return partitionLocalDateTime;
    }

    public JsonNode getRecordData() {
        return recordData;
    }

}