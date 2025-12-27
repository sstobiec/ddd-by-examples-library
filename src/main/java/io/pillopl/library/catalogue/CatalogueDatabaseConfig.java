package io.pillopl.library.catalogue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Database configuration for the Catalogue module.
 * <p>
 * This class sets up the embedded H2 database, JDBC templates, and transaction management
 * specifically for the catalogue.
 * </p>
 */
@Configuration
class CatalogueDatabaseConfig {

    /**
     * Configures the JdbcTemplate.
     *
     * @return the JdbcTemplate.
     */
    @Bean
    JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    /**
     * Configures NamedParameterJdbcOperations.
     *
     * @return the NamedParameterJdbcTemplate.
     */
    @Bean
    NamedParameterJdbcOperations operations() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    /**
     * Configures the transaction manager.
     *
     * @return the DataSourceTransactionManager.
     */
    @Bean
    PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * Configures the embedded H2 data source.
     * <p>
     * Initializes the database schema using 'create_catalogue_book.sql'.
     * </p>
     *
     * @return the configured DataSource.
     */
    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .addScript("create_catalogue_book.sql")
                .build();
    }
}