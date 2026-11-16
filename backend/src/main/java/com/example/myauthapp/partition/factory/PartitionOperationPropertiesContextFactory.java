package com.example.myauthapp.partition.factory;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.config.PartitionOperationProperties;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;


@Component
public class PartitionOperationPropertiesContextFactory {

    public PartitionOperationPropertiesContext create(
        PartitionOperationProperties partitionOperationProperties
    ) {
        Objects.requireNonNull(
            partitionOperationProperties,
            "partitionOperationProperties must not be null"
        );

        return new PartitionOperationPropertiesContext(
            partitionOperationProperties.getPartitionOperationType(),
            partitionOperationProperties.getPartitionOperationStrategy()
        );
    }
}