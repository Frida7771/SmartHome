package edu.neu.csye7374.devices.group;

import edu.neu.csye7374.core.factory.DeviceType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * DeviceGroup implementation for the Composite pattern
 * Allows treating groups of devices as a single device
 */
public class DeviceGroup implements IDeviceComponent {
    
    private final String id;
    private final String name;
    private final DeviceType type;
    private final List<IDeviceComponent> children;
    private boolean isOn;
    
    public DeviceGroup(String id, String name) {
        this.id = id;
        this.name = name;
        this.type = DeviceType.GROUP;
        this.children = new CopyOnWriteArrayList<>();
        this.isOn = false;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public DeviceType getType() {
        return type;
    }
    
    @Override
    public void turnOn() {
        System.out.println("Turning ON all devices in group: " + name);
        for (IDeviceComponent component : children) {
            component.turnOn();
        }
        this.isOn = true;
    }
    
    @Override
    public void turnOff() {
        System.out.println("Turning OFF all devices in group: " + name);
        for (IDeviceComponent component : children) {
            component.turnOff();
        }
        this.isOn = false;
    }
    
    @Override
    public void toggle() {
        if (isOn) {
            turnOff();
        } else {
            turnOn();
        }
    }
    
    @Override
    public boolean isOn() {
        // Group is considered ON if at least one device is ON
        return children.stream().anyMatch(IDeviceComponent::isOn);
    }
    
    @Override
    public void addComponent(IDeviceComponent component) {
        if (component != null && !children.contains(component)) {
            children.add(component);
            System.out.println("Added " + component.getName() + " to group " + name);
        }
    }
    
    @Override
    public void removeComponent(IDeviceComponent component) {
        if (children.remove(component)) {
            System.out.println("Removed " + component.getName() + " from group " + name);
        }
    }
    
    @Override
    public List<IDeviceComponent> getChildren() {
        return new ArrayList<>(children);
    }
    
    @Override
    public int getDeviceCount() {
        int count = 0;
        for (IDeviceComponent component : children) {
            count += component.getDeviceCount();
        }
        return count;
    }
    
    @Override
    public void display(String indent) {
        System.out.println(indent + "Group: " + name + " (ID: " + id + ") - " + 
                         (isOn() ? "ON" : "OFF") + " - " + getDeviceCount() + " devices");
        for (IDeviceComponent component : children) {
            component.display(indent + "  ");
        }
    }
    
    /**
     * Get devices by type within this group
     * @param deviceType The type to filter by
     * @return List of components of the specified type
     */
    public List<IDeviceComponent> getDevicesByType(String deviceType) {
        List<IDeviceComponent> result = new ArrayList<>();
        for (IDeviceComponent component : children) {
            if (deviceType.equals(component.getType())) {
                result.add(component);
            }
        }
        return result;
    }
    
    /**
     * Execute operation on all devices in the group
     * @param operation The operation to execute
     */
    public void executeOnAll(String operation) {
        System.out.println("Executing '" + operation + "' on all devices in group: " + name);
        for (IDeviceComponent component : children) {
            if (component instanceof edu.neu.csye7374.core.device.IDevice) {
                edu.neu.csye7374.core.device.IDevice device = (edu.neu.csye7374.core.device.IDevice) component;
                // This would need to be implemented based on the specific operation
                System.out.println("  - " + component.getName() + ": " + operation);
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format("DeviceGroup{id=%s, name=%s, deviceCount=%d, isOn=%s}", 
                           id, name, getDeviceCount(), isOn());
    }
} 
