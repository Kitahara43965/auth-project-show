package com.example.myauthapp.database.migration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FlywayMigrationTest {

    @Autowired
    @Qualifier("userFlyway")
    private Flyway userFlyway;

    @Autowired
    @Qualifier("auditFlyway")
    private Flyway auditFlyway;

    @Autowired
    @Qualifier("partitionFlyway")
    private Flyway partitionFlyway;

    @Test
    void userFlywayMigrationIsSuccessful() {
        assertDoesNotThrow(
            () -> userFlyway.validate()
        );
    }

    @Test
    void auditFlywayMigrationIsSuccessful() {
        assertDoesNotThrow(
            () -> auditFlyway.validate()
        );
    }

    @Test
    void partitionFlywayMigrationIsSuccessful() {
        assertDoesNotThrow(
            () -> partitionFlyway.validate()
        );
    }
}