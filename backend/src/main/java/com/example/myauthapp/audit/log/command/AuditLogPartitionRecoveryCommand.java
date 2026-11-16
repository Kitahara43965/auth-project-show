package com.example.myauthapp.audit.log.command;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.myauthapp.audit.log.factory.AuditLogPartitionContextGroupFactory;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.service.command.PartitionRecoveryCommandService;

@Component
public class AuditLogPartitionRecoveryCommand implements CommandLineRunner {

    private final PartitionRecoveryCommandService partitionRecoveryCommandService;
    private final AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory;

    public AuditLogPartitionRecoveryCommand(
        PartitionRecoveryCommandService partitionRecoveryCommandService,
        AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory
    ) {
        this.partitionRecoveryCommandService = partitionRecoveryCommandService;
        this.auditLogPartitionContextGroupFactory = auditLogPartitionContextGroupFactory;
    }

    @Override
    public void run(String... args) {

        PartitionContextGroup partitionContextGroup = auditLogPartitionContextGroupFactory.create();

        if (Arrays.asList(args).contains(
            partitionContextGroup
                .getPartitionCommandPropertiesContext()
                .getPartitionRecoveryCommandText()
        )) {
            partitionRecoveryCommandService.partitionRecoveryCommandExecute(
                partitionContextGroup
            );
        }
    }
}