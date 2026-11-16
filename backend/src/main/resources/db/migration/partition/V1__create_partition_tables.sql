CREATE TABLE partition_tables (
    /*変更した場合は、PartitionTableDTODiscoverySqlを変更すること*/
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    table_name VARCHAR(255) NOT NULL UNIQUE,

    partition_strategy VARCHAR(50) NOT NULL,
    partition_column VARCHAR(255) NOT NULL
);