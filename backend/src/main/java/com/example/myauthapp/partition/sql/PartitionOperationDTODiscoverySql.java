package com.example.myauthapp.partition.sql;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;

public final class PartitionOperationDTODiscoverySql {

    public static final String COLUMN_NAMES = """
        id,
        public_partition_operation_id,
        partition_table_id,
        partition_range_id,
        created_at,
        started_at,
        completed_at,
        failed_at,
        partition_operation_type,
        partition_operation_status,
        error_message,
        default_partition_row_count,
        staging_row_count
        """;

    public static final RowMapper<PartitionOperationDTO> ROW_MAPPER =
        (rs, rowNum) -> new PartitionOperationDTO(
            rs.getLong("id"),
            rs.getObject(
                "public_partition_operation_id",
                UUID.class
            ),
            rs.getLong("partition_table_id"),
            rs.getLong("partition_range_id"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("started_at", LocalDateTime.class),
            rs.getObject("completed_at", LocalDateTime.class),
            rs.getObject("failed_at", LocalDateTime.class),
            rs.getString("partition_operation_type"),
            PartitionOperationStatus.valueOf(
                rs.getString("partition_operation_status")
            ),
            rs.getString("error_message"),
            rs.getObject("default_partition_row_count", Long.class),
            rs.getObject("staging_row_count", Long.class)
        );

    private PartitionOperationDTODiscoverySql() {
    }
}