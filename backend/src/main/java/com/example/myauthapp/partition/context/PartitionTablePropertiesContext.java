package com.example.myauthapp.partition.context;

import java.util.Objects;

public class PartitionTablePropertiesContext {

    private final String parentTableName;
    private final String schemaName;
    private final String defaultTableNameSuffix;
    private final String partitionIdColumnName;
    private final String partitionLocalDateTimeColumnName;

    public PartitionTablePropertiesContext(
        String parentTableName,
        String schemaName,
        String defaultTableNameSuffix,
        String partitionIdColumnName,
        String partitionLocalDateTimeColumnName
    ) {
        this.parentTableName = Objects.requireNonNull(
            parentTableName,
            "parentTableName must not be null"
        );
        this.schemaName = Objects.requireNonNull(
            schemaName,
            "schemaName must not be null"
        );
        this.defaultTableNameSuffix = Objects.requireNonNull(
            defaultTableNameSuffix,
            "defaultTableNameSuffix must not be null"
        );
        this.partitionIdColumnName = Objects.requireNonNull(
            partitionIdColumnName,
            "partitionIdColumnName must not be null"
        );
        this.partitionLocalDateTimeColumnName = Objects.requireNonNull(
            partitionLocalDateTimeColumnName,
            "partitionLocalDateTimeColumnName must not be null"
        );
    }

    public String getParentTableName() {
        return parentTableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getDefaultTableNameSuffix() {
        return defaultTableNameSuffix;
    }

    public String getPartitionIdColumnName() {
        return partitionIdColumnName;
    }

    public String getPartitionLocalDateTimeColumnName() {
        return partitionLocalDateTimeColumnName;
    }
}