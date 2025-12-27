package io.pillopl.library.lending.book.model;

import io.pillopl.library.catalogue.BookId;
import io.vavr.control.Option;

/**
 * Repository interface for accessing and persisting {@link Book} aggregates.
 */
public interface BookRepository {

    /**
     * Finds a book by its unique identifier.
     *
     * @param bookId the ID of the book to find
     * @return an {@link Option} containing the {@link Book} if found, or empty if not
     */
    Option<Book> findBy(BookId bookId);

    /**
     * Saves the state of a book.
     * <p>
     * This method persists the changes to the book aggregate.
     *
     * @param book the {@link Book} instance to save
     */
    void save(Book book);
}
