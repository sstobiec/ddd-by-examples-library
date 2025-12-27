package io.pillopl.library.lending.patron.model;

import io.pillopl.library.lending.book.model.AvailableBook;
import io.vavr.Function3;
import io.vavr.collection.List;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.Value;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

/**
 * Domain policy interface for checking if a patron can place a book on hold.
 * <p>
 * It is a function that takes an {@link AvailableBook}, a {@link Patron}, and a {@link HoldDuration},
 * and returns either a {@link Rejection} (if the policy is violated) or an {@link Allowance}.
 */
interface PlacingOnHoldPolicy extends Function3<AvailableBook, Patron, HoldDuration, Either<Rejection, Allowance>> {

    /**
     * Policy ensuring that restricted books can only be held by researchers.
     */
    PlacingOnHoldPolicy onlyResearcherPatronsCanHoldRestrictedBooksPolicy = (AvailableBook toHold, Patron patron, HoldDuration holdDuration) -> {
        if (toHold.isRestricted() && patron.isRegular()) {
            return left(Rejection.withReason("Regular patrons cannot hold restricted books"));
        }
        return right(new Allowance());
    };

    /**
     * Policy preventing holds if the patron has too many overdue checkouts.
     */
    PlacingOnHoldPolicy overdueCheckoutsRejectionPolicy = (AvailableBook toHold, Patron patron, HoldDuration holdDuration) -> {
        if (patron.overdueCheckoutsAt(toHold.getLibraryBranch()) >= OverdueCheckouts.MAX_COUNT_OF_OVERDUE_RESOURCES) {
            return left(Rejection.withReason("cannot place on hold when there are overdue checkouts"));
        }
        return right(new Allowance());
    };

    /**
     * Policy limiting the maximum number of active holds for regular patrons.
     */
    PlacingOnHoldPolicy regularPatronMaximumNumberOfHoldsPolicy = (AvailableBook toHold, Patron patron, HoldDuration holdDuration) -> {
        if (patron.isRegular() && patron.numberOfHolds() >= PatronHolds.MAX_NUMBER_OF_HOLDS) {
            return left(Rejection.withReason("patron cannot hold more books"));
        }
        return right(new Allowance());
    };

    /**
     * Policy ensuring that open-ended holds (no specific end date) are reserved for researchers.
     */
    PlacingOnHoldPolicy onlyResearcherPatronsCanPlaceOpenEndedHolds = (AvailableBook toHold, Patron patron, HoldDuration holdDuration) -> {
        if (patron.isRegular() && holdDuration.isOpenEnded()) {
            return left(Rejection.withReason("regular patron cannot place open ended holds"));
        }
        return right(new Allowance());
    };

    /**
     * Retrieves all active policies for placing books on hold.
     *
     * @return a list of all {@link PlacingOnHoldPolicy} implementations
     */
    static List<PlacingOnHoldPolicy> allCurrentPolicies() {
        return List.of(
                onlyResearcherPatronsCanHoldRestrictedBooksPolicy,
                overdueCheckoutsRejectionPolicy,
                regularPatronMaximumNumberOfHoldsPolicy,
                onlyResearcherPatronsCanPlaceOpenEndedHolds);
    }

}

/**
 * Represents a successful check against a policy.
 */
@Value
class Allowance { }

/**
 * Represents a failure against a policy, containing the reason for rejection.
 */
@Value
class Rejection {

    @Value
    static class Reason {
        @NonNull
        String reason;
    }

    @NonNull
    Reason reason;

    static Rejection withReason(String reason) {
        return new Rejection(new Reason(reason));
    }
}

