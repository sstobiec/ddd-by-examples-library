package io.pillopl.library.commons.commands;

/**
 * Represents the result of a batch command execution.
 */
public enum BatchResult {
    /**
     * Indicates that all commands in the batch were executed successfully.
     */
    FullSuccess,
    
    /**
     * Indicates that some commands in the batch failed.
     */
    SomeFailed
}