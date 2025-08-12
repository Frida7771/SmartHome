package edu.neu.csye7374.devices.state;

import edu.neu.csye7374.core.device.IDevice;

/**
 * On state implementation for devices
 */
public class OnState implements DeviceState {
    
    @Override
    public void turnOn(IDevice device) {
        System.out.println(device.getName() + " is already ON");
    }
    
    @Override
    public void turnOff(IDevice device) {
        System.out.println(device.getName() + " turning OFF");
        device.setState("OFF");
    }
    
    @Override
    public void toggle(IDevice device) {
        turnOff(device);
    }
    
    @Override
    public String getStateName() {
        return "ON";
    }
    
    @Override
    public String getStateDescription() {
        return "Device is powered on and operational";
    }
} 
