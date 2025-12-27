package io.pillopl.library.lending.patron.model;

import io.pillopl.library.catalogue.BookId;
import io.pillopl.library.lending.librarybranch.model.LibraryBranchId;
import lombok.NonNull;
import lombok.Value;

/**
 * Internal value object representing a hold placed by a patron on a book.
 * <p>
 * This class links a specific book to the library branch where it is held.
 * It is used within the {@link Patron} aggregate to track active holds.
 */
@Value
class Hold {

    /**
     * The ID of the book on hold.
     */
    @NonNull BookId bookId;

    /**
     * The ID of the library branch where the book is held.
     */
    @NonNull LibraryBranchId libraryBranchId;

}
