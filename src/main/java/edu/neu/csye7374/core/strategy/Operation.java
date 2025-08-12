package edu.neu.csye7374.core.strategy;

public enum Operation {
    TURN_ON, TURN_OFF, TOGGLE, GET_STATUS,

        // Light-specific operations
        DIM,
        BRIGHTEN,
        
        // Thermostat-specific operations
        SET_HEAT_MODE,
        SET_COOL_MODE
        
}
