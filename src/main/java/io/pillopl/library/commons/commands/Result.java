package io.pillopl.library.commons.commands;

/**
 * Represents the result of a single command execution.
 */
public enum Result {
    /**
     * Indicates that the command was executed successfully.
     */
    Success,
    
    /**
     * Indicates that the command was rejected due to domain rules or validation errors.
     */
    Rejection
}