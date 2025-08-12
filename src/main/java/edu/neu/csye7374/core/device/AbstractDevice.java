package edu.neu.csye7374.core.device;

import edu.neu.csye7374.core.factory.DeviceType;
import edu.neu.csye7374.core.observer.DeviceObserver;
import edu.neu.csye7374.core.memento.DeviceMemento;
import edu.neu.csye7374.devices.state.DeviceState;
import edu.neu.csye7374.devices.state.OnState;
import edu.neu.csye7374.devices.state.OffState;
import edu.neu.csye7374.flyweight.DeviceFlyweightFactory;
import edu.neu.csye7374.flyweight.DeviceFlyweightFactory.DeviceFlyweight;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Abstract base class for all devices
 * Implements Observer, Memento, and Prototype patterns
 */
public abstract class AbstractDevice implements IDevice {
    
    // Device properties
    protected final String id;
    protected final String name;
    protected final DeviceType type;
    
    // State management
    protected DeviceState currentState;
    protected boolean isOn;
    protected String state;
    
    // Observer pattern
    protected final List<DeviceObserver> observers = new CopyOnWriteArrayList<>();
    
    // Flyweight pattern
    protected final DeviceFlyweight flyweight;
    
    // State objects
    protected final OnState onState = new OnState();
    protected final OffState offState = new OffState();
    
    public AbstractDevice(String id, String name, DeviceType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isOn = false;
        this.state = "OFF";
        this.currentState = offState;
        
        // Get flyweight for this device type
        this.flyweight = DeviceFlyweightFactory.getFlyweight(type);
    }
    
    // Basic device operations
    @Override
    public String getId() { return id; }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public DeviceType getType() { return type; }
    
    @Override
    public boolean isOn() { return isOn; }
    
    @Override
    public void turnOn() {
        currentState.turnOn(this);
        // Note: State change notification is handled by setState() called from state classes
    }
    
    @Override
    public void turnOff() {
        currentState.turnOff(this);
        // Note: State change notification is handled by setState() called from state classes
    }
    
    @Override
    public void toggle() {
        currentState.toggle(this);
    }
    
    @Override
    public String getCurrentState() { return state; }
    
    @Override
    public void setState(String state) {
        String oldState = this.state;
        this.state = state;
        this.isOn = "ON".equals(state);
        this.currentState = this.isOn ? onState : offState;
        notifyObservers(oldState, this.state);
    }
    
    // Observer pattern implementation
    @Override
    public void addObserver(DeviceObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(DeviceObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        notifyObservers(null, this.state);
    }
    
    protected void notifyObservers(String oldState, String newState) {
        for (DeviceObserver observer : observers) {
            observer.onDeviceStateChanged(this, oldState, newState);
        }
    }

    /**
     * Notify observers of a property change
     * @param propertyName The name of the property that changed
     * @param oldValue The previous value
     * @param newValue The new value
     */
    protected void notifyPropertyChange(String propertyName, Object oldValue, Object newValue) {
        for (DeviceObserver observer : observers) {
            observer.onDevicePropertyChanged(this, propertyName, oldValue, newValue);
        }
    }
    
    // Memento pattern implementation
    @Override
    public DeviceMemento saveState() {
        return new DeviceMemento(id, name, type, isOn, state);
    }
    
    @Override
    public void restoreState(DeviceMemento memento) {
        if (memento != null && memento.getDeviceId().equals(this.id)) {
            String oldState = this.state;
            this.isOn = memento.isOn();
            this.state = memento.getState();
            this.currentState = this.isOn ? onState : offState;
            notifyObservers(oldState, this.state);
        }
    }
    
    // Prototype pattern implementation
    @Override
    public IDevice clone() {
        try {
            AbstractDevice cloned = (AbstractDevice) super.clone();
            // Reset observers for cloned device
            cloned.observers.clear();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    // Flyweight pattern - get shared data
    public DeviceFlyweight getFlyweight() {
        return flyweight;
    }
    
    @Override
    public String toString() {
        return String.format("%s{id=%s, name=%s, type=%s, state=%s, on=%s}", 
                           getClass().getSimpleName(), id, name, type, state, isOn);
    }
} 
