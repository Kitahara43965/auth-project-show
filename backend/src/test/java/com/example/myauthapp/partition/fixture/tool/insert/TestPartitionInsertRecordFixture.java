package com.example.myauthapp.partition.fixture.tool.insert;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.insert.TestPartitionDefaultRecoveryInsertJdbcRepository;
import com.example.myauthapp.partition.repository.insert.TestPartitionStagingRecoveryInsertJdbcRepository;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.dto.TestPartitionStagingInsertRecord;

@Component
public class TestPartitionInsertRecordFixture {

    private final TestPartitionDefaultRecoveryInsertJdbcRepository
        testPartitionDefaultRecoveryInsertJdbcRepository;

    private final TestPartitionStagingRecoveryInsertJdbcRepository
        testPartitionStagingRecoveryInsertJdbcRepository;

    public TestPartitionInsertRecordFixture(
        TestPartitionDefaultRecoveryInsertJdbcRepository
            testPartitionDefaultRecoveryInsertJdbcRepository,
        TestPartitionStagingRecoveryInsertJdbcRepository
            testPartitionStagingRecoveryInsertJdbcRepository
    ) {
        this.testPartitionDefaultRecoveryInsertJdbcRepository =
            testPartitionDefaultRecoveryInsertJdbcRepository;
        this.testPartitionStagingRecoveryInsertJdbcRepository =
            testPartitionStagingRecoveryInsertJdbcRepository;
    }

    public void insertTestPartitionDefaultInsertRecord(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord,
        int maxRecordDataCount
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        Objects.requireNonNull(
            testPartitionDefaultInsertRecord,
            "testPartitionDefaultInsertRecord must not be null"
        );

        for (
            int recordDataCount = 1;
            recordDataCount <= maxRecordDataCount;
            recordDataCount++
        ) {
            testPartitionDefaultRecoveryInsertJdbcRepository
                .insertIntoDefaultPartition(
                    partitionTablePropertiesContext,
                    testPartitionDefaultInsertRecord
                );
        }
    }

    public void insertTestPartitionStagingInsertRecord(
        TestPartitionStagingInsertRecord testPartitionStagingInsertRecord,
        UUID publicPartitionOperationId,
        int maxRecordDataCount
    ) {

        Objects.requireNonNull(
            testPartitionStagingInsertRecord,
            "testPartitionStagingInsertRecord must not be null"
        );
        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        for (
            int recordDataCount = 1;
            recordDataCount <= maxRecordDataCount;
            recordDataCount++
        ) {
            testPartitionStagingRecoveryInsertJdbcRepository
                .insertIntoStagingPartition(
                    testPartitionStagingInsertRecord,
                    publicPartitionOperationId
                );
        }
    }

}