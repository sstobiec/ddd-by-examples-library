package io.pillopl.library.commons.events;

import io.vavr.control.Either;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

/**
 * Utility class for creating Either results, commonly used to represent success or failure.
 * Wraps Vavr's Either implementation for cleaner API usage in the domain layer.
 */
public class EitherResult {

    /**
     * Creates a failure result (Left side of Either).
     *
     * @param <L>  the type of the failure value
     * @param <R>  the type of the success value
     * @param left the failure value
     * @return an Either containing the failure
     */
    public static <L, R> Either<L, R> announceFailure(L left) {
        return left(left);
    }

    /**
     * Creates a success result (Right side of Either).
     *
     * @param <L>   the type of the failure value
     * @param <R>   the type of the success value
     * @param right the success value
     * @return an Either containing the success value
     */
    public static <L, R> Either<L, R> announceSuccess(R right) {
        return right(right);
    }
}
