package com.example.myauthapp.partition.repository.deactivate;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.config.PartitionProperties;

@Repository
public class PartitionRangeDeactivateJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public PartitionRangeDeactivateJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public int deactivatePartitionRange(
        PartitionRangeDTO partitionRangeDTO,
        LocalDateTime deactivatedAt
    ) {

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );
        Objects.requireNonNull(
            deactivatedAt,
            "deactivatedAt must not be null"
        );

        String sql = """
            UPDATE %s
            SET deactivated_at = ?
            WHERE partition_table_id = ?
              AND target_partition_name = ?
              AND deactivated_at IS NULL
            """.formatted(
                partitionProperties.getPartitionRangeTableName()
            );

        return partitionJdbcTemplate.update(
            sql,
            deactivatedAt,
            partitionRangeDTO.getPartitionTableId(),
            partitionRangeDTO.getTargetPartitionName()
        );
    }
}