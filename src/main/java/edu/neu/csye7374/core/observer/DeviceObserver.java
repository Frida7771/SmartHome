package edu.neu.csye7374.core.observer;

import edu.neu.csye7374.core.device.IDevice;

/**
 * Observer interface for device state changes
 * Enhanced to support different types of device events
 */
public interface DeviceObserver {
    
    /**
     * Called when a device's state changes
     * @param device The device that changed
     * @param oldState The previous state
     * @param newState The new state
     */
    void onDeviceStateChanged(IDevice device, String oldState, String newState);
    
    /**
     * Called when a device is turned on
     * @param device The device that was turned on
     */
    default void onDeviceTurnedOn(IDevice device) {
    }
    
    /**
     * Called when a device is turned off
     * @param device The device that was turned off
     */
    default void onDeviceTurnedOff(IDevice device) {
    }
    
    /**
     * Called when a device property changes (brightness, temperature, etc.)
     * @param device The device that changed
     * @param propertyName The name of the property that changed
     * @param oldValue The previous value
     * @param newValue The new value
     */
    default void onDevicePropertyChanged(IDevice device, String propertyName, Object oldValue, Object newValue) {
    }
    
    /**
     * Get the observer's name/identifier
     * @return Observer name
     */
    String getObserverName();
}
