package com.example.myauthapp.partition.dto;

import java.time.LocalDateTime;

public class PartitionTableDTO {

    private final Long id;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String tableName;
    private final String partitionStrategy;
    private final String partitionColumn;

    public PartitionTableDTO(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String tableName,
        String partitionStrategy,
        String partitionColumn
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tableName = tableName;
        this.partitionStrategy = partitionStrategy;
        this.partitionColumn = partitionColumn;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPartitionStrategy() {
        return partitionStrategy;
    }

    public String getPartitionColumn() {
        return partitionColumn;
    }
}