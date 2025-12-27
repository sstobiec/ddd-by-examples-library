package io.pillopl.library.catalogue;

import io.pillopl.library.commons.events.DomainEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a new book instance is added to the catalogue.
 * <p>
 * This event carries information about the newly added physical copy, including its ID, ISBN, and type.
 * Consumers of this event can update their read models or trigger other processes.
 * </p>
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BookInstanceAddedToCatalogue implements DomainEvent {

    /**
     * Unique ID of the event.
     */
    UUID eventId = UUID.randomUUID();

    /**
     * ISBN of the added book.
     */
    String isbn;

    /**
     * Type of the added book.
     */
    BookType type;

    /**
     * Unique ID of the added book instance.
     */
    UUID bookId;

    /**
     * Timestamp when the event occurred.
     */
    Instant when = Instant.now();

    /**
     * Constructs the event from a BookInstance.
     *
     * @param bookInstance the book instance that was added.
     */
    BookInstanceAddedToCatalogue(BookInstance bookInstance) {
        this(bookInstance.getBookIsbn().getIsbn(), bookInstance.getBookType(), bookInstance.getBookId().getBookId());
    }

    /**
     * Returns the aggregate ID associated with this event.
     *
     * @return the UUID of the book instance.
     */
    @Override
    public UUID getAggregateId() {
        return bookId;
    }
}
