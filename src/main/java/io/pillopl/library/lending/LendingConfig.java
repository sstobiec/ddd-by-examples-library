package io.pillopl.library.lending;

import io.pillopl.library.commons.events.publisher.DomainEventsConfig;
import io.pillopl.library.lending.book.infrastructure.BookConfiguration;
import io.pillopl.library.lending.dailysheet.infrastructure.DailySheetConfiguration;
import io.pillopl.library.lending.patron.infrastructure.PatronConfiguration;
import io.pillopl.library.lending.patronprofile.infrastructure.PatronProfileConfiguration;
import io.pillopl.library.lending.patronprofile.web.WebConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main configuration class for the Lending module.
 * <p>
 * This class serves as the entry point for the module's Spring context, importing necessary
 * configurations for persistence, web controllers, domain events, and sub-module specific settings
 * (Book, DailySheet, Patron, PatronProfile).
 * <p>
 * It also enables Spring's scheduling capabilities required for periodic tasks within the module.
 *
 * @see LendingDatabaseConfig
 * @see WebConfiguration
 * @see PatronProfileConfiguration
 * @see PatronConfiguration
 * @see DailySheetConfiguration
 * @see BookConfiguration
 * @see DomainEventsConfig
 */
@Configuration
@EnableScheduling
@Import({LendingDatabaseConfig.class,
        WebConfiguration.class,
        PatronProfileConfiguration.class,
        PatronConfiguration.class,
        DailySheetConfiguration.class,
        BookConfiguration.class,
        DomainEventsConfig.class})
public class LendingConfig {
}
