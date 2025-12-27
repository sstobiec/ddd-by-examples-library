package io.pillopl.library.lending.dailysheet.model;

import io.pillopl.library.lending.patron.model.PatronEvent.*;

/**
 * Interface for the Daily Sheet read model.
 * <p>
 * This component is responsible for tracking daily operational data, such as checkouts that need
 * to be marked as overdue and holds that are expiring. It updates its state based on domain events.
 */
public interface DailySheet {

    /**
     * Queries for checkouts that have exceeded their loan period and should be marked as overdue.
     *
     * @return a {@link CheckoutsToOverdueSheet} containing the list of potential overdue checkouts
     */
    CheckoutsToOverdueSheet queryForCheckoutsToOverdue();

    /**
     * Queries for holds that have expired and should be removed.
     *
     * @return a {@link HoldsToExpireSheet} containing the list of expiring holds
     */
    HoldsToExpireSheet queryForHoldsToExpireSheet();

    /**
     * Updates the daily sheet when a book is placed on hold.
     *
     * @param event the event details
     */
    void handle(BookPlacedOnHold event);

    /**
     * Updates the daily sheet when a hold is canceled.
     *
     * @param event the event details
     */
    void handle(BookHoldCanceled event);

    /**
     * Updates the daily sheet when a hold expires.
     *
     * @param event the event details
     */
    void handle(BookHoldExpired event);

    /**
     * Updates the daily sheet when a book is checked out.
     *
     * @param event the event details
     */
    void handle(BookCheckedOut event);

    /**
     * Updates the daily sheet when a book is returned.
     *
     * @param event the event details
     */
    void handle(BookReturned event);


}
