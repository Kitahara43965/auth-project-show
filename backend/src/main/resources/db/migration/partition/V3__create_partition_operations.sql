CREATE TABLE partition_operations (
    /*変更した場合は、PartitionOperationDTODiscoverySqlを変更すること*/
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    public_partition_operation_id UUID NOT NULL UNIQUE,

    partition_table_id BIGINT NOT NULL,
    partition_range_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    failed_at TIMESTAMP,

    partition_operation_type VARCHAR(50) NOT NULL,
    partition_operation_status VARCHAR(50) NOT NULL,
    error_message TEXT,

    default_partition_row_count BIGINT,
    staging_row_count BIGINT,

    CONSTRAINT fk_partition_operations_table
        FOREIGN KEY (partition_table_id)
        REFERENCES partition_tables(id),

    CONSTRAINT fk_partition_operations_range
        FOREIGN KEY (partition_range_id, partition_table_id)
        REFERENCES partition_ranges(id, partition_table_id),

    CONSTRAINT chk_partition_operations_status
        CHECK (
            partition_operation_status IN (
                'RUNNING',
                'COMPLETED',
                'FAILED',
                'INCONSISTENT'
            )
        ),

    CONSTRAINT chk_partition_operations_default_partition_row_count
        CHECK (
            default_partition_row_count IS NULL
            OR default_partition_row_count >= 0
        ),

    CONSTRAINT chk_partition_operations_staging_row_count
        CHECK (
            staging_row_count IS NULL
            OR staging_row_count >= 0
        )
);