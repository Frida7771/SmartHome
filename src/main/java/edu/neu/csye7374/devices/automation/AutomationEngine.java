package edu.neu.csye7374.devices.automation;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.core.template.AutomationRule;
import java.util.ArrayList;
import java.util.List;

/**
 * Automation engine using device objects
 */
public class AutomationEngine {
    
    private final List<AutomationRule> rules;
    
    public AutomationEngine() {
        this.rules = new ArrayList<>();
    }
    
    /**
     * Add a rule
     */
    public void addRule(AutomationRule rule) {
        rules.add(rule);
        System.out.println("AUTOMATION: Added rule: " + rule.getName());
    }
    
    /**
     * Process a device state change
     */
    public void processStateChange(IDevice device, String state) {
        System.out.println("AUTOMATION: Processing state change - " + device.getName() + " is now " + state);
        
        for (AutomationRule rule : rules) {
            if (rule.shouldTrigger(device, state)) {
                rule.execute();
            }
        }
    }
    
    /**
     * Get all rules
     */
    public List<AutomationRule> getRules() {
        return new ArrayList<>(rules);
    }
    
    /**
     * Clear all rules
     */
    public void clearRules() {
        rules.clear();
        System.out.println("AUTOMATION: Cleared all rules");
    }
}
