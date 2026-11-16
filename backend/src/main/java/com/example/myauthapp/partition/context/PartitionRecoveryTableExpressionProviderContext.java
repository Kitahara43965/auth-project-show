package com.example.myauthapp.partition.context;

import java.util.List;
import java.util.Objects;

import com.example.myauthapp.partition.dto.PartitionRecoveryTableExpression;

public class PartitionRecoveryTableExpressionProviderContext {

    private final List<PartitionRecoveryTableExpression> partitionRecoveryTableExpressions;

    public PartitionRecoveryTableExpressionProviderContext(
        List<PartitionRecoveryTableExpression> partitionRecoveryTableExpressions
    ) {
        this.partitionRecoveryTableExpressions = List.copyOf(
            Objects.requireNonNull(
                partitionRecoveryTableExpressions,
                "partitionRecoveryTableExpressions must not be null"
            )
        );
    }

    public List<PartitionRecoveryTableExpression> getPartitionRecoveryTableExpressions() {
        return partitionRecoveryTableExpressions;
    }

    public List<String> getPartitionRecoveryTableExpressionColumnNames() {
        return partitionRecoveryTableExpressions
            .stream()
            .map(PartitionRecoveryTableExpression::getColumnName)
            .toList();
    }

    public List<String> getPartitionRecoveryTableExpressionExpressions() {
        return partitionRecoveryTableExpressions
            .stream()
            .map(PartitionRecoveryTableExpression::getExpression)
            .toList();
    }

    public List<String> getPartitionRecoveryTableExpressionRestoreExpressions() {
        return partitionRecoveryTableExpressions
            .stream()
            .map(PartitionRecoveryTableExpression::getRestoreExpression)
            .toList();
    }

    public String recordDataExpression() {
        StringBuilder result = new StringBuilder();

        for (PartitionRecoveryTableExpression
                partitionRecoveryTableExpression :
                    partitionRecoveryTableExpressions) {

            if (result.length() >= 1) {
                result.append(",\n");
            }

            result.append("'")
                .append(partitionRecoveryTableExpression.getColumnName())
                .append("', ")
                .append(partitionRecoveryTableExpression.getExpression());
        }

        return """
            jsonb_build_object(
                %s
            )
            """.formatted(result);
    }

    public String restoreSelectExpression() {
        StringBuilder result = new StringBuilder();

        for (PartitionRecoveryTableExpression
                partitionRecoveryTableExpression :
                    partitionRecoveryTableExpressions) {

            if (result.length() >= 1) {
                result.append(",\n");
            }

            result.append(
                partitionRecoveryTableExpression.getRestoreExpression()
            );
        }

        return result.toString();
    }

    public String columnNames() {
        StringBuilder result = new StringBuilder();

        for (PartitionRecoveryTableExpression
                partitionRecoveryTableExpression :
                    partitionRecoveryTableExpressions) {

            if (result.length() >= 1) {
                result.append(",\n");
            }

            result.append(
                partitionRecoveryTableExpression.getColumnName()
            );
        }

        return result.toString();
    }

    
}