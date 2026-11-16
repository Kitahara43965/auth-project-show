package com.example.myauthapp.partition.repository.partitionrange;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.sql.PartitionRangeDTODiscoverySql;

@Repository
public class PartitionRangeJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionRangeJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public PartitionRangeDTO insert(
        Long partitionTableId,
        LocalDateTime createdAt,
        LocalDateTime rangeStartAt,
        LocalDateTime rangeEndAt,
        String targetPartitionName
    ) {
        Objects.requireNonNull(
            partitionTableId,
            "partitionTableId must not be null"
        );
        Objects.requireNonNull(
            createdAt,
            "createdAt must not be null"
        );
        Objects.requireNonNull(
            rangeStartAt,
            "rangeStartAt must not be null"
        );
        Objects.requireNonNull(
            rangeEndAt,
            "rangeEndAt must not be null"
        );
        Objects.requireNonNull(
            targetPartitionName,
            "targetPartitionName must not be null"
        );
        String partitionRangeTableName =
            Objects.requireNonNull(
                partitionProperties.getPartitionRangeTableName(),
                "partitionRangeTableName must not be null"
            );

        return partitionJdbcTemplate.queryForObject(
            """
            INSERT INTO %s (
                partition_table_id,
                created_at,
                range_start_at,
                range_end_at,
                target_partition_name
            )
            VALUES (?, ?, ?, ?, ?)
            RETURNING
                %s
            """.formatted(
                partitionRangeTableName,
                PartitionRangeDTODiscoverySql.COLUMN_NAMES
            ),
            PartitionRangeDTODiscoverySql.ROW_MAPPER,
            partitionTableId,
            createdAt,
            rangeStartAt,
            rangeEndAt,
            targetPartitionName
        );
    }
}