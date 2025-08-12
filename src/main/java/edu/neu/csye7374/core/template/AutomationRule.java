package edu.neu.csye7374.core.template;

import edu.neu.csye7374.core.device.IDevice;

/**
 * Automation rule template with device objects
 */
public class AutomationRule {
    
    private final String name;
    private final IDevice triggerDevice;
    private final String triggerState;
    private final IDevice actionDevice;
    private final String action;
    
    public AutomationRule(String name, IDevice triggerDevice, String triggerState, IDevice actionDevice, String action) {
        this.name = name;
        this.triggerDevice = triggerDevice;
        this.triggerState = triggerState;
        this.actionDevice = actionDevice;
        this.action = action;
    }
    
    /**
     * Check if this rule should be triggered
     */
    public boolean shouldTrigger(IDevice device, String state) {
        return triggerDevice.equals(device) && triggerState.equals(state);
    }
    
    /**
     * Execute the rule
     */
    public void execute() {
        System.out.println("AUTOMATION: Executing rule: " + name);
        System.out.println("  -> " + action + " " + actionDevice.getName());
        
        // Execute the actual action
        switch (action.toLowerCase()) {
            case "turn on":
                actionDevice.turnOn();
                break;
            case "turn off":
                actionDevice.turnOff();
                break;
            case "toggle":
                actionDevice.toggle();
                break;
            default:
                System.out.println("  -> Unknown action: " + action);
        }
    }
    
    // Getters
    public String getName() { return name; }
    public IDevice getTriggerDevice() { return triggerDevice; }
    public String getTriggerState() { return triggerState; }
    public IDevice getActionDevice() { return actionDevice; }
    public String getAction() { return action; }
    
    @Override
    public String toString() {
        return "Rule: " + name + " (When " + triggerDevice.getName() + " is " + triggerState + 
               ", " + action + " " + actionDevice.getName() + ")";
    }
}