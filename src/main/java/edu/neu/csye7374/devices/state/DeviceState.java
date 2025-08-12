
package edu.neu.csye7374.devices.state;

import edu.neu.csye7374.core.device.IDevice;

/**
 * State interface for the State pattern
 * Defines behavior for different device states
 */
public interface DeviceState {
    
    /**
     * Turn the device on
     * @param device The device to operate on
     */
    void turnOn(IDevice device);
    
    /**
     * Turn the device off
     * @param device The device to operate on
     */
    void turnOff(IDevice device);
    
    /**
     * Toggle the device state
     * @param device The device to operate on
     */
    void toggle(IDevice device);
    
    /**
     * Get the state name
     * @return State name
     */
    String getStateName();
    
    /**
     * Get the state descriptions
     * @return State descriptions
     */
    String getStateDescription();
} 
