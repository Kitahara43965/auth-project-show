package com.example.myauthapp.partition.sql;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.example.myauthapp.partition.dto.PartitionTableDTO;

public final class PartitionTableDTODiscoverySql {

    public static final String COLUMN_NAMES = """
        id,
        created_at,
        updated_at,
        table_name,
        partition_strategy,
        partition_column
        """;

    public static final RowMapper<PartitionTableDTO> ROW_MAPPER =
        (rs, rowNum) -> new PartitionTableDTO(
            rs.getLong("id"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class),
            rs.getString("table_name"),
            rs.getString("partition_strategy"),
            rs.getString("partition_column")
        );

    private PartitionTableDTODiscoverySql() {
    }
}