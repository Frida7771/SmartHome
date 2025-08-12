package edu.neu.csye7374;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.core.factory.DeviceType;
import edu.neu.csye7374.core.strategy.Operation;
import edu.neu.csye7374.devices.concrete.LightColor;
import edu.neu.csye7374.core.registry.DeviceRegistry;
import edu.neu.csye7374.core.factory.DeviceFactory;
import edu.neu.csye7374.core.observer.DeviceStateObserver;
import edu.neu.csye7374.core.memento.DeviceMemento;
import edu.neu.csye7374.devices.concrete.*;
import edu.neu.csye7374.devices.group.DeviceGroup;
import edu.neu.csye7374.devices.group.DeviceAdapter;
import edu.neu.csye7374.devices.group.decorator.GroupSceneDecorator;
import edu.neu.csye7374.devices.group.decorator.Scene;
import edu.neu.csye7374.flyweight.DeviceFlyweightFactory;
import edu.neu.csye7374.core.strategy.BasicOperationStrategy;
import edu.neu.csye7374.core.strategy.LightOperationStrategy;
import edu.neu.csye7374.core.strategy.ThermostatOperationStrategy;
import edu.neu.csye7374.core.command.CommandManager;
import edu.neu.csye7374.core.command.BasicCommands;
import edu.neu.csye7374.core.command.LightCommands;
import edu.neu.csye7374.core.command.ThermostatCommands;
import edu.neu.csye7374.core.command.LightReceiver;
import edu.neu.csye7374.core.command.ThermostatReceiver;
import edu.neu.csye7374.core.command.DeviceCommand;
import edu.neu.csye7374.devices.automation.AutomationEngine;
import edu.neu.csye7374.core.template.AutomationRule;
import edu.neu.csye7374.core.builder.RuleBuilder;

import java.util.Set;

public class Demo {

