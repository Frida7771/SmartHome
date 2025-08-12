package edu.neu.csye7374.devices.concrete;

import edu.neu.csye7374.core.device.AbstractDevice;
import edu.neu.csye7374.core.factory.DeviceType;
import edu.neu.csye7374.core.strategy.DeviceOperationStrategy;
import edu.neu.csye7374.core.strategy.Operation;
import edu.neu.csye7374.core.strategy.LightOperationStrategy;

/**
 * Light device implementation
 */
public class Light extends AbstractDevice {
    
    private int brightness;
    private LightColor color;
    private DeviceOperationStrategy operationStrategy;
    
    public Light(String id, String name) {
        super(id, name, DeviceType.LIGHT);
        this.brightness = 100;
        this.color = LightColor.BRIGHT_WHITE;
        this.operationStrategy = new LightOperationStrategy();
    }
    
    // Light-specific operations
    public void setBrightness(int brightness) {
        if (brightness >= 0 && brightness <= 100) {
            int oldBrightness = this.brightness;
            this.brightness = brightness;
            System.out.println(name + " brightness set to " + brightness + "%");
            notifyPropertyChange("brightness", oldBrightness, brightness);
        }
    }
    
    public int getBrightness() {
        return brightness;
    }
    
    public void setColor(LightColor color) {
        LightColor oldColor = this.color;
        this.color = color;
        System.out.println(name + " color set to " + color.toString().toLowerCase());
        notifyPropertyChange("color", oldColor, color);
    }
    
    public LightColor getColor() {
        return color;
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
        System.out.println(name + " light is now ON with " + brightness + "% brightness and " + color + " color");
    }
    
    @Override
    public void turnOff() {
        super.turnOff();
        System.out.println(name + " light is now OFF");
    }
    
    @Override
    public String toString() {
        return String.format("Light{id=%s, name=%s, state=%s, brightness=%d%%, color=%s}", 
                           id, name, state, brightness, color);
    }
} 
