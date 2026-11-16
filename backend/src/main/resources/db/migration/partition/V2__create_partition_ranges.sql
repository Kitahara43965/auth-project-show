CREATE TABLE partition_ranges (
    /*変更した場合は、PartitionRangeDTODiscoverySqlを変更すること*/
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    partition_table_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    deactivated_at TIMESTAMP,
    range_start_at TIMESTAMP NOT NULL,
    range_end_at TIMESTAMP NOT NULL,
    target_partition_name VARCHAR(255) NOT NULL,

    CONSTRAINT fk_partition_ranges_table
        FOREIGN KEY (partition_table_id)
        REFERENCES partition_tables(id),

    CONSTRAINT uq_partition_ranges_name
        UNIQUE (partition_table_id, target_partition_name),

    CONSTRAINT uq_partition_ranges_id_table
        UNIQUE (id, partition_table_id),

    CONSTRAINT chk_partition_ranges_period
        CHECK (range_start_at < range_end_at)
);