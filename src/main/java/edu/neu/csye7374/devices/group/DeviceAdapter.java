
package edu.neu.csye7374.devices.group;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.core.factory.DeviceType;

import java.util.Collections;
import java.util.List;

/**
 * Adapter class to make IDevice compatible with IDeviceComponents
 * Enables individual devices to be used in composite structures
 */
public class DeviceAdapter implements IDeviceComponent {
    
    private final IDevice device;
    
    public DeviceAdapter(IDevice device) {
        this.device = device;
    }
    
    @Override
    public String getId() {
        return device.getId();
    }
    
    @Override
    public String getName() {
        return device.getName();
    }
    
    @Override
    public DeviceType getType() {
        return device.getType();
    }
    
    @Override
    public void turnOn() {
        device.turnOn();
    }
    
    @Override
    public void turnOff() {
        device.turnOff();
    }
    
    @Override
    public void toggle() {
        device.toggle();
    }
    
    @Override
    public boolean isOn() {
        return device.isOn();
    }
    
    @Override
    public void addComponent(IDeviceComponent component) {
        // Individual devices cannot have children
        System.out.println("Cannot add components to individual device: " + device.getName());
    }
    
    @Override
    public void removeComponent(IDeviceComponent component) {
        // Individual devices cannot have children
        System.out.println("Cannot remove components from individual device: " + device.getName());
    }
    
    @Override
    public List<IDeviceComponent> getChildren() {
        // Individual devices have no children
        return Collections.emptyList();
    }
    
    @Override
    public int getDeviceCount() {
        // Individual device counts as 1
        return 1;
    }
    
    @Override
    public void display(String indent) {
        System.out.println(indent + "Device: " + device.getName() + " (ID: " + device.getId() + 
                         ", Type: " + device.getType() + ") - " + 
                         (device.isOn() ? "ON" : "OFF"));
    }
    
   
    public IDevice getDevice() {
        return device;
    }
    
    @Override
    public String toString() {
        return "DeviceAdapter{" + device.toString() + "}";
    }
} 
