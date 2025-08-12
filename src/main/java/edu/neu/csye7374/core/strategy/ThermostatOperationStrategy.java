package edu.neu.csye7374.core.strategy;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.devices.concrete.Thermostat;
import edu.neu.csye7374.devices.concrete.ThermostatMode;

/**
 * Specialized operation strategy for Thermostat devices
 * Implements thermostat-specific operations and behaviors
 */
public class ThermostatOperationStrategy implements DeviceOperationStrategy {
    
    @Override
    public String execute(IDevice device, Operation operation) {
        if (!(device instanceof Thermostat)) {
            return "Error: Device is not a Thermostat";
        }
        
        Thermostat thermostat = (Thermostat) device;
        
        switch (operation) {
            case TURN_ON:
                thermostat.turnOn();
                return thermostat.getName() + " turned ON (Target: " + thermostat.getTargetTemperature() + "°F)";
                
            case TURN_OFF:
                thermostat.turnOff();
                return thermostat.getName() + " turned OFF";
                
            case TOGGLE:
                thermostat.toggle();
                return thermostat.getName() + " toggled to " + (thermostat.isOn() ? "ON" : "OFF");
                
            case GET_STATUS:
                return thermostat.getName() + " is " + (thermostat.isOn() ? "ON" : "OFF") + 
                       " (Current: " + thermostat.getCurrentTemperature() + "°F, Target: " + 
                       thermostat.getTargetTemperature() + "°F, Mode: " + thermostat.getMode() + ")";
                
            case SET_HEAT_MODE:
                thermostat.setMode(ThermostatMode.HEAT);
                return thermostat.getName() + " mode set to " + ThermostatMode.HEAT;
                
            case SET_COOL_MODE:
                thermostat.setMode(ThermostatMode.COOL);
                return thermostat.getName() + " mode set to " + ThermostatMode.COOL;
                
            default:
                return "Unsupported operation for Thermostat: " + operation;
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Thermostat Operation Strategy";
    }
    
    @Override
    public String getStrategyDescription() {
        return "Handles thermostat-specific operations including on/off control and mode management";
    }
}