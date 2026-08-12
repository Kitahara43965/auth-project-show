package com.example.myauthapp.audit.auditlog.partition.command;

import com.example.myauthapp.audit.auditlog.partition.service.AuditLogPartitionRecoveryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AuditLogRecoveryCommand implements CommandLineRunner {

    private final AuditLogPartitionRecoveryService recoveryService;

    public AuditLogRecoveryCommand(
            AuditLogPartitionRecoveryService recoveryService
    ) {
        this.recoveryService = recoveryService;
    }

    @Override
    public void run(String... args) {

        if (Arrays.asList(args).contains("--audit-log-recovery")) {
            recoveryService.recoverAll();
        }
    }
}