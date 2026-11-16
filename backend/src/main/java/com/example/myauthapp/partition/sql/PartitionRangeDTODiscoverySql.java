package com.example.myauthapp.partition.sql;

import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import com.example.myauthapp.partition.dto.PartitionRangeDTO;

public final class PartitionRangeDTODiscoverySql {

    public static final String COLUMN_NAMES = """
        id,
        partition_table_id,
        created_at,
        deactivated_at,
        range_start_at,
        range_end_at,
        target_partition_name
        """;

    public static final RowMapper<PartitionRangeDTO> ROW_MAPPER =
        (rs, rowNum) -> new PartitionRangeDTO(
            rs.getLong("id"),
            rs.getLong("partition_table_id"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("deactivated_at", LocalDateTime.class),
            rs.getObject("range_start_at", LocalDateTime.class),
            rs.getObject("range_end_at", LocalDateTime.class),
            rs.getString("target_partition_name")
        );

    private PartitionRangeDTODiscoverySql() {

    }
}