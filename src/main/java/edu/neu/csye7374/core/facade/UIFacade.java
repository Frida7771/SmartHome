package edu.neu.csye7374.core.facade;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.core.factory.DeviceFactory;
import edu.neu.csye7374.core.factory.DeviceType;
import edu.neu.csye7374.core.registry.DeviceRegistry;
import edu.neu.csye7374.core.observer.DeviceObserver;
import edu.neu.csye7374.devices.concrete.Light;
import edu.neu.csye7374.devices.concrete.Thermostat;
import edu.neu.csye7374.devices.concrete.LightColor;
import edu.neu.csye7374.devices.concrete.ThermostatMode;
import edu.neu.csye7374.core.strategy.Operation;
import edu.neu.csye7374.core.strategy.LightOperationStrategy;
import edu.neu.csye7374.core.strategy.ThermostatOperationStrategy;

import java.util.Set;

/**
 * Enhanced Facade Pattern Implementation
 * Provides a simplified interface to the smart home system
 * Now includes all operations from Demo.java
 */
public class UIFacade {
    
    private static UIFacade instance;
    private final DeviceRegistry deviceRegistry;
    
    private UIFacade() {
        this.deviceRegistry = DeviceRegistry.getInstance();
    }
    
    public static UIFacade getInstance() {
        if (instance == null) {
            instance = new UIFacade();
        }
        return instance;
    }
    
    // Simple device operations
    public IDevice createDevice(DeviceType type, String name) {
        String id = type.toString().toLowerCase() + "_" + System.currentTimeMillis();
        IDevice device = DeviceFactory.createDevice(type, id, name);
        deviceRegistry.registerDevice(device);
        return device;
    }
    
    public Set<IDevice> getAllDevices() {
        return deviceRegistry.getAllDevices();
    }
    
    public void turnOnDevice(String deviceId) {
        IDevice device = findDeviceById(deviceId);
        if (device != null) {
            device.turnOn();
        }
    }
    
    public void turnOffDevice(String deviceId) {
        IDevice device = findDeviceById(deviceId);
        if (device != null) {
            device.turnOff();
        }
    }
    
    public void addObserver(DeviceObserver observer) {
        for (IDevice device : deviceRegistry.getAllDevices()) {
            device.addObserver(observer);
        }
    }
    
    // Enhanced device operations for specific device types
    public void setLightBrightness(String deviceId, int brightness) {
        IDevice device = findDeviceById(deviceId);
        if (device instanceof Light) {
            ((Light) device).setBrightness(brightness);
        }
    }
    
    public void setLightColor(String deviceId, LightColor color) {
        IDevice device = findDeviceById(deviceId);
        if (device instanceof Light) {
            ((Light) device).setColor(color);
        }
    }
    
    public void setThermostatTemperature(String deviceId, double temperature) {
        IDevice device = findDeviceById(deviceId);
        if (device instanceof Thermostat) {
            ((Thermostat) device).setTargetTemperature(temperature);
        }
    }
    
    public void setThermostatMode(String deviceId, ThermostatMode mode) {
        IDevice device = findDeviceById(deviceId);
        if (device instanceof Thermostat) {
            ((Thermostat) device).setMode(mode);
        }
    }
    
    // Strategy pattern operations
    public String executeLightStrategy(String deviceId, Operation operation) {
        IDevice device = findDeviceById(deviceId);
        if (device instanceof Light) {
            Light light = (Light) device;
            light.setOperationStrategy(new LightOperationStrategy());
            return light.executeOperation(operation);
        }
        return "Device not found or not a light";
    }
    
    public String executeThermostatStrategy(String deviceId, Operation operation) {
        IDevice device = findDeviceById(deviceId);
        if (device instanceof Thermostat) {
            Thermostat thermostat = (Thermostat) device;
            thermostat.setOperationStrategy(new ThermostatOperationStrategy());
            return thermostat.executeOperation(operation);
        }
        return "Device not found or not a thermostat";
    }
    
    // Device state management
    public String getDeviceStatus(String deviceId) {
        IDevice device = findDeviceById(deviceId);
        if (device != null) {
            return device.toString();
        }
        return "Device not found";
    }
    
    public boolean isDeviceOn(String deviceId) {
        IDevice device = findDeviceById(deviceId);
        return device != null && device.isOn();
    }
    
    // Device registry operations
    public int getDeviceCount() {
        return deviceRegistry.getDeviceCount();
    }
    
    public Set<IDevice> getDevicesByType(DeviceType type) {
        return deviceRegistry.getDevicesByType(type);
    }
    
    public void registerDevice(IDevice device) {
        deviceRegistry.registerDevice(device);
    }
    
    /**
     * Delete a device from the system
     * @param deviceId The ID of the device to delete
     * @return The removed device, or null if not found
     */
    public IDevice deleteDevice(String deviceId) {
        return deviceRegistry.unregisterDevice(deviceId);
    }
    
    private IDevice findDeviceById(String deviceId) {
        return deviceRegistry.getAllDevices().stream()
                .filter(device -> device.getId().equals(deviceId))
                .findFirst()
                .orElse(null);
    }
}
