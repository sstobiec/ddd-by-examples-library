package io.pillopl.library.commons.events.publisher;

import io.pillopl.library.commons.events.DomainEvent;
import io.vavr.collection.List;

/**
 * Interface for storing domain events.
 * Used for the Store-and-Forward pattern to ensure reliable event delivery.
 */
public interface EventsStorage {

    /**
     * Saves a domain event to the storage.
     *
     * @param event the domain event to save
     */
    void save(DomainEvent event);

    /**
     * Retrieves a list of events that are ready to be published.
     *
     * @return a list of unpublished domain events
     */
    List<DomainEvent> toPublish();

    /**
     * Marks a list of events as successfully published.
     *
     * @param events the list of domain events that have been published
     */
    void published(List<DomainEvent> events);
}
