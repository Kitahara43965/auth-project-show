package com.example.myauthapp.partition.error;

import com.example.myauthapp.web.partition.error.PartitionError;

public enum PartitionDependentDatabasePartitionError implements PartitionError{

    PARTITION_DROP_FAILED(
        "PARTITION_DROP_FAILED",
        "Error: Partition was not dropped: %s"
    );

    private final String errorCode;
    private final String errorMessage;

    PartitionDependentDatabasePartitionError(
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