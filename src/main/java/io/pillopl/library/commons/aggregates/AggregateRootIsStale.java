package io.pillopl.library.commons.aggregates;

/**
 * Exception thrown when an optimistic locking failure occurs.
 * Indicates that the aggregate root has been modified by another transaction
 * since it was read.
 */
public class AggregateRootIsStale extends RuntimeException {

    /**
     * Constructs a new AggregateRootIsStale exception with the specified detail message.
     *
     * @param msg the detail message explaining the cause of the staleness
     */
    public AggregateRootIsStale(String msg) {
        super(msg);
    }
}
