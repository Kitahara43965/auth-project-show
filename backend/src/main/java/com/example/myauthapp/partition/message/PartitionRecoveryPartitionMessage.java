package com.example.myauthapp.partition.message;

import com.example.myauthapp.web.partition.message.PartitionMessage;

public enum PartitionRecoveryPartitionMessage implements PartitionMessage{

    SIMULTANEOUS_PARTITION_CREATION_FAILED(
        "SIMULTANEOUS_PARTITION_CREATION_FAILED",
        "Message: Simultaneous partition creation failed."
        + " targetMonth = %s. exceptionMessage = %s."
    ),


    PARTITION_RECOVERY_RETRY_FAILED(
        "PARTITION_RECOVERY_RETRY_FAILED",
        "Message: Partition recovery retry failed."
        + " targetMonth = %s. exceptionMessage = %s."
    ),
    
    EXPECTED_PARTITION_RECOVERY_FAILURE_OCCURRED(
        "EXPECTED_PARTITION_RECOVERY_FAILURE_OCCURRED",
        "Message: Expected partition recovery failure occurred. exceptionMessage = %s."
    );

    private final String code;
    private final String message;

    PartitionRecoveryPartitionMessage(
        String code,
        String message
    ) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}