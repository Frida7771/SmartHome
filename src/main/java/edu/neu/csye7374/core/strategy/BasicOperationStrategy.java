package edu.neu.csye7374.core.strategy;

import edu.neu.csye7374.core.device.IDevice;

/**
 * Basic operation strategy for devices
 * Implements common device operations
 */
public class BasicOperationStrategy implements DeviceOperationStrategy {
    
    @Override
    public String execute(IDevice device, Operation operation) {
        switch (operation) {
            case TURN_ON:
                device.turnOn();
                return device.getName() + " turned ON";
            case TURN_OFF:
                device.turnOff();
                return device.getName() + " turned OFF";
            case TOGGLE:
                device.toggle();
                return device.getName() + " toggled";
            case GET_STATUS:
                return device.getName() + " is " + (device.isOn() ? "ON" : "OFF");
            default:
                return "Unknown operation: " + operation;
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Basic Operation Strategy";
    }
    
    @Override
    public String getStrategyDescription() {
        return "Handles basic device operations like turn on, turn off, toggle, and status check";
    }
} 
