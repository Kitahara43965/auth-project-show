package com.example.myauthapp.partition.repository.insert;

import java.util.Objects;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.TestPartitionStagingInsertRecord;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.config.PartitionProperties;

@Repository
public class TestPartitionStagingRecoveryInsertJdbcRepository {

    private final JdbcTemplate partitionJdbcTemplate;
    private final PartitionProperties partitionProperties;

    public TestPartitionStagingRecoveryInsertJdbcRepository(
        @Qualifier("partitionJdbcTemplate")
        JdbcTemplate partitionJdbcTemplate,
        PartitionProperties partitionProperties
    ) {
        this.partitionJdbcTemplate = partitionJdbcTemplate;
        this.partitionProperties = partitionProperties;
    }

    public void insertIntoStagingPartition(
        TestPartitionStagingInsertRecord testPartitionStagingInsertRecord,
        UUID publicPartitionOperationId
    ) {

        Objects.requireNonNull(
            testPartitionStagingInsertRecord,
            "testPartitionStagingInsertRecord must not be null"
        );

        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        Long recordId = testPartitionStagingInsertRecord.getRecordId();

        List<String> columnNames =
            testPartitionStagingInsertRecord.getColumnNames();

        PreparedStatementSetter parameterBinder =
            testPartitionStagingInsertRecord.getParameterBinder();

        String partitionStagingTableName =
            partitionProperties.getPartitionStagingTableName();


        String columnList =
            String.join(", ", columnNames);

        String placeholders =
            String.join(
                ", ",
                Collections.nCopies(
                    columnNames.size() + 3,
                    "?"
                )
            );

        String sql = """
            INSERT INTO %s (
                %s,
                public_partition_operation_id,
                record_id,
                partition_local_date_time
            )
            VALUES (
                %s
            )
            """.formatted(
                partitionStagingTableName,
                columnList,
                placeholders
            );

        partitionJdbcTemplate.update(
            sql,
            preparedStatement -> {

                parameterBinder.setValues(preparedStatement);

                preparedStatement.setObject(
                    columnNames.size() + 1,
                    publicPartitionOperationId
                );

                preparedStatement.setObject(
                    columnNames.size() + 2,
                    recordId
                );

                preparedStatement.setObject(
                    columnNames.size() + 3,
                    testPartitionStagingInsertRecord.getPartitionLocalDateTime()
                );
            }
        );
    }

}