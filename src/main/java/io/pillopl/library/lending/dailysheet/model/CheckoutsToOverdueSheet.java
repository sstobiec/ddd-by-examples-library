package io.pillopl.library.lending.dailysheet.model;

import io.pillopl.library.lending.patron.model.PatronEvent.OverdueCheckoutRegistered;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import lombok.NonNull;
import lombok.Value;

/**
 * Value object representing a collection of checkouts that are identified as overdue.
 * <p>
 * This sheet is generated daily to process and register overdue checkouts in the system.
 */
@Value
public class CheckoutsToOverdueSheet {

    /**
     * The list of overdue checkouts.
     */
    @NonNull
    List<OverdueCheckout> checkouts;

    /**
     * Converts the sheet content into a stream of {@link OverdueCheckoutRegistered} events.
     *
     * @return a stream of events ready to be published
     */
    public Stream<OverdueCheckoutRegistered> toStreamOfEvents() {
        return checkouts.toStream()
                .map(OverdueCheckout::toEvent);
    }

    /**
     * Returns the number of overdue checkouts in this sheet.
     *
     * @return the count
     */
    public int count() {
        return checkouts.size();
    }

}
