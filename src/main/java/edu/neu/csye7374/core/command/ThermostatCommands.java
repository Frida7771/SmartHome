package edu.neu.csye7374.core.command;

/**
 * Thermostat-specific commands
 */
public class ThermostatCommands {
    
    public static class SetTargetTemperatureCommand implements DeviceCommand {
        private final ThermostatReceiver receiver;
        private final double temperature;
        private final CommandType commandType;
        private final String description;
        private double previousTemperature;
        
        public SetTargetTemperatureCommand(ThermostatReceiver receiver, double temperature) {
            this.receiver = receiver;
            this.temperature = temperature;
            this.commandType = CommandType.SET_TARGET_TEMPERATURE;
            this.description = "Set target temperature of " + receiver.getDeviceName() + " to " + temperature + "°F";
        }
        
        @Override
        public String execute() {
            this.previousTemperature = receiver.getTargetTemperature();
            receiver.setTargetTemperature(temperature);
            return receiver.getDeviceName() + " target temperature set to " + temperature + "°F";
        }
        
        @Override
        public String undo() {
            receiver.setTargetTemperature(previousTemperature);
            return receiver.getDeviceName() + " target temperature restored to " + previousTemperature + "°F";
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
    }
    
    public static class SetModeCommand implements DeviceCommand {
        private final ThermostatReceiver receiver;
        private final String mode;
        private final CommandType commandType;
        private final String description;
        private String previousMode;
        
        public SetModeCommand(ThermostatReceiver receiver, String mode) {
            this.receiver = receiver;
            this.mode = mode;
            this.commandType = CommandType.SET_MODE;
            this.description = "Set mode of " + receiver.getDeviceName() + " to " + mode;
        }
        
        @Override
        public String execute() {
            this.previousMode = receiver.getMode();
            receiver.setMode(mode);
            return receiver.getDeviceName() + " mode set to " + mode;
        }
        
        @Override
        public String undo() {
            receiver.setMode(previousMode);
            return receiver.getDeviceName() + " mode restored to " + previousMode;
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
    }
}