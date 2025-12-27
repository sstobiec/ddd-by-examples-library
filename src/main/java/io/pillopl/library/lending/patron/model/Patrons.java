package io.pillopl.library.lending.patron.model;

import io.vavr.control.Option;

/**
 * Repository interface for accessing and persisting {@link Patron} aggregates.
 */
public interface Patrons {

    /**
     * Finds a patron by their unique identifier.
     *
     * @param patronId the ID of the patron to find
     * @return an {@link Option} containing the {@link Patron} if found, or empty if not
     */
    Option<Patron> findBy(PatronId patronId);

    /**
     * Publishes a patron event, effectively persisting the changes associated with it.
     * <p>
     * This method acts as a save mechanism where the side effect of the event
     * (e.g., creating a new patron) is handled by the persistence layer.
     *
     * @param event the {@link PatronEvent} to publish/handle
     * @return the {@link Patron} aggregate affected by the event
     */
    Patron publish(PatronEvent event);
}
