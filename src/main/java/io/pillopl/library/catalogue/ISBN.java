package io.pillopl.library.catalogue;

import lombok.NonNull;
import lombok.Value;

/**
 * Represents an International Standard Book Number (ISBN).
 * <p>
 * This class encapsulates the ISBN value and provides basic validation.
 * It ensures that the ISBN matches a simplified format.
 * </p>
 */
@Value
class ISBN {

    private static final String VERY_SIMPLE_ISBN_CHECK = "^\\d{9}[\\d|X]$";

    /**
     * The string representation of the ISBN.
     */
    @NonNull
    String isbn;

    /**
     * Constructs a new ISBN instance.
     *
     * @param isbn the raw ISBN string. Must not be null.
     * @throws IllegalArgumentException if the ISBN format is invalid (does not match 9 digits followed by digit or 'X').
     */
    ISBN(String isbn) {
        if (!isbn.trim().matches(VERY_SIMPLE_ISBN_CHECK)) {
            throw new IllegalArgumentException("Wrong ISBN!");
        }
        this.isbn = isbn.trim();

    }
}
