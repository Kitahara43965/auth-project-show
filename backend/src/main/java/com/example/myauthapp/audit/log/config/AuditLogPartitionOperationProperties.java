package com.example.myauthapp.audit.log.config;

import java.util.Objects;

import com.example.myauthapp.partition.config.PartitionOperationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("audit.logs.partition.operation")
public class AuditLogPartitionOperationProperties 
    implements PartitionOperationProperties {

    private final String partitionOperationType;
    private final String partitionStrategy;

    public AuditLogPartitionOperationProperties(
        String partitionOperationType,
        String partitionStrategy
    ) {
        this.partitionOperationType = 
            Objects.requireNonNull(
                partitionOperationType,
                "partitionOperationType must not be null"
            );
        this.partitionStrategy =
            Objects.requireNonNull(
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