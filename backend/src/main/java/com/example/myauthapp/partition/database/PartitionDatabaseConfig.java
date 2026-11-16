package com.example.myauthapp.partition.database;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PartitionDatabaseConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.partition")
    public DataSourceProperties partitionDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource partitionDataSource(
        @Qualifier("partitionDataSourceProperties")
        DataSourceProperties properties
    ) {
        return properties
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean
    public JdbcTemplate partitionJdbcTemplate(
        @Qualifier("partitionDataSource")
        DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager partitionTransactionManager(
        @Qualifier("partitionDataSource")
        DataSource dataSource
    ) {
        return new DataSourceTransactionManager(dataSource);
    }
}