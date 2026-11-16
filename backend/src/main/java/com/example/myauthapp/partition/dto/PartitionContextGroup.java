package com.example.myauthapp.partition.dto;

import java.util.Objects;
import java.time.LocalDateTime;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionCommandPropertiesContext;
import com.example.myauthapp.partition.context.PartitionMaintenancePropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;

public class PartitionContextGroup{

    private final PartitionTablePropertiesContext partitionTablePropertiesContext;
    private final PartitionOperationPropertiesContext partitionOperationPropertiesContext;
    private final PartitionCommandPropertiesContext partitionCommandPropertiesContext;
    private final PartitionMaintenancePropertiesContext partitionMaintenancePropertiesContext;
    private final PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext;

    public PartitionContextGroup(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionOperationPropertiesContext partitionOperationPropertiesContext,
        PartitionCommandPropertiesContext partitionCommandPropertiesContext,
        PartitionMaintenancePropertiesContext partitionMaintenancePropertiesContext,
        PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext
    ){
        this.partitionTablePropertiesContext = Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        this.partitionOperationPropertiesContext = Objects.requireNonNull(
            partitionOperationPropertiesContext,
            "partitionOperationPropertiesContext must not be null"
        );
        this.partitionCommandPropertiesContext = Objects.requireNonNull(
            partitionCommandPropertiesContext,
            "partitionCommandPropertiesContext must not be null"
        );
        this.partitionMaintenancePropertiesContext = Objects.requireNonNull(
            partitionMaintenancePropertiesContext,
            "partitionMaintenancePropertiesContext must not be null"
        );
        this.partitionRecoveryTableExpressionProviderContext = Objects.requireNonNull(
            partitionRecoveryTableExpressionProviderContext,
            "partitionRecoveryTableExpressionProviderContext must not be null"
        );
    }

    public PartitionTablePropertiesContext getPartitionTablePropertiesContext(){
        return this.partitionTablePropertiesContext;
    }

    public PartitionOperationPropertiesContext getPartitionOperationPropertiesContext(){
        return this.partitionOperationPropertiesContext;
    }

    public PartitionCommandPropertiesContext getPartitionCommandPropertiesContext(){
        return this.partitionCommandPropertiesContext;
    }

    public PartitionMaintenancePropertiesContext getPartitionMaintenancePropertiesContext(){
        return this.partitionMaintenancePropertiesContext;
    }

    public PartitionRecoveryTableExpressionProviderContext getPartitionRecoveryTableExpressionProviderContext(){
        return this.partitionRecoveryTableExpressionProviderContext;
    }

}