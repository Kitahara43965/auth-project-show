package com.example.myauthapp.support.database.service;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import com.example.myauthapp.support.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.auth.token.repository.RefreshTokenFamilyRepository;
import com.example.myauthapp.auth.token.repository.RefreshTokenRepository;
import com.example.myauthapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseServiceTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenFamilyRepository refreshTokenFamilyRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.deleteAllDatabase();
    }

    @Test
    void checkDatabase() throws Exception {

        DatabaseMetaData metaData =
            dataSource
                .getConnection()
                .getMetaData();

        assertNotNull(metaData);

        assertEquals(
            "PostgreSQL",
            metaData.getDatabaseProductName()
        );
    }

}