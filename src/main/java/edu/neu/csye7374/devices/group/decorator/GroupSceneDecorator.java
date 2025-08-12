package edu.neu.csye7374.devices.group.decorator;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.core.factory.DeviceType;
import edu.neu.csye7374.core.command.CommandExecutor;
import edu.neu.csye7374.core.command.DeviceCommandFactory;
import edu.neu.csye7374.core.command.DeviceCommand;
import edu.neu.csye7374.devices.concrete.Light;
import edu.neu.csye7374.devices.concrete.Thermostat;
import edu.neu.csye7374.devices.group.IDeviceComponent;

import java.util.List;

/**
 * Decorator that adds scene application capability to any IDeviceComponent (typically a DeviceGroup).
 */
public class GroupSceneDecorator implements IDeviceComponent {
    private final IDeviceComponent wrapped;
    private CommandExecutor commandExecutor;
    private DeviceCommandFactory commandFactory;

    public GroupSceneDecorator(IDeviceComponent wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Set command execution dependencies for undo/redo support
     */
    public void setCommandExecutionDependencies(CommandExecutor commandExecutor, DeviceCommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    /**
     * Apply a scene to all compatible devices inside the wrapped component.
     */
    public void applyScene(Scene scene) {
        if (scene == null) return;

        for (IDeviceComponent component : wrapped.getChildren()) {
            applySceneRecursive(component, scene);
        }
    }

    private void applySceneRecursive(IDeviceComponent component, Scene scene) {
        // If this is a group, recurse into children
        List<IDeviceComponent> children = component.getChildren();
        if (children != null && !children.isEmpty()) {
            for (IDeviceComponent child : children) {
                applySceneRecursive(child, scene);
            }
            return;
        }

        // Leaf: attempt to cast to core IDevice to set device-specific properties
        if (component instanceof edu.neu.csye7374.devices.group.DeviceAdapter) {
            IDevice device = ((edu.neu.csye7374.devices.group.DeviceAdapter) component).getDevice();
            if (device.getType() == DeviceType.LIGHT && device instanceof Light) {
                applyLightScene((Light) device, scene);
            } else if (device.getType() == DeviceType.THERMOSTAT && device instanceof Thermostat) {
                applyThermostatScene((Thermostat) device, scene);
            }
        }
    }

    private void applyLightScene(Light light, Scene scene) {
        // Use commands if available, otherwise fall back to direct calls
        if (commandExecutor != null && commandFactory != null) {
            applyLightSceneUsingCommands(light, scene);
        } else {
            applyLightSceneDirect(light, scene);
        }
    }

    private void applyThermostatScene(Thermostat thermostat, Scene scene) {
        // Use commands if available, otherwise fall back to direct calls
        if (commandExecutor != null && commandFactory != null) {
            applyThermostatSceneUsingCommands(thermostat, scene);
        } else {
            applyThermostatSceneDirect(thermostat, scene);
        }
    }

    private void applyLightSceneUsingCommands(Light light, Scene scene) {
        if (scene.getLightsOn() != null) {
            DeviceCommand command = scene.getLightsOn() 
                ? commandFactory.createTurnOnCommand(light)
                : commandFactory.createTurnOffCommand(light);
            commandExecutor.executeCommand(command);
        }
        if (scene.getLightBrightness() != null) {
            DeviceCommand command = commandFactory.createSetBrightnessCommand(light, scene.getLightBrightness());
            commandExecutor.executeCommand(command);
        }
        if (scene.getLightColor() != null) {
            DeviceCommand command = commandFactory.createSetColorCommand(light, scene.getLightColor());
            commandExecutor.executeCommand(command);
        }
    }

    private void applyThermostatSceneUsingCommands(Thermostat thermostat, Scene scene) {
        if (scene.getThermostatOn() != null) {
            DeviceCommand command = scene.getThermostatOn()
                ? commandFactory.createTurnOnCommand(thermostat)
                : commandFactory.createTurnOffCommand(thermostat);
            commandExecutor.executeCommand(command);
        }
        if (scene.getThermostatTargetTemperature() != null) {
            DeviceCommand command = commandFactory.createSetTargetTemperatureCommand(thermostat, scene.getThermostatTargetTemperature());
            commandExecutor.executeCommand(command);
        }
        if (scene.getThermostatMode() != null) {
            DeviceCommand command = commandFactory.createSetModeCommand(thermostat, scene.getThermostatMode());
            commandExecutor.executeCommand(command);
        }
    }

    private void applyLightSceneDirect(Light light, Scene scene) {
        if (scene.getLightsOn() != null) {
            if (scene.getLightsOn()) light.turnOn(); else light.turnOff();
        }
        if (scene.getLightBrightness() != null) {
            light.setBrightness(scene.getLightBrightness());
        }
        if (scene.getLightColor() != null) {
            light.setColor(scene.getLightColor());
        }
    }

    private void applyThermostatSceneDirect(Thermostat thermostat, Scene scene) {
        if (scene.getThermostatOn() != null) {
            if (scene.getThermostatOn()) thermostat.turnOn(); else thermostat.turnOff();
        }
        if (scene.getThermostatTargetTemperature() != null) {
            thermostat.setTargetTemperature(scene.getThermostatTargetTemperature());
        }
        if (scene.getThermostatMode() != null) {
            thermostat.setMode(scene.getThermostatMode());
        }
    }

    // Delegate IDeviceComponent methods to wrapped component
    @Override public String getId() { return wrapped.getId(); }
    @Override public String getName() { return wrapped.getName(); }
    @Override public DeviceType getType() { return wrapped.getType(); }
    @Override public void turnOn() { wrapped.turnOn(); }
    @Override public void turnOff() { wrapped.turnOff(); }
    @Override public void toggle() { wrapped.toggle(); }
    @Override public boolean isOn() { return wrapped.isOn(); }
    @Override public void addComponent(IDeviceComponent component) { wrapped.addComponent(component); }
    @Override public void removeComponent(IDeviceComponent component) { wrapped.removeComponent(component); }
    @Override public List<IDeviceComponent> getChildren() { return wrapped.getChildren(); }
    @Override public int getDeviceCount() { return wrapped.getDeviceCount(); }
    @Override public void display(String indent) { wrapped.display(indent); }
}


