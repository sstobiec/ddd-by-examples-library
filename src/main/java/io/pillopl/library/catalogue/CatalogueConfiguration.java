package io.pillopl.library.catalogue;

import io.pillopl.library.commons.events.DomainEvents;
import io.pillopl.library.commons.events.publisher.DomainEventsConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Spring configuration for the Catalogue module.
 * <p>
 * This class configures the Spring context for the Catalogue module, including the {@link Catalogue} service
 * and the {@link CatalogueDatabase}. It also imports database configuration and event publisher configuration.
 * </p>
 */
@Configuration
@EnableAutoConfiguration
@Import({CatalogueDatabaseConfig.class, DomainEventsConfig.class})
public class CatalogueConfiguration {

    /**
     * Creates the main Catalogue service bean.
     *
     * @param catalogueDatabase the database repository.
     * @param domainEvents      the domain events publisher.
     * @return the configured {@link Catalogue} service.
     */
    @Bean
    Catalogue catalogue(CatalogueDatabase catalogueDatabase, DomainEvents domainEvents) {
        return new Catalogue(catalogueDatabase, domainEvents);
    }

    /**
     * Creates the CatalogueDatabase repository bean.
     *
     * @param jdbcTemplate the Spring JDBC template.
     * @return the configured {@link CatalogueDatabase}.
     */
    @Bean
    CatalogueDatabase catalogueDatabase(JdbcTemplate jdbcTemplate) {
        return new CatalogueDatabase(jdbcTemplate);
    }

    /**
     * Initializes the catalogue with sample data when running in the 'local' profile.
     * <p>
     * Adds 'Effective Java' to the catalogue and creates a restricted instance of it.
     * </p>
     *
     * @param catalogue the catalogue service to use for initialization.
     * @return a {@link CommandLineRunner} that executes the initialization.
     */
    @Profile("local")
    @Bean
    CommandLineRunner init(Catalogue catalogue) {
        return args -> {
            catalogue.addBook("Joshua Bloch", "Effective Java", "0321125215").get();
            catalogue.addBookInstance("0321125215", BookType.Restricted).get();
        };
    }
}
