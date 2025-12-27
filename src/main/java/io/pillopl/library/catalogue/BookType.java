package io.pillopl.library.catalogue;

/**
 * Defines the type of a book instance in the catalogue.
 * <p>
 * This classification determines how the book can be used within the library system (e.g., lending rules).
 * </p>
 */
public enum BookType {
    /**
     * Books that cannot be circulated/checked out (e.g., reference books, rare items).
     */
    Restricted,

    /**
     * Books that are available for standard circulation/checkout.
     */
    Circulating
}

