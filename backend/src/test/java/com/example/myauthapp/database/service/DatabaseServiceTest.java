package com.example.myauthapp.database.service;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;


@SpringBootTest
@ActiveProfiles("test")
class DatabaseServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    @Qualifier("userDataSource")
    private DataSource userDataSource;

    @Autowired
    @Qualifier("auditDataSource")
    private DataSource auditDataSource;

    @Autowired
    @Qualifier("partitionDataSource")
    private DataSource partitionDataSource;


    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void checkUserDatabase() throws Exception {

        DatabaseMetaData metaData =
            userDataSource
                .getConnection()
                .getMetaData();

        assertNotNull(metaData);

        assertEquals(
            "PostgreSQL",
            metaData.getDatabaseProductName()
        );
    }

    @Test
    void checkAuditDatabase() throws Exception {

        DatabaseMetaData metaData =
            auditDataSource
                .getConnection()
                .getMetaData();

        assertNotNull(metaData);

        assertEquals(
            "PostgreSQL",
            metaData.getDatabaseProductName()
        );
    }

    @Test
    void checkPartitionDatabase() throws Exception {

        DatabaseMetaData metaData =
            partitionDataSource
                .getConnection()
                .getMetaData();

        assertNotNull(metaData);

        assertEquals(
            "PostgreSQL",
            metaData.getDatabaseProductName()
        );
    }

}