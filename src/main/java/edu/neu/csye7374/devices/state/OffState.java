package edu.neu.csye7374.devices.state;

import edu.neu.csye7374.core.device.IDevice;

/**
 * Off state implementation for device
 */
public class OffState implements DeviceState {
    
    @Override
    public void turnOn(IDevice device) {
        System.out.println(device.getName() + " turning ON");
        device.setState("ON");
    }
    
    @Override
    public void turnOff(IDevice device) {
        System.out.println(device.getName() + " is already OFF");
    }
    
    @Override
    public void toggle(IDevice device) {
        turnOn(device);
    }
    
    @Override
    public String getStateName() {
        return "OFF";
    }
    
    @Override
    public String getStateDescription() {
        return "Device is powered off and inactive";
    }
} 
