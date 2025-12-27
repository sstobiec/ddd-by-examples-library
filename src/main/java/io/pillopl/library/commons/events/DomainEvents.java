package io.pillopl.library.commons.events;

import io.vavr.collection.List;

/**
 * Interface for publishing domain events.
 * Defines the contract for event propagation mechanisms.
 */
public interface DomainEvents {

    /**
     * Publishes a single domain event.
     *
     * @param event the domain event to publish
     */
    void publish(DomainEvent event);

    /**
     * Publishes a list of domain events.
     *
     * @param events the list of domain events to publish
     */
    default void publish(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
