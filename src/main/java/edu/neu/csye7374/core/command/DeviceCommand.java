package edu.neu.csye7374.core.command;

/**
 * Command interface for the Command pattern
 * Defines the contract for all device commands
 */
public interface DeviceCommand {
    
    /**
     * Execute the command
     * @return Result message of the command execution
     */
    String execute();
    
    /**
     * Undo the command
     * @return Result message of the undo operation
     */
    String undo();
    
    /**
     * Get the command description
     * @return Description of what the command does
     */
    String getDescription();
    
    /**
     * Get the device ID this command operates on
     * @return Device ID
     */
    String getDeviceId();
    
    /**
     * Get the command type
     * @return Type of command
     */
    CommandType getCommandType();
}