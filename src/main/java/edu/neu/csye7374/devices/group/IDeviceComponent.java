package edu.neu.csye7374.devices.group;

import edu.neu.csye7374.core.factory.DeviceType;

import java.util.List;

/**
 * Component interface for the Composite pattern
 * Allows treating individual devices and groups uniformly
 */
public interface IDeviceComponent {
    
    /**
     * Get the component ID
     * @return Component ID
     */
    String getId();
    
    /**
     * Get the component name
     * @return Component name
     */
    String getName();
    
    /**
     * Get the component type
     * @return Component type
     */
    DeviceType getType();
    
    /**
     * Turn on the component
     */
    void turnOn();
    
    /**
     * Turn off the component
     */
    void turnOff();
    
    /**
     * Toggle the component state
     */
    void toggle();
    
    /**
     * Check if component is on
     * @return true if component is on
     */
    boolean isOn();
    
    /**
     * Add a child component (for groups)
     * @param component The component to add
     */
    void addComponent(IDeviceComponent component);
    
    /**
     * Remove a child component (for groups)
     * @param component The component to remove
     */
    void removeComponent(IDeviceComponent component);
    
    /**
     * Get child components (for groups)
     * @return List of child components
     */
    List<IDeviceComponent> getChildren();
    
    /**
     * Get the total number of devices in this component
     * @return Number of devices
     */
    int getDeviceCount();
    
    /**
     * Display component information
     * @param indent Indentation level for display
     */
    void display(String indent);
} 
