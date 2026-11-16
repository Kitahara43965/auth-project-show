package com.example.myauthapp.partition.context;

import java.util.Objects;

public class PartitionCommandPropertiesContext {

    private final String partitionRecoveryCommandText;

    public PartitionCommandPropertiesContext(
        String partitionRecoveryCommandText
    ) {
        this.partitionRecoveryCommandText = Objects.requireNonNull(
            partitionRecoveryCommandText,
            "partitionRecoveryCommandText must not be null"
        );
    }

    public String getPartitionRecoveryCommandText() {
        return partitionRecoveryCommandText;
    }

}