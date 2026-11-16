package com.example.myauthapp.audit.log.config;

import java.util.Objects;

import com.example.myauthapp.partition.config.PartitionMaintenanceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("audit.logs.partition.maintenance")
public class AuditLogPartitionMaintenanceProperties implements PartitionMaintenanceProperties {

    private final int maintenanceTargetMonthsAgo;
    private final String cron;

    public AuditLogPartitionMaintenanceProperties(
        int maintenanceTargetMonthsAgo,
        String cron
    ) {
        this.maintenanceTargetMonthsAgo = 
            Objects.requireNonNull(
                maintenanceTargetMonthsAgo,
                "maintenanceTargetMonthsAgo must not be null"
            );
        this.cron =
            Objects.requireNonNull(
                cron,
                "cron must not be null"
            );
    }

    public int getMaintenanceTargetMonthsAgo(){
        return maintenanceTargetMonthsAgo;
    }

    public String getCron(){
        return cron;
    }
}