package edu.neu.csye7374.core.command;

import edu.neu.csye7374.core.memento.DeviceMemento;

/**
 * Abstract base class for device commands with simple memento integration
 */
public abstract class AbstractCommand implements DeviceCommand {
    
    protected final DeviceReceiver receiver;
    protected final CommandType commandType;
    protected final String description;
    protected DeviceMemento stateBeforeExecution;
    
    public AbstractCommand(DeviceReceiver receiver, CommandType commandType, String description) {
        this.receiver = receiver;
        this.commandType = commandType;
        this.description = description;
    }
    
    @Override
    public String getDeviceId() {
        return receiver.getDeviceId();
    }
    
    @Override
    public CommandType getCommandType() {
        return commandType;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * Save the current state before executing the command
     */
    protected void saveStateBeforeExecution() {
        // Use the device's own memento functionality
        this.stateBeforeExecution = receiver.saveState();
    }
    
    /**
     * Restore the state from before command execution
     * @return True if restoration was successful
     */
    protected boolean restoreStateBeforeExecution() {
        if (stateBeforeExecution != null) {
            receiver.restoreState(stateBeforeExecution);
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s{device=%s, type=%s, description=%s}", 
                           getClass().getSimpleName(), receiver.getDeviceName(), commandType, description);
    }
}