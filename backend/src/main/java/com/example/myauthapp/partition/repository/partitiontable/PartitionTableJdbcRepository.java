package com.example.myauthapp.partition.repository.partitiontable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.dto.PartitionTableDTO;
import com.example.myauthapp.partition.sql.PartitionTableDTODiscoverySql;
import com.example.myauthapp.partition.config.PartitionProperties;

@Repository
public class PartitionTableJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionTableJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }


    public PartitionTableDTO insert(
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String tableName,
        String partitionStrategy,
        String partitionColumn
    ) {
        Objects.requireNonNull(
            createdAt,
            "createdAt must not be null"
        );
        Objects.requireNonNull(
            updatedAt,
            "updatedAt must not be null"
        );
        Objects.requireNonNull(
            tableName,
            "tableName must not be null"
        );
        Objects.requireNonNull(
            partitionStrategy,
            "partitionStrategy must not be null"
        );
        Objects.requireNonNull(
            partitionColumn,
            "partitionColumn must not be null"
        );
        String partitionTableTableName =
            Objects.requireNonNull(
                partitionProperties.getPartitionTableTableName(),
                "partitionTableTableName must not be null"
            );

        return partitionJdbcTemplate.queryForObject(
            """
            INSERT INTO %s (
                created_at,
                updated_at,
                table_name,
                partition_strategy,
                partition_column
            )
            VALUES (?, ?, ?, ?, ?)
            RETURNING
                %s
            """.formatted(
                partitionTableTableName,
                PartitionTableDTODiscoverySql.COLUMN_NAMES
            ),
            PartitionTableDTODiscoverySql.ROW_MAPPER,
            createdAt,
            updatedAt,
            tableName,
            partitionStrategy,
            partitionColumn
        );
    }
}