package edu.neu.csye7374.core.command;

/**
 * Light-specific commands with simple memento integration
 */
public class LightCommands {
    
    public static class SetBrightnessCommand extends AbstractCommand {
        private final int brightness;
        
        public SetBrightnessCommand(LightReceiver receiver, int brightness) {
            super(receiver, CommandType.SET_BRIGHTNESS, 
                  "Set brightness of " + receiver.getDeviceName() + " to " + brightness + "%");
            this.brightness = brightness;
        }
        
        @Override
        public String execute() {
            saveStateBeforeExecution();
            receiver.setBrightness(brightness);
            return receiver.getDeviceName() + " brightness set to " + brightness + "%";
        }
        
        @Override
        public String undo() {
            if (restoreStateBeforeExecution()) {
                return "Undid brightness change for " + receiver.getDeviceName();
            }
            return "Cannot undo brightness change for " + receiver.getDeviceName();
        }
    }
    
    public static class SetColorCommand extends AbstractCommand {
        private final String color;
        
        public SetColorCommand(LightReceiver receiver, String color) {
            super(receiver, CommandType.SET_COLOR, 
                  "Set color of " + receiver.getDeviceName() + " to " + color);
            this.color = color;
        }
        
        @Override
        public String execute() {
            saveStateBeforeExecution();
            receiver.setColor(color);
            return receiver.getDeviceName() + " color set to " + color;
        }
        
        @Override
        public String undo() {
            if (restoreStateBeforeExecution()) {
                return "Undid color change for " + receiver.getDeviceName();
            }
            return "Cannot undo color change for " + receiver.getDeviceName();
        }
    }
    
    public static class DimCommand extends AbstractCommand {
        private final int dimAmount;
        
        public DimCommand(LightReceiver receiver, int dimAmount) {
            super(receiver, CommandType.DIM, 
                  "Dim " + receiver.getDeviceName() + " by " + dimAmount + "%");
            this.dimAmount = dimAmount;
        }
        
        @Override
        public String execute() {
            saveStateBeforeExecution();
            receiver.dim(dimAmount);
            return receiver.getDeviceName() + " dimmed to " + receiver.getBrightness() + "%";
        }
        
        @Override
        public String undo() {
            if (restoreStateBeforeExecution()) {
                return "Undid dim operation for " + receiver.getDeviceName();
            }
            return "Cannot undo dim operation for " + receiver.getDeviceName();
        }
    }
    
    public static class BrightenCommand extends AbstractCommand {
        private final int brightenAmount;
        
        public BrightenCommand(LightReceiver receiver, int brightenAmount) {
            super(receiver, CommandType.BRIGHTEN, 
                  "Brighten " + receiver.getDeviceName() + " by " + brightenAmount + "%");
            this.brightenAmount = brightenAmount;
        }
        
        @Override
        public String execute() {
            saveStateBeforeExecution();
            receiver.brighten(brightenAmount);
            return receiver.getDeviceName() + " brightened to " + receiver.getBrightness() + "%";
        }
        
        @Override
        public String undo() {
            if (restoreStateBeforeExecution()) {
                return "Undid brighten operation for " + receiver.getDeviceName();
            }
            return "Cannot undo brighten operation for " + receiver.getDeviceName();
        }
    }
}