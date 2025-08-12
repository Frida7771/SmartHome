package edu.neu.csye7374.core.strategy;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.devices.concrete.Light;

/**
 * Specialized operation strategy for Light devices
 * Implements light-specific operations and behaviors
 */
public class LightOperationStrategy implements DeviceOperationStrategy {
    
    @Override
    public String execute(IDevice device, Operation operation) {
        if (!(device instanceof Light)) {
            return "Error: Device is not a Light";
        }
        
        Light light = (Light) device;
        
        switch (operation) {
            case TURN_ON:
                light.turnOn();
                return light.getName() + " turned ON with " + light.getBrightness() + "% brightness";
                
            case TURN_OFF:
                light.turnOff();
                return light.getName() + " turned OFF";
                
            case TOGGLE:
                light.toggle();
                return light.getName() + " toggled to " + (light.isOn() ? "ON" : "OFF");
                
            case GET_STATUS:
                return light.getName() + " is " + (light.isOn() ? "ON" : "OFF") + 
                       " (Brightness: " + light.getBrightness() + "%, Color: " + light.getColor() + ")";
                
            case DIM:
                int currentBrightness = light.getBrightness();
                int newBrightness = Math.max(0, currentBrightness - 25);
                light.setBrightness(newBrightness);
                return light.getName() + " dimmed to " + newBrightness + "%";
                
            case BRIGHTEN:
                currentBrightness = light.getBrightness();
                newBrightness = Math.min(100, currentBrightness + 25);
                light.setBrightness(newBrightness);
                return light.getName() + " brightened to " + newBrightness + "%";
                
            default:
                return "Unsupported operation for Light: " + operation;
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Light Operation Strategy";
    }
    
    @Override
    public String getStrategyDescription() {
        return "Handles light-specific operations such as turning on/off, dimming, and brightening";
    }
}