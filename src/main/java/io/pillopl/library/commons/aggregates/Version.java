package io.pillopl.library.commons.aggregates;

import lombok.Value;

/**
 * Represents the version of an aggregate root for optimistic locking.
 * Ensures data consistency during concurrent modifications.
 */
@Value
public class Version {
    
    /**
     * The version number. Incremented on each modification.
     */
    int version;

    /**
     * Creates an initial version (0).
     *
     * @return a new Version instance with version number 0
     */
    public static Version zero() {
        return new Version(0);
    }
}
