package io.pillopl.library.lending.patron.model;

import lombok.NonNull;
import lombok.Value;

import static io.pillopl.library.lending.patron.model.PatronType.Regular;

/**
 * Value object containing static information about a Patron.
 * <p>
 * This includes the patron's identity and type (e.g., Regular or Researcher).
 */
@Value
class PatronInformation {

    /**
     * The unique identifier of the patron.
     */
    @NonNull PatronId patronId;

    /**
     * The type of the patron.
     */
    @NonNull PatronType type;

    /**
     * Checks if the patron is of type 'Regular'.
     *
     * @return {@code true} if the patron is regular, {@code false} otherwise
     */
    boolean isRegular() {
        return type.equals(Regular);
    }
}

