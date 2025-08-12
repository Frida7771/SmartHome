package edu.neu.csye7374.core.strategy;

import edu.neu.csye7374.core.device.IDevice;

/**
 * Strategy interface for device operations
 * Part of the Strategy pattern implementation
 */
public interface DeviceOperationStrategy {
    
    /**
     * Execute the operation on the device
     * @param device The device to operate on
     * @param operation Operations from enum
     * @return Result of the operation
     */
    String execute(IDevice device, Operation operation);
    
    /**
     * Get the strategy name
     * @return Strategy name
     */
    String getStrategyName();
    
    /**
     * Get the strategy description
     * @return Strategy description
     */
    String getStrategyDescription();
}
