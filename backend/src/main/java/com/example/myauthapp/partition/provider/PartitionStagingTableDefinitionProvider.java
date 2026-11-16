
package com.example.myauthapp.partition.provider;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.dto.PartitionRecoveryTableDefinition;

@Component
public class PartitionStagingTableDefinitionProvider {

    private final List<PartitionRecoveryTableDefinition> definitions =
        List.of(
            new PartitionRecoveryTableDefinition(
                "public_partition_operation_id",
                "UUID NOT NULL"
            ),
            new PartitionRecoveryTableDefinition(
                "record_id",
                "BIGINT NOT NULL"
            ),
            new PartitionRecoveryTableDefinition(
                "record_data",
                "JSONB NOT NULL"
            )
        );

    public List<PartitionRecoveryTableDefinition> getDefinitions() {
        return definitions;
    }

    public List<String> getColumnNames() {
        return definitions.stream()
            .map(PartitionRecoveryTableDefinition::getColumnName)
            .toList();
    }

    public List<String> getColumnDefinitions() {
        return definitions.stream()
            .map(PartitionRecoveryTableDefinition::getDefinition)
            .toList();
    }

    public String columnDefinitions() {
        return definitions.stream()
            .map(definition ->
                definition.getColumnName() + " " + definition.getDefinition()
            )
            .collect(Collectors.joining(",\n"));
    }
}
