package edu.neu.csye7374.core.registry;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.core.factory.DeviceType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

/**
 * Singleton DeviceRegistry for centralized device management
 */
public class DeviceRegistry {
    
    // Thread-safe Singleton instance
    private static class SingletonHelper {
        private static final DeviceRegistry INSTANCE = new DeviceRegistry();
    }
    
    // Thread-safe device storage
    private final Map<String, IDevice> devices = new ConcurrentHashMap<>();
    
    // Private constructor to prevent instantiation
    private DeviceRegistry() {}
    
    /**
     * Get the singleton instance
     * @return DeviceRegistry instance
     */
    public static DeviceRegistry getInstance() {
        return SingletonHelper.INSTANCE;
    }
    
    /**
     * Register a device
     * @param device The device to register
     * @return true if registration successful, false if device already exists
     */
    public boolean registerDevice(IDevice device) {
        if (device == null || device.getId() == null) {
            return false;
        }
        return devices.putIfAbsent(device.getId(), device) == null;
    }
    
    /**
     * Unregister a device
     * @param deviceId The ID of the device to unregister
     * @return The removed device, or null if not found
     */
    public IDevice unregisterDevice(String deviceId) {
        return devices.remove(deviceId);
    }
    
    /**
     * Get a device by ID
     * @param deviceId The device ID
     * @return The device, or null if not found
     */
    public IDevice getDevice(String deviceId) {
        return devices.get(deviceId);
    }
    
    /**
     * Get all registered devices
     * @return Unmodifiable set of all devices
     */
    public Set<IDevice> getAllDevices() {
        return Collections.unmodifiableSet(Set.copyOf(devices.values()));
    }
    
    /**
     * Get devices by type
     * @param deviceType The device type to filter by
     * @return Set of devices of the specified type
     */
    public Set<IDevice> getDevicesByType(DeviceType deviceType) {
        return devices.values().stream()
                .filter(device -> device.getType().equals(deviceType))
                .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * Get the total number of registered devices
     * @return Number of devices
     */
    public int getDeviceCount() {
        return devices.size();
    }
    
    /**
     * Check if a device is registered
     * @param deviceId The device ID to check
     * @return true if device is registered
     */
    public boolean isDeviceRegistered(String deviceId) {
        return devices.containsKey(deviceId);
    }
    
    /**
     * Clear all devices (for testing/reset purposes)
     */
    public void clearAllDevices() {
        devices.clear();
    }
}
