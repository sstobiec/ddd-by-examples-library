package io.pillopl.library.lending;

import io.pillopl.library.catalogue.BookId;
import io.pillopl.library.commons.aggregates.Version;
import io.pillopl.library.lending.book.model.AvailableBook;
import io.pillopl.library.lending.book.model.BookInformation;
import io.pillopl.library.lending.book.model.BookRepository;
import io.pillopl.library.lending.librarybranch.model.LibraryBranchId;
import io.pillopl.library.lending.patron.model.PatronEvent.PatronCreated;
import io.pillopl.library.lending.patron.model.PatronId;
import io.pillopl.library.lending.patron.model.Patrons;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;


import static io.pillopl.library.catalogue.BookType.Circulating;
import static io.pillopl.library.lending.patron.model.PatronType.Regular;

/**
 * Database configuration for the Lending module.
 * <p>
 * Configures the persistence layer using Spring JDBC and an embedded H2 database.
 * This configuration includes:
 * <ul>
 *     <li>{@link JdbcTemplate} and {@link NamedParameterJdbcOperations} for database access</li>
 *     <li>{@link PlatformTransactionManager} for transaction management</li>
 *     <li>{@link DataSource} initialization with schema scripts</li>
 * </ul>
 * <p>
 * It also provides a {@link CommandLineRunner} bean for the "local" profile to seed the database
 * with initial test data (a random book, library branch, and patron).
 */
@Configuration
@Slf4j
class LendingDatabaseConfig extends AbstractJdbcConfiguration {

    /**
     * Creates the JdbcTemplate bean.
     *
     * @return a new {@link JdbcTemplate} instance configured with the module's data source
     */
    @Bean
    JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    /**
     * Creates the NamedParameterJdbcOperations bean.
     *
     * @return a new {@link NamedParameterJdbcOperations} instance (specifically {@link NamedParameterJdbcTemplate})
     */
    @Bean
    NamedParameterJdbcOperations operations() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    /**
     * Creates the PlatformTransactionManager bean.
     *
     * @return a {@link DataSourceTransactionManager} for the module's data source
     */
    @Bean
    PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * Configures the embedded H2 DataSource.
     * <p>
     * The data source is initialized with scripts:
     * <ul>
     *     <li>create_patron_db.sql</li>
     *     <li>create_lending_book_db.sql</li>
     *     <li>create_sheets_db.sql</li>
     * </ul>
     *
     * @return the configured {@link DataSource}
     */
    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .addScript("create_patron_db.sql")
                .addScript("create_lending_book_db.sql")
                .addScript("create_sheets_db.sql")
                .build();
    }

    /**
     * Initializes the database with sample data when the "local" profile is active.
     * <p>
     * This runner creates:
     * <ul>
     *     <li>A circulating book instance available in the catalogue</li>
     *     <li>A regular patron</li>
     * </ul>
     * IDs generated are logged for reference.
     *
     * @param bookRepository the repository to save the book
     * @param patrons the patrons aggregate to publish the patron created event
     * @return a {@link CommandLineRunner} that executes the initialization logic
     */
    @Profile("local")
    @Bean
    CommandLineRunner init(BookRepository bookRepository, Patrons patrons) {
        return args -> {
            UUID bookId = UUID.randomUUID();
            UUID libraryBranchId = UUID.randomUUID();
            UUID patronId = UUID.randomUUID();

            AvailableBook availableBook = new AvailableBook(new BookInformation(new BookId(bookId), Circulating), new LibraryBranchId(libraryBranchId), new Version(0));
            bookRepository.save(availableBook);
            patrons.publish(PatronCreated.now(new PatronId(patronId), Regular));

            log.info("Created bookId: {}", bookId);
            log.info("Created libraryBranchId: {}", libraryBranchId);
            log.info("Created patronId: {}", patronId);
        };
    }
}
