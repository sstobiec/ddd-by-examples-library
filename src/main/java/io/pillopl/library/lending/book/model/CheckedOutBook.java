package io.pillopl.library.lending.book.model;

import io.pillopl.library.catalogue.BookId;
import io.pillopl.library.catalogue.BookType;
import io.pillopl.library.commons.aggregates.Version;
import io.pillopl.library.lending.librarybranch.model.LibraryBranchId;
import io.pillopl.library.lending.patron.model.PatronEvent;
import io.pillopl.library.lending.patron.model.PatronId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * Represents a book that is currently checked out by a patron.
 * <p>
 * A {@code CheckedOutBook} is in possession of a patron and is associated with the library branch
 * where the checkout happened.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "bookInformation")
public class CheckedOutBook implements Book {

    /**
     * Basic information about the book.
     */
    @NonNull
    BookInformation bookInformation;

    /**
     * The library branch where the book was checked out.
     */
    @NonNull
    LibraryBranchId checkedOutAt;

    /**
     * The patron who has checked out the book.
     */
    @NonNull
    PatronId byPatron;

    /**
     * The version of this aggregate state.
     */
    @NonNull
    Version version;

    /**
     * Constructs a CheckedOutBook with individual fields.
     *
     * @param bookId          the unique book ID
     * @param type            the book type
     * @param libraryBranchId the branch ID where checkout occurred
     * @param patronId        the patron ID who checked out the book
     * @param version         the aggregate version
     */
    public CheckedOutBook(BookId bookId, BookType type, LibraryBranchId libraryBranchId, PatronId patronId, Version version) {
        this(new BookInformation(bookId, type), libraryBranchId, patronId, version);
    }

    /**
     * @see Book#bookId()
     */
    public BookId getBookId() {
        return bookInformation.getBookId();
    }

    /**
     * Transitions the book to {@link AvailableBook} when it is returned by the patron.
     *
     * @param bookReturnedByPatron event indicating the book was returned
     * @return the book in {@link AvailableBook} state
     */
    public AvailableBook handle(PatronEvent.BookReturned bookReturnedByPatron) {
        return new AvailableBook(
                bookInformation,
                new LibraryBranchId(bookReturnedByPatron.getLibraryBranchId()),
                version);
    }



}

