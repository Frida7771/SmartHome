package edu.neu.csye7374.core.device;

import edu.neu.csye7374.core.factory.DeviceType;
import edu.neu.csye7374.core.observer.DeviceObserver;
import edu.neu.csye7374.core.memento.DeviceMemento;

/**
 * Core interface for all smart home devices
 * Implements Observer, Memento, and Prototype patterns
 */
public interface IDevice extends Cloneable {
    
    // Basic device operations
    String getId();
    String getName();
    DeviceType getType();
    boolean isOn();
    void turnOn();
    void turnOff();
    void toggle();
    
    // Observer pattern methods
    void addObserver(DeviceObserver observer);
    void removeObserver(DeviceObserver observer);
    void notifyObservers();
    
    // Memento pattern methods
    DeviceMemento saveState();
    void restoreState(DeviceMemento memento);
    
    // Prototype pattern methods
    IDevice clone();
    
    // State management
    String getCurrentState();
    void setState(String state);
} 
