package edu.neu.csye7374.core.builder;

import edu.neu.csye7374.core.template.AutomationRule;
import edu.neu.csye7374.core.device.IDevice;

/**
 * Simple builder for automation rules using actual device objects
 */
public class RuleBuilder {
    
    private String name;
    private IDevice triggerDevice;
    private String triggerState;
    private IDevice actionDevice;
    private String action;
    
    public RuleBuilder when(IDevice device, String state) {
        this.triggerDevice = device;
        this.triggerState = state;
        return this;
    }
    
    public RuleBuilder then(String action, IDevice device) {
        this.action = action;
        this.actionDevice = device;
        return this;
    }
    
    public RuleBuilder named(String name) {
        this.name = name;
        return this;
    }
    
    public AutomationRule build() {
        if (name == null) {
            name = "Rule: " + triggerDevice.getName() + " -> " + actionDevice.getName();
        }
        return new AutomationRule(name, triggerDevice, triggerState, actionDevice, action);
    }
}