package com.example.myauthapp.partition.fixture.tool.database.delete;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.repository.database.delete
    .TestPartitionTableDatabaseRepository;
import com.example.myauthapp.partition.repository.database.delete
    .TestPartitionRangeDatabaseRepository;
import com.example.myauthapp.partition.repository.database.delete
    .TestPartitionOperationDatabaseRepository;
import com.example.myauthapp.partition.repository.database.delete
    .TestPartitionStagingDatabaseRepository;


@Component
public class TestPartitionStagingDatabaseDeleteFixture {

    private final TestPartitionTableDatabaseRepository
        testPartitionTableDatabaseRepository;
    private final TestPartitionRangeDatabaseRepository
        testPartitionRangeDatabaseRepository;
    private final TestPartitionOperationDatabaseRepository
        testPartitionOperationDatabaseRepository;
    private final TestPartitionStagingDatabaseRepository
        testPartitionStagingDatabaseRepository;

    public TestPartitionStagingDatabaseDeleteFixture(
        TestPartitionTableDatabaseRepository
            testPartitionTableDatabaseRepository,
        TestPartitionRangeDatabaseRepository
            testPartitionRangeDatabaseRepository,
        TestPartitionOperationDatabaseRepository
            testPartitionOperationDatabaseRepository,
        TestPartitionStagingDatabaseRepository
            testPartitionStagingDatabaseRepository
    ) {
        this.testPartitionTableDatabaseRepository =
            testPartitionTableDatabaseRepository;
        this.testPartitionRangeDatabaseRepository =
            testPartitionRangeDatabaseRepository;
        this.testPartitionOperationDatabaseRepository =
            testPartitionOperationDatabaseRepository;
        this.testPartitionStagingDatabaseRepository =
            testPartitionStagingDatabaseRepository;
        
    }

    public void deleteAllPartitionDatabaseData() {
        testPartitionStagingDatabaseRepository
            .deletePartitionStagingDatabase();
        testPartitionOperationDatabaseRepository
            .deletePartitionOperationDatabase();
        testPartitionRangeDatabaseRepository
            .deletePartitionRangeDatabase();
        testPartitionTableDatabaseRepository
            .deletePartitionTableDatabase();
    }
}
