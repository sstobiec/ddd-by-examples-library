package io.pillopl.library.lending.librarybranch.model;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

/**
 * Value object representing the unique identifier of a Library Branch.
 */
@Value
public class LibraryBranchId {

    /**
     * The unique UUID of the library branch.
     */
    @NonNull UUID libraryBranchId;
}
