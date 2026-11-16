package com.example.myauthapp.partition.factory;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.config.PartitionCommandProperties;
import com.example.myauthapp.partition.context.PartitionCommandPropertiesContext;

@Component
public class PartitionCommandPropertiesContextFactory {

    public PartitionCommandPropertiesContext create(
        PartitionCommandProperties partitionCommandProperties
    ) {
        Objects.requireNonNull(
            partitionCommandProperties,
            "partitionCommandProperties must not be null"
        );

        return new PartitionCommandPropertiesContext(
            partitionCommandProperties.getPartitionRecoveryCommandText()
        );
    }
}