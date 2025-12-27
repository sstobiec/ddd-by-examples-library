package io.pillopl.library.commons.events.publisher;

import io.micrometer.core.instrument.MeterRegistry;
import io.pillopl.library.commons.events.DomainEvents;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for domain event infrastructure.
 * Sets up the domain events publisher with metering capabilities.
 */
@Configuration
public class DomainEventsConfig {

    /**
     * Creates and configures the DomainEvents bean.
     * Wraps the default publisher with a metered publisher for monitoring.
     *
     * @param applicationEventPublisher Spring's application event publisher
     * @param meterRegistry             the registry for application metrics
     * @return a configured DomainEvents instance
     */
    @Bean
    DomainEvents domainEvents(ApplicationEventPublisher applicationEventPublisher, MeterRegistry meterRegistry) {
        return new MeteredDomainEventPublisher(new JustForwardDomainEventPublisher(applicationEventPublisher), meterRegistry);
    }
}
