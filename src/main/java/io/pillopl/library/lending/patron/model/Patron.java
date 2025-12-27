package io.pillopl.library.lending.patron.model;


import io.pillopl.library.lending.book.model.AvailableBook;
import io.pillopl.library.lending.book.model.BookOnHold;
import io.pillopl.library.lending.librarybranch.model.LibraryBranchId;
import io.pillopl.library.lending.patron.model.PatronEvent.*;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import static io.pillopl.library.commons.events.EitherResult.announceFailure;
import static io.pillopl.library.commons.events.EitherResult.announceSuccess;
import static io.pillopl.library.lending.patron.model.PatronEvent.BookCheckedOut.bookCheckedOutNow;
import static io.pillopl.library.lending.patron.model.PatronEvent.BookCheckingOutFailed.bookCheckingOutFailedNow;
import static io.pillopl.library.lending.patron.model.PatronEvent.BookHoldCanceled.holdCanceledNow;
import static io.pillopl.library.lending.patron.model.PatronEvent.BookHoldCancelingFailed.holdCancelingFailedNow;
import static io.pillopl.library.lending.patron.model.PatronEvent.BookHoldFailed.bookHoldFailedNow;
import static io.pillopl.library.lending.patron.model.PatronEvent.BookPlacedOnHold.bookPlacedOnHoldNow;
import static io.pillopl.library.lending.patron.model.PatronEvent.BookPlacedOnHoldEvents.events;
import static io.pillopl.library.lending.patron.model.PatronHolds.MAX_NUMBER_OF_HOLDS;
import static io.pillopl.library.lending.patron.model.Rejection.withReason;

/**
 * Aggregate Root representing a Patron (user) of the library.
 * <p>
 * The Patron aggregate controls the lifecycle of holds and checkouts for a specific user.
 * It enforces policies regarding:
 * <ul>
 *     <li>Placing books on hold (eligibility, limits)</li>
 *     <li>Checking out books (must be on hold first)</li>
 *     <li>Canceling holds</li>
 * </ul>
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "patron")
public class Patron {

    @NonNull
    private final PatronInformation patron;

    @NonNull
    private final List<PlacingOnHoldPolicy> placingOnHoldPolicies;

    @NonNull
    private final OverdueCheckouts overdueCheckouts;

    @NonNull
    private final PatronHolds patronHolds;

    /**
     * Attempts to place an available book on hold for this patron with an open-ended duration.
     *
     * @param book the {@link AvailableBook} to place on hold
     * @return {@link Either} containing {@link BookPlacedOnHoldEvents} on success, or {@link BookHoldFailed} on failure
     */
    public Either<BookHoldFailed, BookPlacedOnHoldEvents> placeOnHold(AvailableBook book) {
        return placeOnHold(book, HoldDuration.openEnded());
    }

    /**
     * Attempts to place an available book on hold for this patron for a specific duration.
     * <p>
     * This method checks all {@link PlacingOnHoldPolicy} rules. If any policy is violated,
     * the hold is rejected. If successful, appropriate events are generated.
     *
     * @param aBook    the {@link AvailableBook} to place on hold
     * @param duration the {@link HoldDuration} for which the hold is requested
     * @return {@link Either} containing {@link BookPlacedOnHoldEvents} on success, or {@link BookHoldFailed} on failure
     */
    public Either<BookHoldFailed, BookPlacedOnHoldEvents> placeOnHold(AvailableBook aBook, HoldDuration duration) {
        Option<Rejection> rejection = patronCanHold(aBook, duration);
        if (rejection.isEmpty()) {
            BookPlacedOnHold bookPlacedOnHold = bookPlacedOnHoldNow(aBook.getBookId(), aBook.type(), aBook.getLibraryBranch(), patron.getPatronId(), duration);
            if (patronHolds.maximumHoldsAfterHolding(aBook)) {
                return announceSuccess(events(bookPlacedOnHold, MaximumNumberOhHoldsReached.now(patron, MAX_NUMBER_OF_HOLDS)));
            }
            return announceSuccess(events(bookPlacedOnHold));
        }
        return announceFailure(bookHoldFailedNow(rejection.get(), aBook.getBookId(), aBook.getLibraryBranch(), patron));
    }

    /**
     * Cancels an existing hold on a book.
     *
     * @param book the {@link BookOnHold} to cancel
     * @return {@link Either} containing {@link BookHoldCanceled} on success, or {@link BookHoldCancelingFailed} if the book was not on hold by this patron
     */
    public Either<BookHoldCancelingFailed, BookHoldCanceled> cancelHold(BookOnHold book) {
        if (patronHolds.a(book)) {
            return announceSuccess(holdCanceledNow(book.getBookId(), book.getHoldPlacedAt(), patron.getPatronId()));
        }
        return announceFailure(holdCancelingFailedNow(book.getBookId(), book.getHoldPlacedAt(), patron.getPatronId()));
    }

    /**
     * Checks out a book that is currently on hold for this patron.
     *
     * @param book     the {@link BookOnHold} to check out
     * @param duration the {@link CheckoutDuration} for the checkout
     * @return {@link Either} containing {@link BookCheckedOut} on success, or {@link BookCheckingOutFailed} if the checkout fails (e.g., book not on hold)
     */
    public Either<BookCheckingOutFailed, BookCheckedOut> checkOut(BookOnHold book, CheckoutDuration duration) {
        if (patronHolds.a(book)) {
            return announceSuccess(bookCheckedOutNow(book.getBookId(), book.type(), book.getHoldPlacedAt(), patron.getPatronId(), duration));
        }
        return announceFailure(bookCheckingOutFailedNow(withReason("book is not on hold by patron"), book.getBookId(), book.getHoldPlacedAt(), patron));
    }

    /**
     * Checks if the patron satisfies all holding policies for a specific book and duration.
     *
     * @param aBook       the book to hold
     * @param forDuration the duration of the hold
     * @return an {@link Option} containing a {@link Rejection} if a policy is violated, or empty if allowed
     */
    private Option<Rejection> patronCanHold(AvailableBook aBook, HoldDuration forDuration) {
        return placingOnHoldPolicies
                .toStream()
                .map(policy -> policy.apply(aBook, this, forDuration))
                .find(Either::isLeft)
                .map(Either::getLeft);
    }

    /**
     * Checks if the patron is a regular patron.
     *
     * @return {@code true} if regular, {@code false} otherwise
     */
    boolean isRegular() {
        return patron.isRegular();
    }

    /**
     * Counts the number of overdue checkouts at a specific library branch.
     *
     * @param libraryBranch the branch to check
     * @return the count of overdue checkouts
     */
    int overdueCheckoutsAt(LibraryBranchId libraryBranch) {
        return overdueCheckouts.countAt(libraryBranch);
    }

    /**
     * Gets the total number of active holds for this patron.
     *
     * @return the number of holds
     */
    public int numberOfHolds() {
        return patronHolds.count();
    }



}


