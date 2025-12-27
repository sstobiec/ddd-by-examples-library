package io.pillopl.library.commons.events.publisher;

import io.pillopl.library.commons.events.DomainEvent;
import io.pillopl.library.commons.events.DomainEvents;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Implementation of {@link DomainEvents} that forwards events directly to Spring's {@link ApplicationEventPublisher}.
 * This is a synchronous publisher.
 */
@AllArgsConstructor
public class JustForwardDomainEventPublisher implements DomainEvents {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Publishes the domain event using Spring's event mechanism.
     *
     * @param event the domain event to publish
     */
    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
