package edu.neu.csye7374.devices.group.decorator;

import edu.neu.csye7374.devices.concrete.LightColor;
import edu.neu.csye7374.devices.concrete.ThermostatMode;

/**
 * Simple scene model describing desired device settings.   
 */
public class Scene {
    private final String name;

    // Light settings
    private final Integer lightBrightness; 
    private final LightColor lightColor;
    private final Boolean lightsOn;

    // Thermostat settings
    private final Double thermostatTargetTemperature; 
    private final ThermostatMode thermostatMode; 
    private final Boolean thermostatOn; 

    // Scene Builder using Builder Pattern
    private Scene(Builder builder) {
        this.name = builder.name;
        this.lightBrightness = builder.lightBrightness;
        this.lightColor = builder.lightColor;
        this.lightsOn = builder.lightsOn;
        this.thermostatTargetTemperature = builder.thermostatTargetTemperature;
        this.thermostatMode = builder.thermostatMode;
        this.thermostatOn = builder.thermostatOn;
    }

    public String getName() { return name; }
    public Integer getLightBrightness() { return lightBrightness; }
    public LightColor getLightColor() { return lightColor; }
    public Boolean getLightsOn() { return lightsOn; }
    public Double getThermostatTargetTemperature() { return thermostatTargetTemperature; }
    public ThermostatMode getThermostatMode() { return thermostatMode; }
    public Boolean getThermostatOn() { return thermostatOn; }

    public static class Builder {
        private final String name;
        private Integer lightBrightness;
        private LightColor lightColor;
        private Boolean lightsOn;
        private Double thermostatTargetTemperature;
        private ThermostatMode thermostatMode;
        private Boolean thermostatOn;

        public Builder(String name) {
            this.name = name;
        }

        public Builder lightsOn(Boolean lightsOn) {
            this.lightsOn = lightsOn; return this;
        }

        public Builder lightsOff(Boolean lightsOff) {
            this.lightsOn = false; return this;
        }
        
        public Builder thermostatOn(Boolean thermostatOn) {
            this.thermostatOn = thermostatOn; return this;
        }

        public Builder thermostatOff(Boolean thermostatOff) {
            this.thermostatOn = false; return this;
        }

        public Builder lightBrightness(Integer brightness) {
            this.lightBrightness = brightness; return this;
        }

        public Builder lightColor(LightColor color) {
            this.lightColor = color; return this;
        }

        public Builder thermostatTarget(Double temperature) {
            this.thermostatTargetTemperature = temperature; return this;
        }

        public Builder thermostatMode(ThermostatMode mode) {
            this.thermostatMode = mode; return this;
        }

        public Scene build() { return new Scene(this); }
    }
}


