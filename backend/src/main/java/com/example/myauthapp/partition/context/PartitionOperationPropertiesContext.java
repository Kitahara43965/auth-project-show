package com.example.myauthapp.partition.context;

import java.util.Objects;

public class PartitionOperationPropertiesContext {

    private final String partitionOperationType;
    private final String partitionStrategy;

    public PartitionOperationPropertiesContext(
        String partitionOperationType,
        String partitionStrategy
    ) {
        this.partitionOperationType = Objects.requireNonNull(
            partitionOperationType,
            "partitionOperationType must not be null"
        );
        this.partitionStrategy = Objects.requireNonNull(
            partitionStrategy,
            "partitionStrategy must not be null"
        );
    }

    public String getPartitionOperationType(){
        return partitionOperationType;
    }

    public String getPartitionOperationStrategy(){
        return partitionStrategy;
    }
}