package edu.neu.csye7374.core.command;

import edu.neu.csye7374.core.memento.DeviceMemento;

/**
 * Basic device commands (turn on, turn off, toggle)
 */
public class BasicCommands {
    
    public static class TurnOnCommand implements DeviceCommand {
        private final DeviceReceiver receiver;
        private final CommandType commandType;
        private final String description;
        private DeviceMemento stateBeforeExecution;
        
        public TurnOnCommand(DeviceReceiver receiver) {
            this.receiver = receiver;
            this.commandType = CommandType.TURN_ON;
            this.description = "Turn on " + receiver.getDeviceName();
        }
        
        @Override
        public String execute() {
            // Save state before execution for undo
            saveStateBeforeExecution();
            
            // Execute the command
            receiver.turnOn();
            return receiver.getDeviceName() + " turned ON";
        }
        
        @Override
        public String undo() {
            if (stateBeforeExecution != null) {
                // Restore the previous state
                restoreState(stateBeforeExecution);
                return "Undid turn on for " + receiver.getDeviceName();
            }
            return "Cannot undo turn on for " + receiver.getDeviceName() + " - no previous state";
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        @Override
        public String getDeviceId() {
            return receiver.getDeviceId();
        }
        
        @Override
        public CommandType getCommandType() {
            return commandType;
        }
        
        private void saveStateBeforeExecution() {
            // This would need to be implemented based on your memento system
            // For now, we'll create a simple state representation
            stateBeforeExecution = new DeviceMemento(
                receiver.getDeviceId(),
                receiver.getDeviceName(),
                edu.neu.csye7374.core.factory.DeviceType.valueOf(receiver.getDeviceType()),
                receiver.isOn(),
                receiver.getCurrentState()
            );
        }
        
        private void restoreState(DeviceMemento memento) {
            // This would need to be implemented based on your memento system
            // For now, we'll just restore the basic on/off state
            if (!memento.isOn()) {
                receiver.turnOff();
            }
        }
    }
    
    public static class TurnOffCommand implements DeviceCommand {
        private final DeviceReceiver receiver;
        private final CommandType commandType;
        private final String description;
        private DeviceMemento stateBeforeExecution;
        
        public TurnOffCommand(DeviceReceiver receiver) {
            this.receiver = receiver;
            this.commandType = CommandType.TURN_OFF;
            this.description = "Turn off " + receiver.getDeviceName();
        }
        
        @Override
        public String execute() {
            saveStateBeforeExecution();
            receiver.turnOff();
            return receiver.getDeviceName() + " turned OFF";
        }
        
        @Override
        public String undo() {
            if (stateBeforeExecution != null) {
                restoreState(stateBeforeExecution);
                return "Undid turn off for " + receiver.getDeviceName();
            }
            return "Cannot undo turn off for " + receiver.getDeviceName() + " - no previous state";
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        @Override
        public String getDeviceId() {
            return receiver.getDeviceId();
        }
        
        @Override
        public CommandType getCommandType() {
            return commandType;
        }
        
        private void saveStateBeforeExecution() {
            stateBeforeExecution = new DeviceMemento(
                receiver.getDeviceId(),
                receiver.getDeviceName(),
                edu.neu.csye7374.core.factory.DeviceType.valueOf(receiver.getDeviceType()),
                receiver.isOn(),
                receiver.getCurrentState()
            );
        }
        
        private void restoreState(DeviceMemento memento) {
            if (memento.isOn()) {
                receiver.turnOn();
            }
        }
    }
    
    public static class ToggleCommand implements DeviceCommand {
        private final DeviceReceiver receiver;
        private final CommandType commandType;
        private final String description;
        private DeviceMemento stateBeforeExecution;
        
        public ToggleCommand(DeviceReceiver receiver) {
            this.receiver = receiver;
            this.commandType = CommandType.TOGGLE;
            this.description = "Toggle " + receiver.getDeviceName();
        }
        
        @Override
        public String execute() {
            saveStateBeforeExecution();
            receiver.toggle();
            return receiver.getDeviceName() + " toggled to " + (receiver.isOn() ? "ON" : "OFF");
        }
        
        @Override
        public String undo() {
            if (stateBeforeExecution != null) {
                restoreState(stateBeforeExecution);
                return "Undid toggle for " + receiver.getDeviceName();
            }
            return "Cannot undo toggle for " + receiver.getDeviceName() + " - no previous state";
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        @Override
        public String getDeviceId() {
            return receiver.getDeviceId();
        }
        
        @Override
        public CommandType getCommandType() {
            return commandType;
        }
        
        private void saveStateBeforeExecution() {
            stateBeforeExecution = new DeviceMemento(
                receiver.getDeviceId(),
                receiver.getDeviceName(),
                edu.neu.csye7374.core.factory.DeviceType.valueOf(receiver.getDeviceType()),
                receiver.isOn(),
                receiver.getCurrentState()
            );
        }
        
        private void restoreState(DeviceMemento memento) {
            if (memento.isOn()) {
                receiver.turnOn();
            } else {
                receiver.turnOff();
            }
        }
    }
}