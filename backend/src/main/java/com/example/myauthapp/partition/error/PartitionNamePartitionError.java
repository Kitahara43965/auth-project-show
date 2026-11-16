package com.example.myauthapp.partition.error;

import com.example.myauthapp.web.partition.error.PartitionError;

public enum PartitionNamePartitionError implements PartitionError{

    PARTITION_NAME_IS_INVALID(
        "PARTITION_NAME_IS_INVALID",
        "Error: partition name is invalid"
    );

    private final String errorCode;
    private final String errorMessage;

    PartitionNamePartitionError(
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