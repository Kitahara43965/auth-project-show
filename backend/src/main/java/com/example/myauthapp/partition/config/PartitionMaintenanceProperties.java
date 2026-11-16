package com.example.myauthapp.partition.config;

public interface PartitionMaintenanceProperties {

    int getMaintenanceTargetMonthsAgo();
    
    String getCron();
}