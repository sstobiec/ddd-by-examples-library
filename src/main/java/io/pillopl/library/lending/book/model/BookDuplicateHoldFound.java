package io.pillopl.library.lending.book.model;

import io.pillopl.library.commons.events.DomainEvent;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;


/**
 * Domain event raised when a duplicate hold on the same book is detected.
 * <p>
 * This event captures the details of the conflict, including the original and second patron involved.
 */
@Value
public class BookDuplicateHoldFound implements DomainEvent {
    
    /**
     * Unique identifier for this event instance.
     */
    @NonNull UUID eventId = UUID.randomUUID();

    /**
     * The timestamp when the duplicate hold was detected.
     */
    @NonNull Instant when;

    /**
     * The ID of the patron who already holds the book or placed the first hold.
     */
    @NonNull UUID firstPatronId;

    /**
     * The ID of the patron who attempted to place the duplicate hold.
     */
    @NonNull UUID secondPatronId;

    /**
     * The ID of the library branch where the event occurred.
     */
    @NonNull UUID libraryBranchId;

    /**
     * The ID of the book involved in the duplicate hold.
     */
    @NonNull UUID bookId;

    /**
     * Returns the ID of the aggregate associated with this event (the book ID).
     *
     * @return the book ID as a UUID
     */
    @Override
    public UUID getAggregateId() {
        return bookId;
    }
}