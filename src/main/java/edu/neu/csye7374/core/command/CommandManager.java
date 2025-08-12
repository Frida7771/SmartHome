package edu.neu.csye7374.core.command;

import java.util.*;

/**
 * Concrete invoker implementation
 * Manages command execution, history, and undo/redo functionality
 */
public class CommandManager implements CommandInvoker {
    
    private final Stack<DeviceCommand> commandHistory;
    private final Stack<DeviceCommand> redoStack;
    private final Map<String, List<DeviceCommand>> deviceCommandHistory;
    
    public CommandManager() {
        this.commandHistory = new Stack<>();
        this.redoStack = new Stack<>();
        this.deviceCommandHistory = new HashMap<>();
    }
    
    @Override
    public String executeCommand(DeviceCommand command) {
        if (command == null) {
            return "Error: Cannot execute null command";
        }
        
        try {
            // Execute the command
            String result = command.execute();
            
            // Add to command history
            commandHistory.push(command);
            
            // Add to device-specific history
            deviceCommandHistory.computeIfAbsent(command.getDeviceId(), k -> new ArrayList<>())
                               .add(command);
            
            // Clear redo stack since we're executing a new command
            redoStack.clear();
            
            System.out.println("Command executed: " + command.getDescription());
            return result;
            
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }
    
    @Override
    public String undo() {
        if (commandHistory.isEmpty()) {
            return "No commands to undo";
        }
        
        try {
            DeviceCommand command = commandHistory.pop();
            String result = command.undo();
            
            // Add to redo stack
            redoStack.push(command);
            
            System.out.println("Command undone: " + command.getDescription());
            return result;
            
        } catch (Exception e) {
            return "Error undoing command: " + e.getMessage();
        }
    }
    
    @Override
    public String redo() {
        if (redoStack.isEmpty()) {
            return "No commands to redo";
        }
        
        try {
            DeviceCommand command = redoStack.pop();
            String result = command.execute();
            
            // Add back to command history
            commandHistory.push(command);
            
            System.out.println("Command redone: " + command.getDescription());
            return result;
            
        } catch (Exception e) {
            return "Error redoing command: " + e.getMessage();
        }
    }
    
    @Override
    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }
    
    @Override
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    @Override
    public List<DeviceCommand> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }
    
    @Override
    public void clearHistory() {
        commandHistory.clear();
        redoStack.clear();
        deviceCommandHistory.clear();
        System.out.println("Command history cleared");
    }
    
    /**
     * Get command history for a specific device
     * @param deviceId The device ID
     * @return List of commands for the device
     */
    public List<DeviceCommand> getDeviceCommandHistory(String deviceId) {
        return new ArrayList<>(deviceCommandHistory.getOrDefault(deviceId, new ArrayList<>()));
    }
    
    /**
     * Get command statistics
     * @return Map of command type to count
     */
    public Map<CommandType, Integer> getCommandStatistics() {
        Map<CommandType, Integer> stats = new HashMap<>();
        for (DeviceCommand command : commandHistory) {
            CommandType type = command.getCommandType();
            stats.put(type, stats.getOrDefault(type, 0) + 1);
        }
        return stats;
    }
    
    /**
     * Get the number of commands in history
     * @return Number of commands
     */
    public int getCommandCount() {
        return commandHistory.size();
    }
    
    /**
     * Get the number of commands available for redo
     * @return Number of redo commands
     */
    public int getRedoCount() {
        return redoStack.size();
    }
}