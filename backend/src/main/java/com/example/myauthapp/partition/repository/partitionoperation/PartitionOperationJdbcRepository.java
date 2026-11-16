package com.example.myauthapp.partition.repository.partitionoperation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.sql.PartitionOperationDTODiscoverySql;

import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionDatabasePartitionError;

@Repository
public class PartitionOperationJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionOperationJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    @Transactional(
        value = "partitionTransactionManager",
        propagation = Propagation.REQUIRES_NEW
    )
    public PartitionOperationDTO create(
        PartitionOperationPropertiesContext partitionOperationPropertiesContext,
        PartitionRangeDTO partitionRangeDTO,
        PartitionOperationStatus partitionOperationStatus
    ) {
        Objects.requireNonNull(
            partitionOperationPropertiesContext,
            "partitionOperationPropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        Objects.requireNonNull(
            partitionOperationStatus,
            "partitionOperationStatus must not be null"
        );


        LocalDateTime now = LocalDateTime.now();
        UUID publicPartitionOperationId = UUID.randomUUID();

        return partitionJdbcTemplate.queryForObject(
            """
            INSERT INTO %s (
                %s,
                partition_table_id,
                partition_range_id,
                created_at,
                started_at,
                partition_operation_type,
                partition_operation_status
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING
                %s
            """.formatted(
                partitionProperties.getPartitionOperationTableName(),
                partitionProperties.getPublicPartitionOperationIdColumnName(),
                PartitionOperationDTODiscoverySql.COLUMN_NAMES
            ),
            PartitionOperationDTODiscoverySql.ROW_MAPPER,
            publicPartitionOperationId,
            partitionRangeDTO.getPartitionTableId(),
            partitionRangeDTO.getId(),
            now,
            now,
            partitionOperationPropertiesContext
                .getPartitionOperationType(),
            partitionOperationStatus.name()
        );
    }

    @Transactional(
        value = "partitionTransactionManager",
        propagation = Propagation.REQUIRES_NEW
    )
    public void complete(
        UUID publicPartitionOperationId,
        LocalDateTime completedAt,
        long defaultPartitionRowCount,
        long stagingRowCount
    ) {
        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        Objects.requireNonNull(
            completedAt,
            "completedAt must not be null"
        );

        if (defaultPartitionRowCount < 0L || stagingRowCount < 0L) {
            throw new PartitionException(
                PartitionDatabasePartitionError.ROW_COUNT_MUST_NOT_BE_NEGATIVE
            );
        }

        int updatedRowCount =
            partitionJdbcTemplate.update(
                """
                UPDATE %s
                SET
                    completed_at = ?,
                    failed_at = NULL,
                    partition_operation_status = ?,
                    default_partition_row_count = ?,
                    staging_row_count = ?,
                    error_message = NULL
                WHERE %s = ?
                AND partition_operation_status = 'RUNNING'
                """.formatted(
                    partitionProperties.getPartitionOperationTableName(),
                    partitionProperties.getPublicPartitionOperationIdColumnName()
                ),
                completedAt,
                PartitionOperationStatus.COMPLETED.name(),
                defaultPartitionRowCount,
                stagingRowCount,
                publicPartitionOperationId
            );

        if (updatedRowCount != 1) {
            throw new PartitionException(
                PartitionDatabasePartitionError.OPERATION_WAS_NOT_UPDATED,
                publicPartitionOperationId
            );
        }
    }

    @Transactional(
        value = "partitionTransactionManager",
        propagation = Propagation.REQUIRES_NEW
    )
    public void fail(
        UUID publicPartitionOperationId,
        LocalDateTime failedAt,
        String errorMessage
    ) {
        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        Objects.requireNonNull(
            failedAt,
            "failedAt must not be null"
        );
        Objects.requireNonNull(
            errorMessage,
            "errorMessage must not be null"
        );

        int updatedRowCount =
            partitionJdbcTemplate.update(
                """
                UPDATE %s
                SET
                    completed_at = NULL,
                    failed_at = ?,
                    partition_operation_status = ?,
                    error_message = ?
                WHERE %s = ?
                """.formatted(
                    partitionProperties.getPartitionOperationTableName(),
                    partitionProperties.getPublicPartitionOperationIdColumnName()
                ),
                failedAt,
                PartitionOperationStatus.FAILED.name(),
                errorMessage,
                publicPartitionOperationId
            );

        if (updatedRowCount != 1) {
            throw new PartitionException(
                PartitionDatabasePartitionError.OPERATION_WAS_NOT_UPDATED,
                publicPartitionOperationId
            );
        }
    }
}