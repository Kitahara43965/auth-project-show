CREATE TABLE partition_stagings (
    public_partition_operation_id UUID NOT NULL,
    record_id BIGINT NOT NULL,
    partition_local_date_time TIMESTAMP NOT NULL,
    record_data JSONB NOT NULL,

    PRIMARY KEY (public_partition_operation_id, record_id),

    CONSTRAINT fk_partition_stagings_operation
        FOREIGN KEY (public_partition_operation_id)
        REFERENCES partition_operations(public_partition_operation_id)
);