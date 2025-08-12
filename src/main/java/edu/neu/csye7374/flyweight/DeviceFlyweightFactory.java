    package edu.neu.csye7374.flyweight;

    import edu.neu.csye7374.core.factory.DeviceType;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.concurrent.ConcurrentHashMap;
    import java.util.Map;

    /**
     * Flyweight factory for sharing common device data
     * Reduces memory usage for similar devices
     */
    public class DeviceFlyweightFactory {
        
        // Thread-safe storage for flyweight objects
        private static final Map<DeviceType, DeviceFlyweight> flyweights = new ConcurrentHashMap<>();
        
        /**
         * Get or create a flyweight for the given device type
         * @param deviceType The device type
         * @return The flyweight object
         */
        public static DeviceFlyweight getFlyweight(DeviceType deviceType) {
            return flyweights.computeIfAbsent(deviceType, DeviceFlyweight::new);
        }
        
        /**
         * Get the number of flyweight objects created
         * @return Number of flyweights
         */
        public static int getFlyweightCount() {
            return flyweights.size();
        }
        
        /**
         * Clear all flyweights (for testing/reset purposes)
         */
        public static void clearFlyweights() {
            flyweights.clear();
        }
        
        /**
         * Flyweight class containing intrinsic state (shared data)
         */
        public static class DeviceFlyweight {
            private final DeviceType deviceType;
            private final String manufacturer;
            private final String model;
            private final List<String> capabilities;
            
            public DeviceFlyweight(DeviceType deviceType) {
                this.deviceType = deviceType;
                // Set manufacturer and model based on device type
                switch (deviceType) {
                    case LIGHT:
                        this.manufacturer = "SmartLight Inc.";
                        this.model = "SL-2024";
                        this.capabilities = new ArrayList<>(List.of("Brightness", "Color"));
                        break;
                    case THERMOSTAT:
                        this.manufacturer = "ClimateControl Corp.";
                        this.model = "CC-2024";
                        this.capabilities = new ArrayList<>(List.of("Mode"));
                        break;
                    default:
                        this.manufacturer = "Generic Manufacturer";
                        this.model = "Generic Model";
                        this.capabilities = new ArrayList<>();
                }
            }
            
            // Getters for intrinsic state
            public DeviceType getDeviceType() { return deviceType; }
            public String getManufacturer() { return manufacturer; }
            public String getModel() { return model; }
            public List<String> getCapabilities() { return capabilities; }
            
            @Override
            public String toString() {
                return String.format("DeviceFlyweight{type=%s, manufacturer=%s, model=%s, capabilities=%s}", 
                                deviceType, manufacturer, model, capabilities);
            }
        }
    } 
