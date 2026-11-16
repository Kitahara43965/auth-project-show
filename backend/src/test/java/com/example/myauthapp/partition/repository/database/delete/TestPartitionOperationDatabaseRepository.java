package com.example.myauthapp.partition.repository.database.delete;

import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;

@Repository
public class TestPartitionOperationDatabaseRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public TestPartitionOperationDatabaseRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public void deletePartitionOperationDatabase() {
        String partitionOperationTableName =
            Objects.requireNonNull(
                partitionProperties.getPartitionOperationTableName(),
                "partitionOperationTableName must not be null"
            );

        partitionJdbcTemplate.update(
            "DELETE FROM " + partitionOperationTableName
        );
    }

    
}