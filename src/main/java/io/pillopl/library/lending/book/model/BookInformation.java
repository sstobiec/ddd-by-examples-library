package io.pillopl.library.lending.book.model;


import io.pillopl.library.catalogue.BookId;
import io.pillopl.library.catalogue.BookType;
import lombok.NonNull;
import lombok.Value;

/**
 * Value object holding static information about a book.
 * <p>
 * Contains the book's unique identifier and its classification type.
 * This information does not change regardless of the book's state (available, checked out, etc.).
 */
@Value
public class BookInformation {

    /**
     * The unique identifier of the book.
     */
    @NonNull
    BookId bookId;

    /**
     * The type of the book, which may influence lending policies.
     */
    @NonNull
    BookType bookType;
}
