package com.example.myauthapp.partition.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("partition")
public class PartitionProperties{

    private final String partitionTableTableName;
    private final String partitionRangeTableName;
    private final String partitionOperationTableName;
    private final String partitionStagingTableName;
    private final String publicPartitionOperationIdColumnName;

    public PartitionProperties(
        String partitionTableTableName,
        String partitionRangeTableName,
        String partitionOperationTableName,
        String partitionStagingTableName,
        String publicPartitionOperationIdColumnName
    ) {
        this.partitionTableTableName = 
            Objects.requireNonNull(
                partitionTableTableName,
                "partitionTableTableName must not be null"
            );
        this.partitionRangeTableName = 
            Objects.requireNonNull(
                partitionRangeTableName,
                "partitionRangeTableName must not be null"
            );
        this.partitionOperationTableName =
            Objects.requireNonNull(
                partitionOperationTableName,
                "partitionOperationTableName must not be null"
            );
        this.partitionStagingTableName =
            Objects.requireNonNull(
                partitionStagingTableName,
                "partitionStagingTableName must not be null"
            );
        this.publicPartitionOperationIdColumnName =
            Objects.requireNonNull(
                publicPartitionOperationIdColumnName,
                "publicPartitionOperationIdColumnName must not be null"
            );
    }

    public String getPartitionTableTableName() {
        return partitionTableTableName;
    }

    public String getPartitionRangeTableName() {
        return partitionRangeTableName;
    }

    public String getPartitionOperationTableName() {
        return partitionOperationTableName;
    }

    public String getPartitionStagingTableName() {
        return partitionStagingTableName;
    }

    public String getPublicPartitionOperationIdColumnName(){
        return publicPartitionOperationIdColumnName;
    }
}