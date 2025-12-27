package io.pillopl.library.catalogue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

/**
 * Represents a physical copy (instance) of a book in the library.
 * <p>
 * Each instance is uniquely identified by a {@link BookId} and is associated with a specific {@link ISBN}.
 * It also carries a {@link BookType} which defines its circulation rules.
 * </p>
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class BookInstance {

    /**
     * The ISBN of the book this instance belongs to.
     */
    @NonNull
    ISBN bookIsbn;

    /**
     * The unique identifier for this physical copy.
     */
    @NonNull
    BookId bookId;

    /**
     * The type of the book (e.g., Restricted, Circulating).
     */
    @NonNull
    BookType bookType;

    /**
     * Factory method to create a new BookInstance.
     * <p>
     * Generates a new unique {@link BookId} for the instance.
     * </p>
     *
     * @param book     the book definition this instance is based on.
     * @param bookType the type of this specific instance.
     * @return a new BookInstance with a generated ID.
     */
    static BookInstance instanceOf(Book book, BookType bookType) {
        return new BookInstance(book.getBookIsbn(), new BookId(UUID.randomUUID()), bookType);

    }
}
