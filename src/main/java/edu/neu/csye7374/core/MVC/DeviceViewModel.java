package edu.neu.csye7374.core.MVC;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.core.factory.DeviceType;
import javafx.beans.property.*;

/**
 * Simple Model for device data in the UI
 * Part of MVC Pattern - Model Layer
 */
public class DeviceViewModel {
    
    private final StringProperty id;
    private final StringProperty name;
    private final ObjectProperty<DeviceType> type;
    private final BooleanProperty isOn;
    private final StringProperty status;
    
    public DeviceViewModel(IDevice device) {
        this.id = new SimpleStringProperty(device.getId());
        this.name = new SimpleStringProperty(device.getName());
        this.type = new SimpleObjectProperty<>(device.getType());
        this.isOn = new SimpleBooleanProperty(device.isOn());
        this.status = new SimpleStringProperty(device.isOn() ? "ON" : "OFF");
    }
    
    public void updateFromDevice(IDevice device) {
        id.set(device.getId());
        name.set(device.getName());
        type.set(device.getType());
        isOn.set(device.isOn());
        status.set(device.isOn() ? "ON" : "OFF");
    }
    
    // Property getters
    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public ObjectProperty<DeviceType> typeProperty() { return type; }
    public BooleanProperty isOnProperty() { return isOn; }
    public StringProperty statusProperty() { return status; }
    
    // Regular getters
    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public DeviceType getType() { return type.get(); }
    public boolean isOn() { return isOn.get(); }
    public String getStatus() { return status.get(); }
}