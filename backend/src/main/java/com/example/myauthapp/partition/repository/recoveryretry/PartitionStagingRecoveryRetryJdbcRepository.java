package com.example.myauthapp.partition.repository.recoveryretry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.Objects;

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
import com.example.myauthapp.partition.repository.discovery.partitionoperation
    .PartitionOperationDTODiscoveryJdbcRepository;

import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionDatabasePartitionError;

@Repository
public class PartitionStagingRecoveryRetryJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;
    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;

    public PartitionStagingRecoveryRetryJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties,
        PartitionOperationDTODiscoveryJdbcRepository
            partitionOperationDTODiscoveryJdbcRepository
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties =
            partitionProperties;
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;
    }

    @Transactional(
        value = "partitionTransactionManager",
        propagation = Propagation.REQUIRED
    )
    public void startRetry(
        UUID publicPartitionOperationId,
        LocalDateTime startedAt
    ) {
        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );
        Objects.requireNonNull(
            startedAt,
            "startedAt must not be null"
        );

        int updated = partitionJdbcTemplate.update(
            """
            UPDATE %s
            SET
                started_at = ?,
                completed_at = NULL,
                failed_at = NULL,
                partition_operation_status = ?
            WHERE
                %s = ?
                AND partition_operation_status = ?
            """.formatted(
                partitionProperties.getPartitionOperationTableName(),
                partitionProperties.getPublicPartitionOperationIdColumnName()
            ),
            startedAt,
            PartitionOperationStatus.RUNNING.name(),
            publicPartitionOperationId,
            PartitionOperationStatus.FAILED.name()
        );

        if (updated != 1) {
            throw new PartitionException(
                PartitionDatabasePartitionError
                    .PARTITION_OPERATION_IS_NOT_AVAILABLE_FOR_RETRY,
                publicPartitionOperationId
            );
        }
    }
}