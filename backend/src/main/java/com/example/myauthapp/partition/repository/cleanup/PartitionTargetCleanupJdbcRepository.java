package com.example.myauthapp.partition.repository.cleanup;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionDependentDatabasePartitionError;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;

@Repository
public class PartitionTargetCleanupJdbcRepository {


    private final JdbcTemplate auditJdbcTemplate;

    public PartitionTargetCleanupJdbcRepository(
        @Qualifier("auditJdbcTemplate")
        JdbcTemplate auditJdbcTemplate
    ) {
        this.auditJdbcTemplate = auditJdbcTemplate;
    }

    public void dropPartition(
        PartitionRangeDTO partitionRangeDTO
    ) {

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        String targetPartitionName = partitionRangeDTO.getTargetPartitionName();

        auditJdbcTemplate.execute(
            """
            DROP TABLE IF EXISTS %s
            """.formatted(targetPartitionName)
        );

        Long count = auditJdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM pg_class
            WHERE relname = ?
            """,
            Long.class,
            targetPartitionName
        );

        if (count != 0) {
            throw new PartitionException(
                PartitionDependentDatabasePartitionError.PARTITION_DROP_FAILED,
                targetPartitionName
            );
        }

    }
}