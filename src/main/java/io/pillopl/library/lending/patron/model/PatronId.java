package io.pillopl.library.lending.patron.model;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

/**
 * Value object representing the unique identifier of a Patron.
 */
@Value
public class PatronId {
    
    /**
     * The unique UUID of the patron.
     */
    @NonNull UUID patronId;
}
