package com.example.myauthapp.audit.auditlog.partition.job;
import com.example.myauthapp.audit.auditlog.partition.service.AuditLogPartitionService;
import com.example.myauthapp.audit.auditlog.partition.service.AuditLogDefaultCleanupService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
public class AuditLogPartitionJob {

    private final AuditLogPartitionService auditPartitionService;
    private final AuditLogDefaultCleanupService auditDefaultCleanupService;

    public AuditLogPartitionJob(
            AuditLogPartitionService auditPartitionService,
            AuditLogDefaultCleanupService auditDefaultCleanupService
    ) {
        this.auditPartitionService = auditPartitionService;
        this.auditDefaultCleanupService = auditDefaultCleanupService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void createFuturePartitions() {

        YearMonth currentMonth = YearMonth.now();

        for (int addedMonths = 0; addedMonths < 4; addedMonths++) {

            YearMonth targetMonth =
                    currentMonth.plusMonths(addedMonths);

            // ① Partitionを確保
            auditPartitionService.ensurePartition(targetMonth);

            // ② DEFAULTに残っている対象月のデータを移動
            auditDefaultCleanupService.cleanup(targetMonth);
        }
    }
}