    public static void demonstrateDesignPatterns() {
        System.out.println("=== SMART HOME AUTOMATION SYSTEM - DESIGN PATTERNS DEMONSTRATION ===\n");

        // 1. SINGLETON PATTERN - DeviceRegistry
        System.out.println("1. SINGLETON PATTERN - DeviceRegistry");
        System.out.println("==========================================");
        DeviceRegistry registry1 = DeviceRegistry.getInstance();
        DeviceRegistry registry2 = DeviceRegistry.getInstance();
        System.out.println("Registry instances are the same: " + (registry1 == registry2));
        System.out.println("Initial device count: " + registry1.getDeviceCount());
        System.out.println();

        // 2. FACTORY PATTERN - DeviceFactory
        System.out.println("2. FACTORY PATTERN - DeviceFactory");
        System.out.println("===================================");
        IDevice light1 = DeviceFactory.createDevice(DeviceType.LIGHT, "light1", "Living Room Light");
        IDevice thermostat1 = DeviceFactory.createDevice(DeviceType.THERMOSTAT, "thermostat1", "Main Thermostat");

        System.out.println("Created devices:");
        System.out.println("  " + light1);
        System.out.println("  " + thermostat1);
        System.out.println();

        // 3. FLYWEIGHT PATTERN - DeviceFlyweightFactory
        System.out.println("3. FLYWEIGHT PATTERN - DeviceFlyweightFactory");
        System.out.println("==============================================");
        System.out.println("Flyweight count before: " + DeviceFlyweightFactory.getFlyweightCount());
        DeviceFlyweightFactory.getFlyweight(DeviceType.LIGHT);
        DeviceFlyweightFactory.getFlyweight(DeviceType.THERMOSTAT);
        System.out.println("Flyweight count after: " + DeviceFlyweightFactory.getFlyweightCount());
        System.out.println("Light flyweight: " + DeviceFlyweightFactory.getFlyweight(DeviceType.LIGHT));
        System.out.println("Thermostat flyweight: " + DeviceFlyweightFactory.getFlyweight(DeviceType.THERMOSTAT));
        System.out.println();

        // 4. OBSERVER PATTERN - Device State Changes
        System.out.println("4. OBSERVER PATTERN - Device State Changes");
        System.out.println("===========================================");
        DeviceStateObserver observer1 = new DeviceStateObserver("System Monitor");
        DeviceStateObserver observer2 = new DeviceStateObserver("User Interface");

        light1.addObserver(observer1);
        light1.addObserver(observer2);
        thermostat1.addObserver(observer1);

        System.out.println("Turning on devices (observers will be notified):");
        light1.turnOn();
        thermostat1.turnOn();
        System.out.println();

        // 5. MEMENTO PATTERN - State Persistence
        System.out.println("5. MEMENTO PATTERN - State Persistence");
        System.out.println("======================================");
        System.out.println("Saving current state of light1...");
        DeviceMemento lightMemento = light1.saveState();
        System.out.println("Saved memento: " + lightMemento);

        System.out.println("Turning off light1...");
        light1.turnOff();
        System.out.println("Light1 state: " + light1.getCurrentState());

        System.out.println("Restoring light1 state from memento...");
        light1.restoreState(lightMemento);
        System.out.println("Light1 state after restore: " + light1.getCurrentState());
        System.out.println();

        // 6. PROTOTYPE PATTERN - Device Cloning
        System.out.println("6. PROTOTYPE PATTERN - Device Cloning");
        System.out.println("=====================================");
        IDevice lightClone = light1.clone();
        System.out.println("Original light: " + light1);
        System.out.println("Cloned light: " + lightClone);
        System.out.println("Clone is different object: " + (light1 != lightClone));
        System.out.println();

        // 7. STATE PATTERN - Device Behavior
        System.out.println("7. STATE PATTERN - Device Behavior");
        System.out.println("==================================");
        System.out.println("Light1 current state: " + light1.getCurrentState());
        light1.turnOn();
        System.out.println("Light1 after turnOn: " + light1.getCurrentState());
        light1.toggle();
        System.out.println("Light1 after toggle: " + light1.getCurrentState());

        System.out.println("Thermostat1 current state: " + thermostat1.getCurrentState());
        thermostat1.turnOn();
        System.out.println("Thermostat1 after turnOn: " + thermostat1.getCurrentState());
        thermostat1.toggle();
        System.out.println("Thermostat1 after toggle: " + thermostat1.getCurrentState());
        System.out.println();

        // 8. STRATEGY PATTERN - Device Operations
        System.out.println("8. STRATEGY PATTERN - Device Operations");
        System.out.println("=======================================");
        Light light = (Light) light1;
        light.setOperationStrategy(new BasicOperationStrategy());

        System.out.println("Executing operations using strategy:");
        System.out.println("  " + light.executeOperation(Operation.GET_STATUS));
        System.out.println("  " + light.executeOperation(Operation.TURN_OFF));
        System.out.println("  " + light.executeOperation(Operation.GET_STATUS));
        System.out.println();

        // 9. COMPOSITE PATTERN - Device Groups
        System.out.println("9. COMPOSITE PATTERN - Device Groups");
        System.out.println("====================================");
        DeviceGroup livingRoomGroup = new DeviceGroup("group1", "Living Room Devices");

        // Add devices to group using adapters
        livingRoomGroup.addComponent(new DeviceAdapter(light1));
        livingRoomGroup.addComponent(new DeviceAdapter(thermostat1));

        System.out.println("Created device group: " + livingRoomGroup);
        System.out.println("Group device count: " + livingRoomGroup.getDeviceCount());

        System.out.println("Turning on entire group:");
        livingRoomGroup.turnOn();

        System.out.println("Group display:");
        livingRoomGroup.display("");
        System.out.println();

        // Register devices with registry
        System.out.println("10. REGISTRY INTEGRATION");
        System.out.println("=========================");
        registry1.registerDevice(light1);
        registry1.registerDevice(thermostat1);

        System.out.println("Registered devices in registry. Total count: " + registry1.getDeviceCount());

        Set<IDevice> allDevices = registry1.getAllDevices();
        System.out.println("All registered devices:");
        for (IDevice device : allDevices) {
            System.out.println("  " + device.getName() + " (" + device.getType() + ")");
        }

        Set<IDevice> lights = registry1.getDevicesByType(DeviceType.LIGHT);
        System.out.println("Lights in registry: " + lights.size());
        System.out.println();

        // Demonstrate device-specific functionality
        System.out.println("11. DEVICE-SPECIFIC FUNCTIONALITY");
        System.out.println("=================================");

        // Light functionality
        Light livingRoomLight = (Light) light1;
        livingRoomLight.setBrightness(75);
        livingRoomLight.setColor(LightColor.RED);
        System.out.println("Light settings: " + livingRoomLight);

        // Thermostat functionality
        Thermostat mainThermostat = (Thermostat) thermostat1;
        mainThermostat.setTargetTemperature(75.5);
        mainThermostat.setMode(ThermostatMode.COOL);
        System.out.println("Thermostat settings: " + mainThermostat);
        System.out.println();

        // Milestone 2 Demo
        System.out.println("=== SMART HOME AUTOMATION SYSTEM - MILESTONE 3 DEMO ===");
        System.out.println("=======================================================");

        // Strategy Pattern Updates
        System.out.println("=== STRATEGY PATTERN UPDATES ===");
        System.out.println("=================================");
        Light light2 = new Light("light2", "Bedroom Light");
        light2.setOperationStrategy(new LightOperationStrategy());
        System.out.println("Light2: " + light2);
        light2.executeOperation(Operation.TURN_ON);
        System.out.println("Light2 after turnOn: " + light2);
        light2.executeOperation(Operation.DIM);
        System.out.println("Light2 after dim: " + light2);
        light2.executeOperation(Operation.BRIGHTEN);
        System.out.println("Light2 after brighten: " + light2);

        Thermostat thermostat2 = new Thermostat("thermostat2", "Bedroom Thermostat");
        thermostat2.setOperationStrategy(new ThermostatOperationStrategy());
        System.out.println("Thermostat2: " + thermostat2);
        thermostat2.executeOperation(Operation.TURN_ON);
        System.out.println("Thermostat2 after turnOn: " + thermostat2);
        thermostat2.executeOperation(Operation.SET_HEAT_MODE);
        System.out.println("Thermostat2 after setHeatMode: " + thermostat2);
        thermostat2.executeOperation(Operation.SET_COOL_MODE);
        System.out.println("Thermostat2 after setCoolMode: " + thermostat2);

        // Command Pattern
        System.out.println("=== COMMAND PATTERN ===");
        // Create receivers (adapters for our devices)
        Light demoLight = new Light("demoLight", "Demo Light");
        Thermostat demoThermostat = new Thermostat("demoThermostat", "Demo Thermostat");

        LightReceiver lightReceiver = new LightReceiver(demoLight);
        ThermostatReceiver thermostatReceiver = new ThermostatReceiver(demoThermostat);

        // Create invoker
        CommandManager controller = new CommandManager();

        System.out.println("=== EXECUTING COMMANDS ===");

        // Create and execute basic commands
        DeviceCommand turnOnLight = new BasicCommands.TurnOnCommand(lightReceiver);
        String result1 = controller.executeCommand(turnOnLight);
        System.out.println("  " + result1);

        DeviceCommand setBrightness = new LightCommands.SetBrightnessCommand(lightReceiver, 75);
        String result2 = controller.executeCommand(setBrightness);
        System.out.println("  " + result2);

        DeviceCommand setColor = new LightCommands.SetColorCommand(lightReceiver, "RED");
        String result3 = controller.executeCommand(setColor);
        System.out.println("  " + result3);

        // Create and execute thermostat commands
        DeviceCommand turnOnThermostat = new BasicCommands.TurnOnCommand(thermostatReceiver);
        String result4 = controller.executeCommand(turnOnThermostat);
        System.out.println("  " + result4);

        DeviceCommand setTemp = new ThermostatCommands.SetTargetTemperatureCommand(thermostatReceiver, 78.0);
        String result5 = controller.executeCommand(setTemp);
        System.out.println("  " + result5);

        DeviceCommand setMode = new ThermostatCommands.SetModeCommand(thermostatReceiver, "COOL");
        String result6 = controller.executeCommand(setMode);
        System.out.println("  " + result6);

        System.out.println("\n=== UNDO/REDO DEMONSTRATION ===");

        // Demonstrate undo functionality
        System.out.println("Undoing last command:");
        String undoResult1 = controller.undo();
        System.out.println("  " + undoResult1);

        System.out.println("Undoing another command:");
        String undoResult2 = controller.undo();
        System.out.println("  " + undoResult2);

        System.out.println("After undo:");
        System.out.println("  " + demoLight);
        System.out.println("  " + demoThermostat);

        // Demonstrate redo functionality
        System.out.println("\nRedoing last undone command:");
        String redoResult1 = controller.redo();
        System.out.println("  " + redoResult1);

        System.out.println("Redoing another command:");
        String redoResult2 = controller.redo();
        System.out.println("  " + redoResult2);

        System.out.println("After redo:");
        System.out.println("  " + demoLight);
        System.out.println("  " + demoThermostat);

        // Automation Engine
        System.out.println("=== AUTOMATION ENGINE ===");
        
        AutomationEngine automationEngine = new AutomationEngine();

        Light roomLight = new Light("room_light", "Room Light");

        RuleBuilder ruleBuilder = new RuleBuilder();
        AutomationRule rule = ruleBuilder.when(roomLight, "ON")
                .then("turn off", livingRoomLight)
                .build();

        automationEngine.addRule(rule);
        automationEngine.processStateChange(roomLight, "ON");

        // Decorator Pattern - Apply Scene to Group
        System.out.println(" 12. DECORATOR PATTERN - Group Scene Application");
        System.out.println("================================================");
        GroupSceneDecorator livingRoomWithScenes = new GroupSceneDecorator(livingRoomGroup);

        Scene eveningScene = new Scene.Builder("Evening Relax")
                .lightsOn(true)
                .lightBrightness(40)
                .lightColor(LightColor.WARM_WHITE)
                .thermostatOn(true)
                .thermostatTarget(72.0)
                .thermostatMode(ThermostatMode.HEAT)
                .build();

        System.out.println("Applying scene: " + eveningScene.getName() + " to group: " + livingRoomWithScenes.getName());
        livingRoomWithScenes.applyScene(eveningScene);
        System.out.println();

        Scene movieScene = new Scene.Builder("Movie Night")
                .lightsOff(true)
                .thermostatOn(true)
                .thermostatTarget(68.0)
                .thermostatMode(ThermostatMode.COOL)
                .build();

        System.out.println("Applying scene: " + movieScene.getName() + " to group: " + livingRoomWithScenes.getName());
        livingRoomWithScenes.applyScene(movieScene);
        System.out.println();

        System.out.println("=== DESIGN PATTERNS DEMONSTRATION COMPLETE ===");

    }
}
