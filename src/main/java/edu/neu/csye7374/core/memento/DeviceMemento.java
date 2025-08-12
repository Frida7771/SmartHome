package edu.neu.csye7374.core.memento;

import edu.neu.csye7374.core.factory.DeviceType;
import edu.neu.csye7374.devices.concrete.LightColor;
import edu.neu.csye7374.devices.concrete.ThermostatMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Memento class for storing device state
 * Implements the Memento pattern to capture and restore device state
 */
public class DeviceMemento {
    
    // Basic device properties
    private final String deviceId;
    private final String deviceName;
    private final DeviceType deviceType;
    private final boolean isOn;
    private final String state;
    private final long timestamp;
    
    // Device-specific state storage
    private final Map<String, Object> deviceSpecificState;
    
    public DeviceMemento(String deviceId, String deviceName, DeviceType deviceType,
                        boolean isOn, String state) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.isOn = isOn;
        this.state = state;
        this.timestamp = System.currentTimeMillis();
        this.deviceSpecificState = new HashMap<>();
    }
    
    // Getters for basic properties
    public String getDeviceId() { return deviceId; }
    public String getDeviceName() { return deviceName; }
    public DeviceType getDeviceType() { return deviceType; }
    public boolean isOn() { return isOn; }
    public String getState() { return state; }
    public long getTimestamp() { return timestamp; }
    
    // Methods for device-specific state
    public void setDeviceSpecificState(String key, Object value) {
        deviceSpecificState.put(key, value);
    }
    
    /**
     * Set device-specific state for integer values
     * @param key The state key
     * @param value The integer value
     */
    public void setDeviceSpecificState(String key, int value) {
        deviceSpecificState.put(key, value);
    }
    
    /**
     * Set device-specific state for double values
     * @param key The state key
     * @param value The double value
     */
    public void setDeviceSpecificState(String key, double value) {
        deviceSpecificState.put(key, value);
    }
    
    public Object getDeviceSpecificState(String key) {
        return deviceSpecificState.get(key);
    }
    
    public Map<String, Object> getAllDeviceSpecificState() {
        return new HashMap<>(deviceSpecificState);
    }
    
    // Convenience methods for common device properties
    public Integer getBrightness() {
        return (Integer) deviceSpecificState.get("brightness");
    }
    
    public LightColor getColor() {
        return (LightColor) deviceSpecificState.get("color");
    }
    
    public Double getCurrentTemperature() {
        return (Double) deviceSpecificState.get("currentTemperature");
    }
    
    public Double getTargetTemperature() {
        return (Double) deviceSpecificState.get("targetTemperature");
    }
    
    public ThermostatMode getThermostatMode() {
        return (ThermostatMode) deviceSpecificState.get("mode");
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("DeviceMemento{id=%s, name=%s, type=%s, on=%s, state=%s, time=%d", 
                               deviceId, deviceName, deviceType, isOn, state, timestamp));
        
        if (!deviceSpecificState.isEmpty()) {
            sb.append(", specificState=").append(deviceSpecificState);
        }
        
        sb.append("}");
        return sb.toString();
    }
}
