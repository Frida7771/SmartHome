package edu.neu.csye7374.core.command;

import edu.neu.csye7374.devices.concrete.Thermostat;
import edu.neu.csye7374.devices.concrete.ThermostatMode;
import edu.neu.csye7374.core.memento.DeviceMemento;

/**
 * Thermostat receiver that implements DeviceReceiver interface
 * Adapts the Thermostat device to work with the Command pattern
 */
public class ThermostatReceiver implements DeviceReceiver {
    
    private final Thermostat thermostat;
    
    public ThermostatReceiver(Thermostat thermostat) {
        this.thermostat = thermostat;
    }
    
    // Basic device operations
    @Override
    public void turnOn() {
        thermostat.turnOn();
    }
    
    @Override
    public void turnOff() {
        thermostat.turnOff();
    }
    
    @Override
    public void toggle() {
        thermostat.toggle();
    }
    
    // Light operations (not applicable for thermostat)
    @Override
    public void setBrightness(int brightness) {
        throw new UnsupportedOperationException("Thermostat does not support brightness operations");
    }
    
    @Override
    public int getBrightness() {
        throw new UnsupportedOperationException("Thermostat does not support brightness operations");
    }
    
    @Override
    public void setColor(String color) {
        throw new UnsupportedOperationException("Thermostat does not support color operations");
    }
    
    @Override
    public String getColor() {
        throw new UnsupportedOperationException("Thermostat does not support color operations");
    }
    
    @Override
    public void dim(int amount) {
        throw new UnsupportedOperationException("Thermostat does not support dimming operations");
    }
    
    @Override
    public void brighten(int amount) {
        throw new UnsupportedOperationException("Thermostat does not support brightening operations");
    }
    
    // Thermostat-specific operations
    @Override
    public void setTargetTemperature(double temperature) {
        thermostat.setTargetTemperature(temperature);
    }
    
    @Override
    public double getTargetTemperature() {
        return thermostat.getTargetTemperature();
    }
    
    @Override
    public void setCurrentTemperature(double temperature) {
        thermostat.setCurrentTemperature(temperature);
    }
    
    @Override
    public double getCurrentTemperature() {
        return thermostat.getCurrentTemperature();
    }
    
    @Override
    public void setMode(String mode) {
        try {
            ThermostatMode thermostatMode = ThermostatMode.valueOf(mode.toUpperCase());
            thermostat.setMode(thermostatMode);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid mode: " + mode);
        }
    }
    
    @Override
    public String getMode() {
        return thermostat.getMode().toString();
    }
    
    // Utility methods
    @Override
    public String getDeviceId() {
        return thermostat.getId();
    }
    
    @Override
    public String getDeviceName() {
        return thermostat.getName();
    }
    
    @Override
    public String getDeviceType() {
        return thermostat.getType().toString();
    }
    
    @Override
    public boolean isOn() {
        return thermostat.isOn();
    }
    
    @Override
    public String getCurrentState() {
        return thermostat.getCurrentState();
    }
    
    // Getter for the underlying thermostat device
    public Thermostat getThermostat() {
        return thermostat;
    }

    // Memento methods
    @Override
    public DeviceMemento saveState() {
        DeviceMemento memento = thermostat.saveState();
        // Add thermostat-specific state
        memento.setDeviceSpecificState("currentTemperature", thermostat.getCurrentTemperature());
        memento.setDeviceSpecificState("targetTemperature", thermostat.getTargetTemperature());
        memento.setDeviceSpecificState("mode", thermostat.getMode()); // Store as ThermostatMode enum, not String
        return memento;
    }
    
    @Override
    public void restoreState(DeviceMemento memento) {
        thermostat.restoreState(memento);
        // Restore thermostat-specific state
        Double currentTemp = memento.getCurrentTemperature();
        Double targetTemp = memento.getTargetTemperature();
        ThermostatMode mode = memento.getThermostatMode();
        
        if (currentTemp != null && !currentTemp.equals(thermostat.getCurrentTemperature())) {
            thermostat.setCurrentTemperature(currentTemp);
        }
        if (targetTemp != null && !targetTemp.equals(thermostat.getTargetTemperature())) {
            thermostat.setTargetTemperature(targetTemp);
        }
        if (mode != null && !mode.equals(thermostat.getMode())) {
            thermostat.setMode(mode);
        }
    }
}