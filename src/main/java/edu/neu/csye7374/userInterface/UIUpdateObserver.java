package edu.neu.csye7374.userInterface;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.core.observer.DeviceObserver;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Observer for updating the smart home UI
 * Provides real-time updates to the user interface
 */
public class UIUpdateObserver implements DeviceObserver {
    
    private final String observerName;
    private final List<UIUpdate> uiUpdates;
    private Consumer<UIUpdate> uiUpdateCallback;
    public UIUpdateObserver(String observerName) {
        this.observerName = observerName;
        this.uiUpdates = new ArrayList<>();
    }
    
    public void setUIUpdateCallback(Consumer<UIUpdate> callback) {
        this.uiUpdateCallback = callback;
    }
    
    @Override
    public void onDeviceStateChanged(IDevice device, String oldState, String newState) {
        UIUpdate update = new UIUpdate(
            device.getId(),
            device.getName(),
            "STATE_CHANGE",
            oldState,
            newState,
            LocalDateTime.now()
        );
        
        uiUpdates.add(update);
        System.out.println("UI UPDATE: " + update);
        
        // In a real UI, this would trigger UI component updates
        if (uiUpdateCallback != null) {
            uiUpdateCallback.accept(update);
        }   
    }
    
    @Override
    public void onDeviceTurnedOn(IDevice device) {
        UIUpdate update = new UIUpdate(
            device.getId(),
            device.getName(),
            "DEVICE_ON",
            "OFF",
            "ON",
            LocalDateTime.now()
        );
        
        uiUpdates.add(update);
        System.out.println("UI UPDATE: " + update);
        if (uiUpdateCallback != null) {
            uiUpdateCallback.accept(update);
        }   
    }
    
    @Override
    public void onDeviceTurnedOff(IDevice device) {
        UIUpdate update = new UIUpdate(
            device.getId(),
            device.getName(),
            "DEVICE_OFF",
            "ON",
            "OFF",
            LocalDateTime.now()
        );
        
        uiUpdates.add(update);
        System.out.println("UI UPDATE: " + update);
        if (uiUpdateCallback != null) {
            uiUpdateCallback.accept(update);
        }   
    }
    
    @Override
    public void onDevicePropertyChanged(IDevice device, String propertyName, Object oldValue, Object newValue) {
        UIUpdate update = new UIUpdate(
            device.getId(),
            device.getName(),
            "PROPERTY_CHANGE",
            oldValue.toString(),
            newValue.toString(),
            LocalDateTime.now(),
            propertyName
        );
        
        uiUpdates.add(update);
        System.out.println("UI UPDATE: " + update);
        if (uiUpdateCallback != null) {
            uiUpdateCallback.accept(update);
        }   
    }
    
    @Override
    public String getObserverName() {
        return observerName;
    }
    
    /**
     * Simulate updating a UI component
     * In a real implementation, this would update actual UI elements
     */
    private void updateUIComponent(UIUpdate update) {
        switch (update.getUpdateType()) {
            case "STATE_CHANGE":
                // Update device status indicator
                System.out.println("  → Updating status indicator for " + update.getDeviceName());
                break;
            case "DEVICE_ON":
                // Update device icon to "on" state
                System.out.println("  → Updating device icon to ON state for " + update.getDeviceName());
                break;
            case "DEVICE_OFF":
                // Update device icon to "off" state
                System.out.println("  → Updating device icon to OFF state for " + update.getDeviceName());
                break;
            case "PROPERTY_CHANGE":
                // Update specific property display
                System.out.println("  → Updating " + update.getPropertyName() + " display for " + update.getDeviceName());
                break;
        }
        if (uiUpdateCallback != null) {
            uiUpdateCallback.accept(update);
        }   
    }
    
    /**
     * Get all UI updates
     * @return List of all UI updates
     */
    public List<UIUpdate> getUIUpdates() {
        return new ArrayList<>(uiUpdates);
    }
    
    /**
     * Clear UI updates
     */
    public void clearUpdates() {
        uiUpdates.clear();
    }
    
    /**
     * Inner class to represent a UI update
     */
    public static class UIUpdate {
        private final String deviceId;
        private final String deviceName;
        private final String updateType;
        private final String oldValue;
        private final String newValue;
        private final LocalDateTime timestamp;
        private final String propertyName;
        
        public UIUpdate(String deviceId, String deviceName, String updateType, 
                       String oldValue, String newValue, LocalDateTime timestamp) {
            this(deviceId, deviceName, updateType, oldValue, newValue, timestamp, null);
        }
        
        public UIUpdate(String deviceId, String deviceName, String updateType, 
                       String oldValue, String newValue, LocalDateTime timestamp, String propertyName) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.updateType = updateType;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.timestamp = timestamp;
            this.propertyName = propertyName;
        }
        
        // Getters
        public String getDeviceId() { return deviceId; }
        public String getDeviceName() { return deviceName; }
        public String getUpdateType() { return updateType; }
        public String getOldValue() { return oldValue; }
        public String getNewValue() { return newValue; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getPropertyName() { return propertyName; }
        
        @Override
        public String toString() {
            String base = String.format("%s: %s → %s at %s", deviceName, oldValue, newValue, timestamp);
            return propertyName != null ? base + " (Property: " + propertyName + ")" : base;
        }
    }
}   

