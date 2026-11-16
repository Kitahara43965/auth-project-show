package com.example.myauthapp.web.partition.mapper;

import org.springframework.http.HttpStatus;

import com.example.myauthapp.web.partition.error.PartitionError;
import com.example.myauthapp.partition.error.PartitionDatabasePartitionError;
import com.example.myauthapp.partition.error.PartitionDependentDatabasePartitionError;
import com.example.myauthapp.partition.error.PartitionNamePartitionError;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.error.PartitionTimePartitionError;

public final class PartitionErrorMapper {

    private PartitionErrorMapper() {
    }

    public static HttpStatus toHttpStatus(PartitionError partitionError) {

        if (partitionError instanceof PartitionDatabasePartitionError error) {

            switch (error) {

                case OPERATION_WAS_NOT_UPDATED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_OPERATION_IS_NOT_AVAILABLE_FOR_RETRY:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case ROW_COUNT_MUST_NOT_BE_NEGATIVE:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown PartitionDatabasePartitionError: " + error
                    );
            }
        }else if (partitionError instanceof PartitionDependentDatabasePartitionError error) {
            switch (error) {

                case PARTITION_DROP_FAILED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown PartitionDependentDatabasePartitionError: " + error
                    );
            }
        }else if (partitionError instanceof PartitionNamePartitionError error) {
            switch (error) {

                case PARTITION_NAME_IS_INVALID:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown PartitionNamePartitionError: " + error
                    );
            }
        }else if (partitionError instanceof PartitionRecoveryPartitionError error) {
            switch (error) {

                case FAILED_TO_PARSE_RECOVERY_RECORD_DATA:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_RANGE_IS_NOT_FOUND:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case RECOVERY_SOURCE_COUNT_MISMATCH:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case RECOVERY_SOURCE_RECORD_ID_MISMATCH:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case RECOVERY_SOURCE_RECORD_DATA_MISMATCH:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_CREATION_FAILED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case SIMULTANEOUS_PARTITION_CREATION_FAILED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case ROWS_REMAIN_IN_DEFAULT_PARTITION:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case ROWS_REMAIN_IN_STAGING_TABLE:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_OPERATION_STATUS_IS_NOT_FAILED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_OPERATION_STATUS_IS_NOT_RUNNING:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_RECOVERY_FAILED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case CONCURRENCY_PARTITION_CREATION_FAILED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case EXACTLY_ONE_PARTITION_OPERATION_MUST_BE_CREATED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case DEFAULT_PARTITION_ROWS_REMAIN_AFTER_MOVING_TO_STAGING:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case STAGING_ROWS_REMAIN_AFTER_MOVING_TO_PARTITION:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case MULTIPLE_PARTITION_TABLE_RECORDS_ARE_FOUND:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case MULTIPLE_PARTITION_RANGE_RECORDS_ARE_FOUND:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case NO_PARTITION_TABLE_RECORDS_ARE_FOUND:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case NO_PARTITION_RANGE_RECORDS_ARE_FOUND:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case NO_PARTITION_RANGE_DEACTIVATION_TARGETS_ARE_FOUND:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case MULTIPLE_PARTITION_RANGE_RECORDS_ARE_UPDATED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case INVALID_PARTITION_RANGE_RECORDS_ARE_UPDATED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case MAX_CONCURRENCY_TASK_COUNT_MUST_BE_GREATER_THAN_ZERO:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case TIMED_OUT_WAITING_FOR_START_SIGNAL:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case NOT_ALL_TASKS_BECAME_READY:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case CONCURRENT_TASKS_TIME_OUT:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case CONCURRENT_TASKS_INTERRUPTED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case UNSUPPORTED_PARTITION_CONCURRENCY_TYPE:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case TIMEOUT_SECONDS_MUST_BE_GREATER_THAN_ZERO:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case RECOVERED_PARTITION_ROW_COUNT_IS_INVALID:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_OPERATION_MUST_BE_FAILED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case TARGET_PARTITION_ROW_COUNTS_MISMATCH:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case INSERT_FAILED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case INSERT_COUNT_UNKNOWN:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case INSERT_COUNT_MISMATCH:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_RANGE_ID_MISMATCH:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_OPERATION_RECOVERY_STATE_IS_INVALID:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case ROWS_COUNT_MUST_NOT_BE_NEGATIVE:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case EXECUTOR_SERVICE_DID_NOT_TERMINATE:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case PARTITION_RANGE_IS_ALREADY_DEACTIVATED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown PartitionRecoveryPartitionError: " + error
                    );
            }
        }else if (partitionError instanceof PartitionTimePartitionError error) {
            switch (error) {

                case INVALID_PARTITION_TIME_RANGE:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown PartitionTimePartitionError: " + error
                    );
            }
        }//partitionError

        throw new IllegalArgumentException(
            "Unknown PartitionError: " + partitionError
        );
    }
    
}