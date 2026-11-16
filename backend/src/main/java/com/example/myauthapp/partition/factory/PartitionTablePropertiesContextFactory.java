package com.example.myauthapp.partition.factory;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.config.PartitionTableProperties;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;


@Component
public class PartitionTablePropertiesContextFactory {

    public PartitionTablePropertiesContext create(
        PartitionTableProperties partitionTableProperties
    ) {
        Objects.requireNonNull(
            partitionTableProperties,
            "partitionTableProperties must not be null"
        );

        return new PartitionTablePropertiesContext(
            partitionTableProperties.getParentTableName(),
            partitionTableProperties.getSchemaName(),
            partitionTableProperties.getDefaultTableNameSuffix(),
            partitionTableProperties.getPartitionIdColumnName(),
            partitionTableProperties.getPartitionLocalDateTimeColumnName()
        );
    }
}