    package edu.neu.csye7374.core.factory;

    import edu.neu.csye7374.core.device.IDevice;
    import edu.neu.csye7374.devices.concrete.Light;
    import edu.neu.csye7374.devices.concrete.Thermostat;

    /**
     * Factory class for creating different types of devices
     * Implements Factory pattern for device creation
     */
    public class DeviceFactory {
        
        // Device type constants
        public static final String LIGHT = DeviceType.LIGHT.name();
        public static final String THERMOSTAT = DeviceType.THERMOSTAT.name();
        
        /**
         * Create a device of the specified type
         * @param deviceType The type of device to create
         * @param id The device ID
         * @param name The device name
         * @return The created device, or null if type not supported
         */
        public static IDevice createDevice(DeviceType deviceType, String id, String name) {
            if (deviceType == null || id == null || name == null) {
                return null;
            }
            
            switch (deviceType) {
                case LIGHT:
                    return new Light(id, name);
                case THERMOSTAT:
                    return new Thermostat(id, name);
                default:
                    System.err.println("Unknown device type: " + deviceType);
                    return null;
            }
        }
        
        /**
         * Create a device with default name
         * @param deviceType The type of device to create
         * @param id The device ID
         * @return The created device, or null if type not supported
         */
        public static IDevice createDevice(DeviceType deviceType, String id) {
            return createDevice(deviceType, id, deviceType.name() + "_" + id);
        }
        
        /**
         * Get all supported device types
         * @return Array of supported device types
         */
        public static String[] getSupportedDeviceTypes() {
            return new String[]{LIGHT, THERMOSTAT};
        }
        
        /**
         * Check if a device type is supported
         * @param deviceType The device type to check
         * @return true if the device type is supported
         */
        public static boolean isDeviceTypeSupported(String deviceType) {
            if (deviceType == null) {
                return false;
            }
            
            String upperType = deviceType.toUpperCase();
            return upperType.equals(LIGHT) || 
                upperType.equals(THERMOSTAT);
        }
    } 
