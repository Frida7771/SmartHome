package edu.neu.csye7374.core.command;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.devices.concrete.Light;
import edu.neu.csye7374.devices.concrete.LightColor;
import edu.neu.csye7374.devices.concrete.Thermostat;
import edu.neu.csye7374.devices.concrete.ThermostatMode;

/**
 * Factory interface for creating device commands.
 * This allows the decorator to create commands without knowing about specific receivers.
 */
public interface DeviceCommandFactory {
    /**
     * Create a turn on command for a device
     */
    DeviceCommand createTurnOnCommand(IDevice device);
    
    /**
     * Create a turn off command for a device
     */
    DeviceCommand createTurnOffCommand(IDevice device);
    
    /**
     * Create a set brightness command for a light
     */
    DeviceCommand createSetBrightnessCommand(Light light, Integer brightness);
    
    /**
     * Create a set color command for a light
     */
    DeviceCommand createSetColorCommand(Light light, LightColor color);
    
    /**
     * Create a set target temperature command for a thermostat
     */
    DeviceCommand createSetTargetTemperatureCommand(Thermostat thermostat, Double temperature);
    
    /**
     * Create a set mode command for a thermostat
     */
    DeviceCommand createSetModeCommand(Thermostat thermostat, ThermostatMode mode);
}

