package edu.neu.csye7374.core.observer;

import edu.neu.csye7374.core.device.IDevice;

/**
 * Concrete observer implementation for device state changes
 */
public class DeviceStateObserver implements DeviceObserver {
    
    private final String observerName;
    
    public DeviceStateObserver(String observerName) {
        this.observerName = observerName;
    }
    
    @Override
    public void onDeviceStateChanged(IDevice device, String oldState, String newState) {
        System.out.println("[" + observerName + "] Device state change detected:");
        System.out.println("  Device: " + device.getName() + " (ID: " + device.getId() + ")");
        System.out.println("  State: " + oldState + " -> " + newState);
        System.out.println("  Time: " + java.time.LocalDateTime.now());
        System.out.println();
    }
    
    public String getObserverName() {
        return observerName;
    }
    
    @Override
    public String toString() {
        return "DeviceStateObserver{name=" + observerName + "}";
    }
} 
