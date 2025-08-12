package edu.neu.csye7374.core.memento;

import java.util.Stack;

/**
 * Simple Memento Manager for Command pattern integration
 */
public class MementoManager {
    
    private final Stack<DeviceMemento> mementos;
    
    public MementoManager() {
        this.mementos = new Stack<>();
    }
    
    /**
     * Save a memento
     * @param memento The memento to save
     */
    public void save(DeviceMemento memento) {
        if (memento != null) {
            mementos.push(memento);
        }
    }
    
    /**
     * Get the last saved memento
     * @return Last memento or null if none exists
     */
    public DeviceMemento getLast() {
        return mementos.isEmpty() ? null : mementos.peek();
    }
    
    /**
     * Restore the last memento (removes it from stack)
     * @return Last memento or null if none exists
     */
    public DeviceMemento restore() {
        return mementos.isEmpty() ? null : mementos.pop();
    }
    
    /**
     * Check if there are mementos available
     * @return True if mementos exist
     */
    public boolean hasMementos() {
        return !mementos.isEmpty();
    }
    
    /**
     * Clear all mementos
     */
    public void clear() {
        mementos.clear();
    }
    
    /**
     * Get the number of mementos
     * @return Number of saved mementos
     */
    public int size() {
        return mementos.size();
    }
}
