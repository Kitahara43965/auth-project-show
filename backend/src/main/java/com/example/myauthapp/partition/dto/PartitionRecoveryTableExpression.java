package com.example.myauthapp.partition.dto;

public class PartitionRecoveryTableExpression {

    private final String columnName;
    private final String expression;
    private final String restoreExpression;

    public PartitionRecoveryTableExpression(
        String columnName,
        String expression,
        String restoreExpression
    ) {
        this.columnName = columnName;
        this.expression = expression;
        this.restoreExpression = restoreExpression;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getExpression() {
        return expression;
    }

    public String getRestoreExpression() {
        return restoreExpression;
    }

}