package com.example.myauthapp.partition.error;

import com.example.myauthapp.web.partition.error.PartitionError;

public enum PartitionTimePartitionError implements PartitionError{

    INVALID_PARTITION_TIME_RANGE(
        "INVALID_PARTITION_TIME_RANGE",
        "Error: rangeStartAt must be before rangeEndAt."
    );

    private final String errorCode;
    private final String errorMessage;

    PartitionTimePartitionError(
        String errorCode,
        String errorMessage
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}