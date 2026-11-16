package com.example.myauthapp.partition.factory;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.config.PartitionMaintenanceProperties;
import com.example.myauthapp.partition.context.PartitionMaintenancePropertiesContext;

@Component
public class PartitionMaintenancePropertiesContextFactory {

    public PartitionMaintenancePropertiesContext create(
        PartitionMaintenanceProperties partitionMaintenanceProperties
    ) {
        Objects.requireNonNull(
            partitionMaintenanceProperties,
            "partitionMaintenanceProperties must not be null"
        );

        return new PartitionMaintenancePropertiesContext(
            partitionMaintenanceProperties.getMaintenanceTargetMonthsAgo(),
            partitionMaintenanceProperties.getCron()
        );
    }
}