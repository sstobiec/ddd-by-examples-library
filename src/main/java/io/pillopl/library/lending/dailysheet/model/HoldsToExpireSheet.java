package io.pillopl.library.lending.dailysheet.model;

import io.pillopl.library.lending.patron.model.PatronEvent;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import lombok.NonNull;
import lombok.Value;
import org.springframework.context.event.EventListener;

/**
 * Value object representing a collection of holds that are due to expire.
 * <p>
 * This sheet is generated daily to identify and process holds that are no longer valid.
 */
@Value
public class HoldsToExpireSheet {

    /**
     * The list of expired holds.
     */
    @NonNull
    List<ExpiredHold> expiredHolds;

    /**
     * Converts the sheet content into a stream of {@link PatronEvent.BookHoldExpired} events.
     *
     * @return a stream of events ready to be published
     */
    @EventListener
    public Stream<PatronEvent.BookHoldExpired> toStreamOfEvents() {
        return expiredHolds
                .toStream()
                .map(ExpiredHold::toEvent);
    }

    /**
     * Returns the number of expired holds in this sheet.
     *
     * @return the count
     */
    public int count() {
        return expiredHolds.size();
    }

}
