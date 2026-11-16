package com.example.myauthapp.partition.repository.database.delete;

import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.config.PartitionProperties;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;

@Repository
public class TestPartitionRangeDatabaseRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public TestPartitionRangeDatabaseRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public void deletePartitionRangeDatabase() {
        String partitionRangeTableName =
            Objects.requireNonNull(
                partitionProperties.getPartitionRangeTableName(),
                "partitionRangeTableName must not be null"
            );

        partitionJdbcTemplate.update(
            "DELETE FROM " + partitionRangeTableName
        );
    }

    
}