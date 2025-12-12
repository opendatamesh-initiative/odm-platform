package org.opendatamesh.platform.pp.policy.server.controllers;

import com.zaxxer.hikari.HikariDataSource;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration
public class TestContainerConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private PostgreSQLContainer<?> postgresContainer;

    @Bean
    @Primary
    public PostgreSQLContainer<?> postgresContainer() {
        postgresContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:15")
        )
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");

        postgresContainer.start();

        logger.info("Started PostgreSQL container: {}", postgresContainer.getJdbcUrl());

        return postgresContainer;
    }

    @Bean
    @Primary
    public DataSource dataSource(PostgreSQLContainer<?> postgresContainer) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(postgresContainer.getJdbcUrl());
        dataSource.setUsername(postgresContainer.getUsername());
        dataSource.setPassword(postgresContainer.getPassword());
        dataSource.setDriverClassName(postgresContainer.getDriverClassName());
        return dataSource;
    }

    @PreDestroy
    public void cleanup() {
        if (postgresContainer != null && postgresContainer.isRunning()) {
            postgresContainer.stop();
            logger.info("Stopped PostgreSQL container");
        }
    }
}

