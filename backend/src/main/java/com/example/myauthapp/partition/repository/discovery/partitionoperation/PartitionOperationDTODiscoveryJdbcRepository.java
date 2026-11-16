package com.example.myauthapp.partition.repository.discovery.partitionoperation;

import java.util.List;
import java.util.UUID;
import java.util.Objects;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.sql.PartitionOperationDTODiscoverySql;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;

@Repository
public class PartitionOperationDTODiscoveryJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionOperationDTODiscoveryJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public PartitionOperationDTO
        findPartitionOperationDTOByPublicPartitionOperationId(
            UUID publicPartitionOperationId
        ) {
        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        return partitionJdbcTemplate.queryForObject(
            """
            SELECT
                %s
            FROM %s
            WHERE %s = ?
            """.formatted(
                PartitionOperationDTODiscoverySql.COLUMN_NAMES,
                partitionProperties.getPartitionOperationTableName(),
                partitionProperties.getPublicPartitionOperationIdColumnName()
            ),
            PartitionOperationDTODiscoverySql.ROW_MAPPER,
            publicPartitionOperationId
        );
    }

    public List<PartitionOperationDTO> findPartitionOperationDTOs() {

        return partitionJdbcTemplate.query(
            """
            SELECT
                %s
            FROM %s
            ORDER BY created_at, id
            """.formatted(
                PartitionOperationDTODiscoverySql.COLUMN_NAMES,
                partitionProperties.getPartitionOperationTableName()
            ),
            PartitionOperationDTODiscoverySql.ROW_MAPPER
        );
    }

    public List<PartitionOperationDTO>
        findPartitionOperationDTOsByPartitionRangeId(
            Long partitionRangeId
    ) {
        Objects.requireNonNull(
            partitionRangeId,
            "partitionRangeId must not be null"
        );

        String partitionOperationTableName =
            Objects.requireNonNull(
                partitionProperties.getPartitionOperationTableName(),
                "partitionOperationTableName must not be null"
            );

        return partitionJdbcTemplate.query(
            """
            SELECT %s
            FROM %s
            WHERE partition_range_id = ?
            ORDER BY created_at, id
            """.formatted(
                PartitionOperationDTODiscoverySql.COLUMN_NAMES,
                partitionOperationTableName
            ),
            PartitionOperationDTODiscoverySql.ROW_MAPPER,
            partitionRangeId
        );
    }

    public List<PartitionOperationDTO>
        findPartitionOperationDTOsByPartitionOperationStatus(
            PartitionOperationStatus partitionOperationStatus
        ) {
        Objects.requireNonNull(
            partitionOperationStatus,
            "partitionOperationStatus must not be null"
        );

        String sql =
            """
            SELECT
                %s
            FROM %s
            WHERE partition_operation_status = ?
            ORDER BY started_at ASC, id ASC
            """.formatted(
                PartitionOperationDTODiscoverySql.COLUMN_NAMES,
                partitionProperties.getPartitionOperationTableName()
            );

        return partitionJdbcTemplate.query(
            sql,
            PartitionOperationDTODiscoverySql.ROW_MAPPER,
            partitionOperationStatus.name()
        );
    }

    public List<PartitionOperationDTO> 
        findPartitionOperationDTOsByPartitionOperationStatusAndThresholdLocalDateTime(
            PartitionOperationStatus partitionOperationStatus,
            LocalDateTime thresholdLocalDateTime
    ) {
        Objects.requireNonNull(
            partitionOperationStatus,
            "partitionOperationStatus must not be null"
        );
        Objects.requireNonNull(
            thresholdLocalDateTime,
            "thresholdLocalDateTime must not be null"
        );

        String sql = """
            SELECT
                %s
            FROM %s
            WHERE partition_operation_status = ?
            AND started_at < ?
            ORDER BY started_at ASC, id ASC
            """.formatted(
                PartitionOperationDTODiscoverySql.COLUMN_NAMES,
                partitionProperties.getPartitionOperationTableName()
            );

        return partitionJdbcTemplate.query(
            sql,
            PartitionOperationDTODiscoverySql.ROW_MAPPER,
            partitionOperationStatus.name(),
            thresholdLocalDateTime
        );
    }

    public List<PartitionOperationDTO>
        findFailedPartitionOperationDTOsByPartitionRangeId(
            Long partitionRangeId
    ) {

        Objects.requireNonNull(
            partitionRangeId,
            "partitionRangeId must not be null"
        );

        String partitionOperationTableName =
            Objects.requireNonNull(
                partitionProperties.getPartitionOperationTableName(),
                "partitionOperationTableName must not be null"
            );

        return partitionJdbcTemplate.query(
            """
            SELECT
                %s
            FROM %s
            WHERE
                partition_range_id = ?
                AND partition_operation_status = ?
            ORDER BY created_at, id
            """.formatted(
                PartitionOperationDTODiscoverySql.COLUMN_NAMES,
                partitionOperationTableName
            ),
            PartitionOperationDTODiscoverySql.ROW_MAPPER,
            partitionRangeId,
            PartitionOperationStatus.FAILED.name()
        );
    }

    

}