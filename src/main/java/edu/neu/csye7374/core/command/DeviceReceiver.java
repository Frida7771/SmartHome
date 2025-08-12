package edu.neu.csye7374.core.command;

import edu.neu.csye7374.core.memento.DeviceMemento;

/**
 * Receiver interface for the Command pattern
 * Defines the operations that can be performed on devices
 */
public interface DeviceReceiver {
    
    // Basic device operations
    void turnOn();
    void turnOff();
    void toggle();
    
    // Light-specific operations
    void setBrightness(int brightness);
    int getBrightness();
    void setColor(String color);
    String getColor();
    void dim(int amount);
    void brighten(int amount);
    
    // Thermostat-specific operations
    void setTargetTemperature(double temperature);
    double getTargetTemperature();
    void setCurrentTemperature(double temperature);
    double getCurrentTemperature();
    void setMode(String mode);
    String getMode();
    
    // Utility methods
    String getDeviceId();
    String getDeviceName();
    String getDeviceType();
    boolean isOn();
    String getCurrentState();
    
    // Memento methods
    DeviceMemento saveState();
    void restoreState(DeviceMemento memento);
}