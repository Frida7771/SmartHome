package edu.neu.csye7374.core.command;

/**
 * Interface for executing commands with undo/redo support.
 * This allows other classes to execute commands without directly accessing the CommandManager.
 */
public interface CommandExecutor {
    /**
     * Execute a command and record it for undo/redo.
     * @param command The command to execute
     */
    void executeCommand(DeviceCommand command);
}

