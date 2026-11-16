package com.example.myauthapp.partition.repository.delete;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.config.PartitionProperties;

@Repository
public class PartitionStagingDeleteJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionStagingDeleteJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public void deleteStagingRows(
        UUID publicPartitionOperationId
    ) {
        
        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        partitionJdbcTemplate.update(
            """
            DELETE FROM %s
            WHERE %s = ?
            """.formatted(
                partitionProperties.getPartitionStagingTableName(),
                partitionProperties.getPublicPartitionOperationIdColumnName()
            ),
            publicPartitionOperationId
        );
    }
    
}
