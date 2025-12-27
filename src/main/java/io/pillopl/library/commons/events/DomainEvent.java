package io.pillopl.library.commons.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Marker interface for all domain events in the system.
 * Represents a significant occurrence in the domain.
 */
public interface DomainEvent {

    /**
     * Returns the unique identifier of the event.
     *
     * @return the UUID of the event
     */
    UUID getEventId();

    /**
     * Returns the identifier of the aggregate that generated this event.
     *
     * @return the UUID of the aggregate
     */
    UUID getAggregateId();

    /**
     * Returns the timestamp when the event occurred.
     *
     * @return the Instant representing the event occurrence time
     */
    Instant getWhen();
}
