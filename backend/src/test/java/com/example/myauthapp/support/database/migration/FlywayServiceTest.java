package com.example.myauthapp.support.database.migration;

import org.junit.jupiter.api.BeforeEach;
import com.example.myauthapp.support.database.cleaner.TestDatabaseCleaner;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FlywayServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.deleteAllDatabase();
    }

    @SpringBootTest
    @ActiveProfiles("test")
    class FlywayMigrationTest {

        @Autowired
        private Flyway flyway;

        @Test
        void flywayMigrationIsSuccessful() {
            assertDoesNotThrow(() -> flyway.validate());
        }
    }
}