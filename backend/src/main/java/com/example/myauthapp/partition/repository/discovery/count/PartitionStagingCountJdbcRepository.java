package com.example.myauthapp.partition.repository.discovery.count;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.config.PartitionProperties;

@Repository
public class PartitionStagingCountJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionStagingCountJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public long countStagingRowCountByPublicPartitionOperationId(
        UUID publicPartitionOperationId
    ) {

        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        return partitionJdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM %s
            WHERE %s = ?
            """.formatted(
                partitionProperties.getPartitionStagingTableName(),
                partitionProperties.getPublicPartitionOperationIdColumnName()
            ),
            Long.class,
            publicPartitionOperationId
        );
    }

    public long countStagingRowCount() {

        return partitionJdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM %s
            """.formatted(
                partitionProperties.getPartitionStagingTableName()
            ),
            Long.class
        );
    }

}