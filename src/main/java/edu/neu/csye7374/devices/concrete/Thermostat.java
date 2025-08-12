package edu.neu.csye7374.devices.concrete;

import edu.neu.csye7374.core.device.AbstractDevice;
import edu.neu.csye7374.core.factory.DeviceType;
import edu.neu.csye7374.core.strategy.DeviceOperationStrategy;
import edu.neu.csye7374.core.strategy.ThermostatOperationStrategy;
import edu.neu.csye7374.core.strategy.Operation;

/**
 * Thermostat device implementation
 */
public class Thermostat extends AbstractDevice {
    
    private double currentTemperature;
    private double targetTemperature;
    private ThermostatMode mode;
    private DeviceOperationStrategy operationStrategy;
    
    public Thermostat(String id, String name) {
        super(id, name, DeviceType.THERMOSTAT);
        this.currentTemperature = 72.0;
        this.targetTemperature = 72.0;
        this.mode = ThermostatMode.HEAT;
        this.operationStrategy = new ThermostatOperationStrategy();
    }
    
    // Thermostat-specific operations
    public void setTargetTemperature(double temperature) {
        if (temperature >= 50 && temperature <= 90) {
            double oldTemp = this.targetTemperature;
            this.targetTemperature = temperature;
            System.out.println(name + " target temperature set to " + temperature + "°F");
            notifyPropertyChange("targetTemperature", oldTemp, temperature);
        }
    }
    
    public double getTargetTemperature() {
        return targetTemperature;
    }
    
    public void setCurrentTemperature(double temperature) {
        this.currentTemperature = temperature;
        System.out.println(name + " current temperature is " + temperature + "°F");
    }
    
    public double getCurrentTemperature() {
        return currentTemperature;
    }
    
    public void setMode(ThermostatMode mode) {
        ThermostatMode oldMode = this.mode;
        this.mode = mode;
        System.out.println(name + " mode set to " + mode);
        notifyPropertyChange("mode", oldMode, mode);
    }
    
    public ThermostatMode getMode() {
        return mode;
    }
    
    public void setOperationStrategy(DeviceOperationStrategy strategy) {
        this.operationStrategy = strategy;
    }
    
    public String executeOperation(Operation operation) {
        return operationStrategy.execute(this, operation);
    }
    
    @Override
    public void turnOn() {
        super.turnOn();
        System.out.println(name + " thermostat is now ON, target: " + targetTemperature + "°F, mode: " + mode);
    }
    
    @Override
    public void turnOff() {
        super.turnOff();
        System.out.println(name + " thermostat is now OFF");
    }
    
    @Override
    public String toString() {
        return String.format("Thermostat{id=%s, name=%s, state=%s, current=%.1f°F, target=%.1f°F, mode=%s}", 
                           id, name, state, currentTemperature, targetTemperature, mode);
    }
} 
