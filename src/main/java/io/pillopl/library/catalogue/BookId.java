package io.pillopl.library.catalogue;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

/**
 * Unique identifier for a specific physical book instance.
 * <p>
 * This value object wraps a UUID to strongly type the book ID, preventing confusion with other IDs (like ISBN).
 * </p>
 */
@Value
public class BookId {

    /**
     * The unique UUID of the book instance.
     */
    @NonNull
    UUID bookId;
}
