package com.example.myauthapp.partition.config;

public interface PartitionTableProperties {

    String getParentTableName();

    String getSchemaName();

    String getDefaultTableNameSuffix();

    String getPartitionIdColumnName();

    String getPartitionLocalDateTimeColumnName();
    
}