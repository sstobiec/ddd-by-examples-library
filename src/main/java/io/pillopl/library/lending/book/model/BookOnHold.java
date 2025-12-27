package io.pillopl.library.lending.book.model;

import io.pillopl.library.catalogue.BookId;
import io.pillopl.library.catalogue.BookType;
import io.pillopl.library.commons.aggregates.Version;
import io.pillopl.library.lending.librarybranch.model.LibraryBranchId;
import io.pillopl.library.lending.patron.model.PatronEvent.BookCheckedOut;
import io.pillopl.library.lending.patron.model.PatronEvent.BookHoldCanceled;
import io.pillopl.library.lending.patron.model.PatronEvent.BookHoldExpired;
import io.pillopl.library.lending.patron.model.PatronEvent.BookReturned;
import io.pillopl.library.lending.patron.model.PatronId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

/**
 * Represents a book that has been placed on hold for a specific patron.
 * <p>
 * A {@code BookOnHold} is reserved at a specific library branch until a certain date.
 * From this state, it can transition to:
 * <ul>
 *     <li>{@link AvailableBook} (if returned, expired, or canceled)</li>
 *     <li>{@link CheckedOutBook} (if collected by the patron)</li>
 * </ul>
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "bookInformation")
public class BookOnHold implements Book {

    /**
     * Basic information about the book.
     */
    @NonNull
    BookInformation bookInformation;

    /**
     * The library branch where the hold is placed.
     */
    @NonNull
    LibraryBranchId holdPlacedAt;

    /**
     * The patron who requested the hold.
     */
    @NonNull
    PatronId byPatron;

    /**
     * The timestamp until when the hold is valid.
     */
    @NonNull
    Instant holdTill;

    /**
     * The version of this aggregate state.
     */
    @NonNull
    Version version;

    /**
     * Constructs a BookOnHold with individual fields.
     *
     * @param bookId          the unique book ID
     * @param type            the book type
     * @param libraryBranchId the branch ID where the hold is placed
     * @param patronId        the patron ID who placed the hold
     * @param holdTill        the expiration time of the hold
     * @param version         the aggregate version
     */
    public BookOnHold(BookId bookId, BookType type, LibraryBranchId libraryBranchId, PatronId patronId, Instant holdTill, Version version) {
        this(new BookInformation(bookId, type), libraryBranchId, patronId, holdTill, version);
    }

    /**
     * Transitions the book back to {@link AvailableBook} when it is returned.
     *
     * @param bookReturned event indicating the book was returned
     * @return the book in {@link AvailableBook} state
     */
    public AvailableBook handle(BookReturned bookReturned) {
        return new AvailableBook(
                bookInformation, new LibraryBranchId(bookReturned.getLibraryBranchId()),
                version);
    }

    /**
     * Transitions the book back to {@link AvailableBook} when the hold expires.
     *
     * @param bookHoldExpired event indicating the hold expiration
     * @return the book in {@link AvailableBook} state
     */
    public AvailableBook handle(BookHoldExpired bookHoldExpired) {
        return new AvailableBook(
                bookInformation,
                new LibraryBranchId(bookHoldExpired.getLibraryBranchId()),
                version);
    }

    /**
     * Transitions the book to {@link CheckedOutBook} when the patron collects it.
     *
     * @param bookCheckedOut event indicating the book checkout
     * @return the book in {@link CheckedOutBook} state
     */
    public CheckedOutBook handle(BookCheckedOut bookCheckedOut) {
        return new CheckedOutBook(
                bookInformation,
                new LibraryBranchId(bookCheckedOut.getLibraryBranchId()),
                new PatronId(bookCheckedOut.getPatronId()),
                version);
    }

    /**
     * Transitions the book back to {@link AvailableBook} when the hold is canceled.
     *
     * @param bookHoldCanceled event indicating the hold cancellation
     * @return the book in {@link AvailableBook} state
     */
    public AvailableBook handle(BookHoldCanceled bookHoldCanceled) {
        return new AvailableBook(
                bookInformation, new LibraryBranchId(bookHoldCanceled.getLibraryBranchId()),
                version);
    }


    /**
     * @see Book#bookId()
     */
    public BookId getBookId() {
        return bookInformation.getBookId();
    }

    /**
     * Checks if the hold was placed by the specified patron.
     *
     * @param patronId the patron ID to check
     * @return {@code true} if the hold belongs to the given patron, {@code false} otherwise
     */
    public boolean by(PatronId patronId) {
        return byPatron.equals(patronId);
    }
}

