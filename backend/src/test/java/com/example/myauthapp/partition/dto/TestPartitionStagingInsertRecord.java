package com.example.myauthapp.partition.dto;

import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.PreparedStatementSetter;

public class TestPartitionStagingInsertRecord {
    
    private final Long recordId;
    private final List<String> columnNames;
    private final PreparedStatementSetter parameterBinder;
    private final LocalDateTime partitionLocalDateTime;

    public TestPartitionStagingInsertRecord(
        Long recordId,
        List<String> columnNames,
        PreparedStatementSetter parameterBinder,
        LocalDateTime partitionLocalDateTime
    ) {
        this.recordId = Objects.requireNonNull(
            recordId,
            "recordId must not be null"
        );

        this.columnNames = Objects.requireNonNull(
            columnNames,
            "columnNames must not be null"
        );

        this.parameterBinder = Objects.requireNonNull(
            parameterBinder,
            "parameterBinder must not be null"
        );

        this.partitionLocalDateTime = Objects.requireNonNull(
            partitionLocalDateTime,
            "partitionLocalDateTime must not be null"
        );
    }

    public Long getRecordId() {
        return recordId;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public PreparedStatementSetter getParameterBinder() {
        return parameterBinder;
    }

    public LocalDateTime getPartitionLocalDateTime(){
        return partitionLocalDateTime;
    }
}