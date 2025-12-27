package io.pillopl.library.lending.book.model;

import io.pillopl.library.catalogue.BookId;
import io.pillopl.library.catalogue.BookType;
import io.pillopl.library.commons.aggregates.Version;

/**
 * Represents a Book in the Lending context.
 * <p>
 * This interface defines the common contract for a book in various states (Available, OnHold, CheckedOut).
 * It provides access to basic book information like ID, type, and version.
 */
public interface Book {

    /**
     * Retrieves the unique identifier of the book.
     *
     * @return the {@link BookId}
     */
    default BookId bookId() {
        return getBookInformation().getBookId();
    }

    /**
     * Retrieves the type of the book (e.g., Restricted, Circulating).
     *
     * @return the {@link BookType}
     */
    default BookType type() {
        return getBookInformation().getBookType();
    }

    /**
     * Gets the detailed information about the book.
     *
     * @return the {@link BookInformation} value object
     */
    BookInformation getBookInformation();

    /**
     * Gets the current version of the aggregate.
     * Used for optimistic locking and concurrency control.
     *
     * @return the {@link Version} of the book aggregate
     */
    Version getVersion();

}

