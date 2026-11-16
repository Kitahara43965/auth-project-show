package com.example.myauthapp.audit.log.factory;

import org.springframework.stereotype.Component;

import com.example.myauthapp.audit.log.config.AuditLogPartitionCommandProperties;
import com.example.myauthapp.audit.log.config.AuditLogPartitionMaintenanceProperties;
import com.example.myauthapp.audit.log.config.AuditLogPartitionTableProperties;
import com.example.myauthapp.audit.log.config.AuditLogPartitionOperationProperties;
import com.example.myauthapp.audit.log.provider.AuditLogPartitionRecoveryTableExpressionProvider;
import com.example.myauthapp.partition.context.PartitionCommandPropertiesContext;
import com.example.myauthapp.partition.context.PartitionMaintenancePropertiesContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.factory.PartitionCommandPropertiesContextFactory;
import com.example.myauthapp.partition.factory.PartitionMaintenancePropertiesContextFactory;
import com.example.myauthapp.partition.factory.PartitionTablePropertiesContextFactory;
import com.example.myauthapp.partition.factory.PartitionOperationPropertiesContextFactory;
import com.example.myauthapp.partition.factory.PartitionRecoveryTableExpressionProviderContextFactory;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.factory.PartitionContextGroupFactory;

@Component
public class AuditLogPartitionContextGroupFactory 
    implements PartitionContextGroupFactory{

    private final PartitionTablePropertiesContextFactory partitionTablePropertiesContextFactory;
    private final PartitionOperationPropertiesContextFactory partitionOperationPropertiesContextFactory;
    private final PartitionCommandPropertiesContextFactory partitionCommandPropertiesContextFactory;
    private final PartitionMaintenancePropertiesContextFactory partitionMaintenancePropertiesContextFactory;
    private final PartitionRecoveryTableExpressionProviderContextFactory partitionRecoveryTableExpressionProviderContextFactory;
    private final AuditLogPartitionTableProperties auditLogPartitionTableProperties;
    private final AuditLogPartitionOperationProperties auditLogPartitionOperationProperties;
    private final AuditLogPartitionCommandProperties auditLogPartitionCommandProperties;
    private final AuditLogPartitionMaintenanceProperties auditLogPartitionMaintenanceProperties;
    private final AuditLogPartitionRecoveryTableExpressionProvider auditLogPartitionRecoveryTableExpressionProvider;

    public AuditLogPartitionContextGroupFactory(
        PartitionTablePropertiesContextFactory partitionTablePropertiesContextFactory,
        PartitionOperationPropertiesContextFactory partitionOperationPropertiesContextFactory,
        PartitionCommandPropertiesContextFactory partitionCommandPropertiesContextFactory,
        PartitionMaintenancePropertiesContextFactory partitionMaintenancePropertiesContextFactory,
        PartitionRecoveryTableExpressionProviderContextFactory partitionRecoveryTableExpressionProviderContextFactory,
        AuditLogPartitionTableProperties auditLogPartitionTableProperties,
        AuditLogPartitionOperationProperties auditLogPartitionOperationProperties,
        AuditLogPartitionCommandProperties auditLogPartitionCommandProperties,
        AuditLogPartitionMaintenanceProperties auditLogPartitionMaintenanceProperties,
        AuditLogPartitionRecoveryTableExpressionProvider auditLogPartitionRecoveryTableExpressionProvider
    ) {
        this.partitionTablePropertiesContextFactory = partitionTablePropertiesContextFactory;
        this.partitionOperationPropertiesContextFactory = partitionOperationPropertiesContextFactory;
        this.partitionCommandPropertiesContextFactory = partitionCommandPropertiesContextFactory;
        this.partitionMaintenancePropertiesContextFactory = partitionMaintenancePropertiesContextFactory;
        this.partitionRecoveryTableExpressionProviderContextFactory = partitionRecoveryTableExpressionProviderContextFactory;
        this.auditLogPartitionTableProperties = auditLogPartitionTableProperties;
        this.auditLogPartitionOperationProperties = auditLogPartitionOperationProperties;
        this.auditLogPartitionCommandProperties = auditLogPartitionCommandProperties;
        this.auditLogPartitionMaintenanceProperties = auditLogPartitionMaintenanceProperties;
        this.auditLogPartitionRecoveryTableExpressionProvider = auditLogPartitionRecoveryTableExpressionProvider;
    }

    public PartitionContextGroup create() {

        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionTablePropertiesContextFactory.create(auditLogPartitionTableProperties);

        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionOperationPropertiesContextFactory.create(auditLogPartitionOperationProperties);

        PartitionCommandPropertiesContext partitionCommandPropertiesContext =
            partitionCommandPropertiesContextFactory.create(auditLogPartitionCommandProperties);

        PartitionMaintenancePropertiesContext partitionMaintenancePropertiesContext =
            partitionMaintenancePropertiesContextFactory.create(auditLogPartitionMaintenanceProperties);
        
        PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext =
            partitionRecoveryTableExpressionProviderContextFactory.create(auditLogPartitionRecoveryTableExpressionProvider);
        
        return new PartitionContextGroup(
            partitionTablePropertiesContext,
            partitionOperationPropertiesContext,
            partitionCommandPropertiesContext,
            partitionMaintenancePropertiesContext,
            partitionRecoveryTableExpressionProviderContext
        );
    }
}