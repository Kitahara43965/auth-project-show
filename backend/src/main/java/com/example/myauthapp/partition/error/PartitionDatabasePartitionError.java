package com.example.myauthapp.partition.error;

import com.example.myauthapp.web.partition.error.PartitionError;

public enum PartitionDatabasePartitionError implements PartitionError{

    OPERATION_WAS_NOT_UPDATED(
        "OPERATION_WAS_NOT_UPDATED",
        "Error: Operation was not updated. publicPartitionOperationId: %s"
    ),
    
    PARTITION_OPERATION_IS_NOT_AVAILABLE_FOR_RETRY(
        "PARTITION_OPERATION_IS_NOT_AVAILABLE_FOR_RETRY",
        "Error: Partition operation is not available for retry. publicPartitionOperationId: %s"
    ),
    
    ROW_COUNT_MUST_NOT_BE_NEGATIVE(
        "ROW_COUNT_MUST_NOT_BE_NEGATIVE",
        "Error: Row counts must not be negative."
    );

    private final String errorCode;
    private final String errorMessage;

    PartitionDatabasePartitionError(
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