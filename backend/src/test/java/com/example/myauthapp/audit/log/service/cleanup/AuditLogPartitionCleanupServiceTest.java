package com.example.myauthapp.audit.log.service.cleanup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.fixture.service.cleanup.TestPartitionCleanupServiceFixture;
import com.example.myauthapp.audit.log.factory.AuditLogPartitionContextGroupFactory;

@SpringBootTest
@ActiveProfiles("test")
class AuditLogPartitionCleanupServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestPartitionCleanupServiceFixture testPartitionCleanupServiceFixture;

    @Autowired
    private AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory;


    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void cleanupPartitionBeforeShouldDropOldPartitions(){
        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();

        testPartitionCleanupServiceFixture.onCleanupPartitionBeforeShouldDropOldPartitions(
            partitionContextGroup
        );
    }//onCleanupPartitionBeforeShouldDropOldPartitions

    @Test
    void cleanupPartitionBeforeShouldKeepPartitionAtBoundary() {
        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();

        testPartitionCleanupServiceFixture.onCleanupPartitionBeforeShouldKeepPartitionAtBoundary(
            partitionContextGroup
        );
    }//cleanupPartitionBeforeShouldKeepPartitionAtBoundary

}