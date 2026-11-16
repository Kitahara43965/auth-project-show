package com.example.myauthapp.partition.context;

import java.util.Objects;

public class PartitionMaintenancePropertiesContext {

    private final int maintenanceTargetMonthsAgo;
    private final String cron;

    public PartitionMaintenancePropertiesContext(
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

    public int getMaintenanceTargetMonthsAgo() {
        return maintenanceTargetMonthsAgo;
    }

    public String getCron() {
        return cron;
    }

}