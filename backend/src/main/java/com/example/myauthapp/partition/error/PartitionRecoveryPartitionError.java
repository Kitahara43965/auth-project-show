package com.example.myauthapp.partition.error;

import com.example.myauthapp.web.partition.error.PartitionError;

public enum PartitionRecoveryPartitionError implements PartitionError{

    FAILED_TO_PARSE_RECOVERY_RECORD_DATA(
        "FAILED_TO_PARSE_RECOVERY_RECORD_DATA",
        "Error: failed to parse recovery record data %s"
    ),
    
    PARTITION_RANGE_IS_NOT_FOUND(
        "PARTITION_RANGE_IS_NOT_FOUND",
        "Error: Partition range is not found."
    ),
    
    RECOVERY_SOURCE_COUNT_MISMATCH(
        "RECOVERY_SOURCE_COUNT_MISMATCH",
        "Error: DEFAULT and STAGING recovery source counts do not match."
        +"publicPartitionOperationId=%s, defaultPartitionRowCount=%d, stagingRowCount=%d"
    ),

    RECOVERY_SOURCE_RECORD_ID_MISMATCH(
        "RECOVERY_SOURCE_RECORD_ID_MISMATCH",
        "Error: DEFAULT and STAGING record IDs do not match."
        +"publicPartitionOperationId=%s, defaultRecordId=%s, stagingRecordId=%s"
    ),

    RECOVERY_SOURCE_RECORD_DATA_MISMATCH(
        "RECOVERY_SOURCE_RECORD_DATA_MISMATCH",
        "Error: DEFAULT and STAGING record data do not match."
        +"publicPartitionOperationId=%s, recordId=%s"
    ),

    PARTITION_CREATION_FAILED(
        "PARTITION_CREATION_FAILED",
        "Error: Partition creation failed: Exception:%s ErrorMessage:%s"
    ),
    
    SIMULTANEOUS_PARTITION_CREATION_FAILED(
        "SIMULTANEOUS_PARTITION_CREATION_FAILED",
        "Error: Simultaneous partition creation failed: Exception:%s ErrorMessage:%s"
    ),
    
    ROWS_REMAIN_IN_DEFAULT_PARTITION(
        "ROWS_REMAIN_IN_DEFAULT_PARTITION",
        "Error: Rows remain in default partition. partition=%s default partition count=%s"
    ),
    
    ROWS_REMAIN_IN_STAGING_TABLE(
        "ROWS_REMAIN_IN_STAGING_TABLE",
        "Error: Rows remain in staging table. partition=%s default partition count=%s"
    ),
    
    PARTITION_OPERATION_STATUS_IS_NOT_FAILED(
        "PARTITION_OPERATION_STATUS_IS_NOT_FAILED",
        "Error: PartitionOperationStatus is not failed. partitionOperationStatus=%s"
    ),

    PARTITION_OPERATION_STATUS_IS_NOT_RUNNING(
        "PARTITION_OPERATION_STATUS_IS_NOT_RUNNING",
        "Error: PartitionOperationStatus is not running. partitionOperationStatus=%s"
    ),
    
    PARTITION_RECOVERY_FAILED(
        "PARTITION_RECOVERY_FAILED",
        "Error: Partition recovery failed. Exception: %s ErrorMessage:%s"
    ),
    
    CONCURRENCY_PARTITION_CREATION_FAILED(
        "CONCURRENCY_PARTITION_CREATION_FAILED",
        "Error: Concurrency partition creation failed: Exception:%s ErrorMessage:%s"
    ),
    
    EXACTLY_ONE_PARTITION_OPERATION_MUST_BE_CREATED(
        "EXACTLY_ONE_PARTITION_OPERATION_MUST_BE_CREATED",
        "Error: Exactly one partition operation must be created."
        +"beforePartitionOperationDTOsCount=%d"
        +", afterOperationCount=%d"
    ),

    DEFAULT_PARTITION_ROWS_REMAIN_AFTER_MOVING_TO_STAGING(
        "DEFAULT_PARTITION_ROWS_REMAIN_AFTER_MOVING_TO_STAGING",
        "Error: Default partition rows remain after moving to staging."
    ),

    STAGING_ROWS_REMAIN_AFTER_MOVING_TO_PARTITION(
        "STAGING_ROWS_REMAIN_AFTER_MOVING_TO_PARTITION",
        "Error: Staging rows remain after moving to partition."
    ),

    MULTIPLE_PARTITION_TABLE_RECORDS_ARE_FOUND(
        "MULTIPLE_PARTITION_TABLE_RECORDS_ARE_FOUND",
        "Error: Multiple partition table records are found."
    ),
    
    MULTIPLE_PARTITION_RANGE_RECORDS_ARE_FOUND(
        "MULTIPLE_PARTITION_RANGE_RECORDS_ARE_FOUND",
        "Error: Multiple partition range records are found."
    ),

    NO_PARTITION_TABLE_RECORDS_ARE_FOUND(
        "NO_PARTITION_TABLE_RECORDS_ARE_FOUND",
        "Error: No partition table records are found."
    ),

