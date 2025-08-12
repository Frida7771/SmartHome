package edu.neu.csye7374.core.command;

/**
 * Invoker interface for the Command pattern
 * Defines the contract for command execution and management
 */
public interface CommandInvoker {
    
    /**
     * Execute a command
     * @param command The command to execute
     * @return Result of command execution
     */
    String executeCommand(DeviceCommand command);
    
    /**
     * Undo the last command
     * @return Result of undo operation
     */
    String undo();
    
    /**
     * Redo the last undone command
     * @return Result of redo operation
     */
    String redo();
    
    /**
     * Check if undo is available
     * @return True if undo is available
     */
    boolean canUndo();
    
    /**
     * Check if redo is available
     * @return True if redo is available
     */
    boolean canRedo();
    
    /**
     * Get command history
     * @return List of executed commands
     */
    java.util.List<DeviceCommand> getCommandHistory();
    
    /**
     * Clear command history
     */
    void clearHistory();
}