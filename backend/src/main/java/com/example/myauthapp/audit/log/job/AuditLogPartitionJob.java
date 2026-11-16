package com.example.myauthapp.audit.log.job;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.myauthapp.audit.log.factory.AuditLogPartitionContextGroupFactory;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.service.job.PartitionJobService;

@Component
public class AuditLogPartitionJob {

    private final AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory;
    private final PartitionJobService partitionJobService;

    public AuditLogPartitionJob(
        AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory,
        PartitionJobService partitionJobService
    ) {
        this.auditLogPartitionContextGroupFactory = auditLogPartitionContextGroupFactory;
        this.partitionJobService = partitionJobService;
    }

    @Scheduled(cron = "${audit.logs.partition.maintenance.cron}")
    public void execute() {

        PartitionContextGroup partitionContextGroup = auditLogPartitionContextGroupFactory.create();

        partitionJobService.partitionJobExecute(
            partitionContextGroup,
            LocalDateTime.now().minusMonths(
                partitionContextGroup
                    .getPartitionMaintenancePropertiesContext()
                    .getMaintenanceTargetMonthsAgo()
            )
        );
    }
}