    NO_PARTITION_RANGE_RECORDS_ARE_FOUND(
        "NO_PARTITION_RANGE_RECORDS_ARE_FOUND",
        "Error: No partition range records are found."
    ),

    NO_PARTITION_RANGE_DEACTIVATION_TARGETS_ARE_FOUND(
        "NO_PARTITION_RANGE_DEACTIVATION_TARGETS_ARE_FOUND",
        "Error: No partition range deactivation targets are found."
    ),

    MULTIPLE_PARTITION_RANGE_RECORDS_ARE_UPDATED(
        "MULTIPLE_PARTITION_RANGE_RECORDS_ARE_UPDATED",
        "Error: Multiple partition range records are updated."
    ),

    INVALID_PARTITION_RANGE_RECORDS_ARE_UPDATED(
        "INVALID_PARTITION_RANGE_RECORDS_ARE_UPDATED",
        "Error: Invalid partition range records are updated."
    ),

    MAX_CONCURRENCY_TASK_COUNT_MUST_BE_GREATER_THAN_ZERO(
        "MAX_CONCURRENCY_TASK_COUNT_MUST_BE_GREATER_THAN_ZERO",
        "Error: MaxConcurrencyTaskCount must be greater than 0."
    ),

    TIMED_OUT_WAITING_FOR_START_SIGNAL(
        "TIMED_OUT_WAITING_FOR_START_SIGNAL",
        "Error: Timed out waiting for start signal."
    ),

    NOT_ALL_TASKS_BECAME_READY(
        "NOT_ALL_TASKS_BECAME_READY",
        "Error: Not all tasks became ready. timeoutException=%s"
    ),

    CONCURRENT_TASKS_TIME_OUT(
        "CONCURRENT_TASKS_TIME_OUT",
        "Error: Concurrent tasks time out."
    ),

    CONCURRENT_TASKS_INTERRUPTED(
        "CONCURRENT_TASKS_INTERRUPTED",
        "Error: Concurrent tasks interrupted."
    ),

    UNSUPPORTED_PARTITION_CONCURRENCY_TYPE(
        "UNSUPPORTED_PARTITION_CONCURRENCY_TYPE",
        "Error: Unsupported partition concurrency type."
    ),

    TIMEOUT_SECONDS_MUST_BE_GREATER_THAN_ZERO(
        "TIMEOUT_SECONDS_MUST_BE_GREATER_THAN_ZERO",
        "Error: Timeout seconds must be greater than 0."
    ),

    RECOVERED_PARTITION_ROW_COUNT_IS_INVALID(
        "RECOVERED_PARTITION_ROW_COUNT_IS_INVALID",
        "Error: Recovered partition row count is invalid. "
        +" expectedRecoveredPartitionRowCount=%s,"
        +" recoveredPartitionRowCount=%s"
    ),

    PARTITION_RANGE_IS_ALREADY_DEACTIVATED(
        "PARTITION_RANGE_IS_ALREADY_DEACTIVATED",
        "Error: Partition range is already deactivated."
    ),

    TARGET_PARTITION_ROW_COUNTS_MISMATCH(
        "TARGET_PARTITION_ROW_COUNTS_MISMATCH",
        "Error: Target partition row counts mismatch"
    ),

    INSERT_FAILED(
        "INSERT_FAILED",
        "Error: Target partition insert failed: table name is %s"
    ),

    INSERT_COUNT_UNKNOWN(
        "INSERT_COUNT_UNKNOWN",
        "Error: Target partition insert count unknown: table name is %s"
    ),

    INSERT_COUNT_MISMATCH(
        "INSERT_COUNT_MISMATCH",
        "Error: Insert count mismatch: table name is %s"
    ),

    PARTITION_RANGE_ID_MISMATCH(
        "PARTITION_RANGE_ID_MISMATCH",
        "Error: Partition range id mismatch:%s.VS.%s"
    ),

    PARTITION_OPERATION_RECOVERY_STATE_IS_INVALID(
        "PARTITION_OPERATION_RECOVERY_STATE_IS_INVALID",
        "Error: Partition operation recovery state is invalid: state = %s"
    ),

    ROWS_COUNT_MUST_NOT_BE_NEGATIVE(
        "ROWS_COUNT_MUST_NOT_BE_NEGATIVE",
        "Error: Rows count must not be negative."
    ),

    EXECUTOR_SERVICE_DID_NOT_TERMINATE(
        "EXECUTOR_SERVICE_DID_NOT_TERMINATE",
        "Error: Executor service did not terminate within %s seconds"
    ),

    PARTITION_OPERATION_MUST_BE_FAILED(
        "PARTITION_OPERATION_MUST_BE_FAILED",
        "Error: Partition operation must be FAILED. "
        + "publicPartitionOperationId=%s"
        + ", partitionOperationStatus=%s"
    );

    private final String errorCode;
    private final String errorMessage;

    PartitionRecoveryPartitionError(
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