package edu.neu.csye7374.core.command;

/**
 * Enumeration of command types
 */
public enum CommandType {
    // Basic device operations
    TURN_ON("Turn On"),
    TURN_OFF("Turn Off"),
    TOGGLE("Toggle"),
    
    // Light-specific operations
    SET_BRIGHTNESS("Set Brightness"),
    SET_COLOR("Set Color"),
    DIM("Dim"),
    BRIGHTEN("Brighten"),
    
    // Thermostat-specific operations
    SET_TARGET_TEMPERATURE("Set Target Temperature"),
    SET_CURRENT_TEMPERATURE("Set Current Temperature"),
    SET_MODE("Set Mode"),
    
    // Advanced operations
    RESET_TO_DEFAULT("Reset to Default"),
    EMERGENCY_SHUTDOWN("Emergency Shutdown");
    
    private final String displayName;
    
    CommandType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}