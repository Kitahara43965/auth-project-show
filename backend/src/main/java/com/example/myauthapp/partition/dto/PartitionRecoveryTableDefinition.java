package com.example.myauthapp.partition.dto;

public class PartitionRecoveryTableDefinition {

    private final String columnName;
    private final String definition;

    public PartitionRecoveryTableDefinition(
        String columnName,
        String definition
    ) {
        this.columnName = columnName;
        this.definition = definition;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDefinition() {
        return definition;
    }

}