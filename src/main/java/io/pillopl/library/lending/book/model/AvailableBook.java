package io.pillopl.library.lending.book.model;

import io.pillopl.library.catalogue.BookId;
import io.pillopl.library.catalogue.BookType;
import io.pillopl.library.commons.aggregates.Version;
import io.pillopl.library.lending.librarybranch.model.LibraryBranchId;
import io.pillopl.library.lending.patron.model.PatronEvent.BookPlacedOnHold;
import io.pillopl.library.lending.patron.model.PatronId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * Represents a book that is currently available in the library.
 * <p>
 * An {@code AvailableBook} is located at a specific library branch and can be placed on hold
 * by a patron. It is one of the possible states of a {@link Book}.
 */
@Value
@AllArgsConstructor
@EqualsAndHashCode(of = "bookInformation")
public class AvailableBook implements Book {

    /**
     * Basic information about the book.
     */
    @NonNull
    BookInformation bookInformation;

    /**
     * The library branch where this book is currently located.
     */
    @NonNull
    LibraryBranchId libraryBranch;

    /**
     * The version of this aggregate state.
     */
    @NonNull
    Version version;

    /**
     * Constructs an AvailableBook with individual fields.
     *
     * @param bookId          the unique book ID
     * @param type            the book type
     * @param libraryBranchId the branch ID where the book is located
     * @param version         the aggregate version
     */
    public AvailableBook(BookId bookId, BookType type, LibraryBranchId libraryBranchId, Version version) {
        this(new BookInformation(bookId, type), libraryBranchId, version);
    }

    /**
     * Checks if the book is restricted.
     *
     * @return {@code true} if the book type is {@link BookType#Restricted}, {@code false} otherwise
     */
    public boolean isRestricted() {
        return bookInformation.getBookType().equals(BookType.Restricted);
    }

    /**
     * @see Book#bookId()
     */
    public BookId getBookId() {
        return bookInformation.getBookId();
    }

    /**
     * Transitions this book to the {@link BookOnHold} state when a valid {@link BookPlacedOnHold} event occurs.
     *
     * @param bookPlacedOnHold the event triggering the state change
     * @return a new {@link BookOnHold} instance representing the new state
     */
    public BookOnHold handle(BookPlacedOnHold bookPlacedOnHold) {
        return new BookOnHold(
                bookInformation,
                new LibraryBranchId(bookPlacedOnHold.getLibraryBranchId()),
                new PatronId(bookPlacedOnHold.getPatronId()),
                bookPlacedOnHold.getHoldTill(),
                version);
    }
}

