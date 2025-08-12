package edu.neu.csye7374.core.command;

import edu.neu.csye7374.devices.concrete.Light;
import edu.neu.csye7374.devices.concrete.LightColor;
import edu.neu.csye7374.core.memento.DeviceMemento;

/**
 * Light receiver that implements DeviceReceiver interface
 * Adapts the Light device to work with the Command pattern
 */
public class LightReceiver implements DeviceReceiver {
    
    private final Light light;
    
    public LightReceiver(Light light) {
        this.light = light;
    }
    
    // Basic device operations
    @Override
    public void turnOn() {
        light.turnOn();
    }
    
    @Override
    public void turnOff() {
        light.turnOff();
    }
    
    @Override
    public void toggle() {
        light.toggle();
    }
    
    // Light-specific operations
    @Override
    public void setBrightness(int brightness) {
        light.setBrightness(brightness);
    }
    
    @Override
    public int getBrightness() {
        return light.getBrightness();
    }
    
    @Override
    public void setColor(String color) {
        try {
            LightColor lightColor = LightColor.valueOf(color.toUpperCase());
            light.setColor(lightColor);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid color: " + color);
        }
    }
    
    @Override
    public String getColor() {
        return light.getColor().toString();
    }
    
    @Override
    public void dim(int amount) {
        int currentBrightness = light.getBrightness();
        int newBrightness = Math.max(0, currentBrightness - amount);
        light.setBrightness(newBrightness);
    }
    
    @Override
    public void brighten(int amount) {
        int currentBrightness = light.getBrightness();
        int newBrightness = Math.min(100, currentBrightness + amount);
        light.setBrightness(newBrightness);
    }
    
    // Thermostat operations (not applicable for light)
    @Override
    public void setTargetTemperature(double temperature) {
        throw new UnsupportedOperationException("Light does not support temperature operations");
    }
    
    @Override
    public double getTargetTemperature() {
        throw new UnsupportedOperationException("Light does not support temperature operations");
    }
    
    @Override
    public void setCurrentTemperature(double temperature) {
        throw new UnsupportedOperationException("Light does not support temperature operations");
    }
    
    @Override
    public double getCurrentTemperature() {
        throw new UnsupportedOperationException("Light does not support temperature operations");
    }
    
    @Override
    public void setMode(String mode) {
        throw new UnsupportedOperationException("Light does not support mode operations");
    }
    
    @Override
    public String getMode() {
        throw new UnsupportedOperationException("Light does not support mode operations");
    }
    
    // Utility methods
    @Override
    public String getDeviceId() {
        return light.getId();
    }
    
    @Override
    public String getDeviceName() {
        return light.getName();
    }
    
    @Override
    public String getDeviceType() {
        return light.getType().toString();
    }
    
    @Override
    public boolean isOn() {
        return light.isOn();
    }
    
    @Override
    public String getCurrentState() {
        return light.getCurrentState();
    }
    
    // Getter for the underlying light device
    public Light getLight() {
        return light;
    }

        // Memento methods
        @Override
        public DeviceMemento saveState() {
            DeviceMemento memento = light.saveState();
            // Add light-specific state
            memento.setDeviceSpecificState("brightness", light.getBrightness());
            memento.setDeviceSpecificState("color", light.getColor()); // Store as LightColor enum, not String
            return memento;
        }
        
        @Override
        public void restoreState(DeviceMemento memento) {
            light.restoreState(memento);
            // Restore light-specific state only if values are different
            Integer brightness = memento.getBrightness();
            LightColor color = memento.getColor();
            
            if (brightness != null && brightness != light.getBrightness()) {
                light.setBrightness(brightness);
            }
            if (color != null && !color.equals(light.getColor())) {
                light.setColor(color);
            }
        }
}