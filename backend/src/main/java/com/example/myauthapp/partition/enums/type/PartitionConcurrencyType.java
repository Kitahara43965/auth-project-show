package com.example.myauthapp.partition.enums.type;

public enum PartitionConcurrencyType {
    RECOVERY,
    RECOVERY_RETRY,
    RECOVERY_AND_RECOVERY_RETRY,
    CLEANUP,
    RECOVERY_AND_CLEANUP,
    RECOVERY_RETRY_AND_CLEANUP,
    RECOVERY_AND_RECOVERY_RETRY_AND_CLEANUP
}