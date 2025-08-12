package edu.neu.csye7374.core.MVC;

import edu.neu.csye7374.core.device.IDevice;
import edu.neu.csye7374.core.factory.DeviceType;
import edu.neu.csye7374.core.facade.UIFacade;
import edu.neu.csye7374.userInterface.UIUpdateObserver;
import edu.neu.csye7374.devices.concrete.Light;
import edu.neu.csye7374.devices.concrete.Thermostat;
import edu.neu.csye7374.devices.concrete.LightColor;
import edu.neu.csye7374.devices.concrete.ThermostatMode;
import edu.neu.csye7374.core.memento.DeviceMemento;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import edu.neu.csye7374.devices.group.DeviceGroup;
import edu.neu.csye7374.devices.group.DeviceAdapter;
import edu.neu.csye7374.devices.group.decorator.GroupSceneDecorator;
import edu.neu.csye7374.devices.group.decorator.Scene;
import edu.neu.csye7374.core.template.AutomationRule;
import edu.neu.csye7374.core.command.DeviceCommand;
import edu.neu.csye7374.core.command.BasicCommands;
import edu.neu.csye7374.core.command.LightCommands;
import edu.neu.csye7374.core.command.ThermostatCommands;
import edu.neu.csye7374.core.command.LightReceiver;
import edu.neu.csye7374.core.command.ThermostatReceiver;
import edu.neu.csye7374.core.command.DeviceReceiver;
import edu.neu.csye7374.core.command.CommandExecutor;
import edu.neu.csye7374.core.command.DeviceCommandFactory;
import edu.neu.csye7374.core.command.CommandManager;
import edu.neu.csye7374.devices.automation.AutomationEngine;
import edu.neu.csye7374.core.builder.RuleBuilder;  
import edu.neu.csye7374.devices.group.IDeviceComponent;

/**
 * Natural Smart Home Controller
 * Provides intuitive interface for smart home management
 */
public class DeviceController implements CommandExecutor, DeviceCommandFactory {
    
    @FXML private VBox deviceListContainer;
    @FXML private Label statusLabel;
    @FXML private TextArea systemLogArea;
    
    // Automation & Scenes
    @FXML private VBox automationRuleContainer;
    @FXML private VBox sceneContainer;
    @FXML private ListView<String> groupListView;
    @FXML private ComboBox<String> sceneGroupSelector;
    
    private UIFacade facade;
    private ObservableList<DeviceViewModel> deviceModels;
    private DeviceViewModel selectedDevice;
    private UIUpdateObserver uiObserver;
    private Consumer<UIUpdateObserver.UIUpdate> uiUpdateCallback;
    
    // Command stack for undo/redo (already wired via receivers/manager elsewhere if needed)
    private final CommandManager commandManager = new CommandManager();
    
    // Automation Engine
    private final AutomationEngine automationEngine = new AutomationEngine();
    
    // Device Groups and Scenes
    private Map<String, DeviceGroup> deviceGroups;
    private Map<String, Scene> scenes;
    private ObservableList<String> groupNames;
    private int groupCounter = 0;
    
    // Automation Rules Management
    private ObservableList<String> ruleNames;
    private Map<String, AutomationRule> automationRules;
    private int ruleCounter = 0;
    
    // Command Pattern - Device Receivers for undo/redo functionality
    private Map<String, LightReceiver> lightReceivers;
    private Map<String, ThermostatReceiver> thermostatReceivers;
    
    // Memento Pattern
    private Map<String, DeviceMemento> savedStates;

    public void setUIUpdateCallback(Consumer<UIUpdateObserver.UIUpdate> callback) {
        this.uiUpdateCallback = callback;
    }
    
    @FXML
    public void initialize() {
        facade = UIFacade.getInstance();
        deviceModels = FXCollections.observableArrayList();
        
        // Initialize device groups and scenes
        deviceGroups = new HashMap<>();
        scenes = new HashMap<>();
        groupNames = FXCollections.observableArrayList();
        
        // Initialize automation rules
        ruleNames = FXCollections.observableArrayList();
        automationRules = new HashMap<>();
        
        // Initialize command receivers
        lightReceivers = new HashMap<>();
        thermostatReceivers = new HashMap<>();
        
        // Initialize saved states
        savedStates = new HashMap<>();
        
        // Bind group UI controls once
        if (groupListView != null) {
            groupListView.setItems(groupNames);
            // Set custom cell factory to add delete buttons
            setupGroupListCellFactory();
        }
        if (sceneGroupSelector != null) {
            sceneGroupSelector.setItems(groupNames);
        }
        
        // Set up UI observer (no global attach)
        uiObserver = new UIUpdateObserver("Smart Home UI");
        uiObserver.setUIUpdateCallback(this::handleUIUpdate);
        
        // Load initial devices (empty at start) and setup UI panels
        loadDevices();
        // Attach UI observer to existing devices once
        for (IDevice device : facade.getAllDevices()) {
            device.addObserver(uiObserver);
        }
        setupAutomationUI();
        setupSceneUI();
        updateStatus("Welcome to your Smart Home! Start by adding devices.");
        logActivity("Smart Home System initialized");
    }
    
    private void loadDevices() {
        deviceModels.clear();
        lightReceivers.clear();
        thermostatReceivers.clear();
        
        Set<IDevice> devices = facade.getAllDevices();
        for (IDevice device : devices) {
            DeviceViewModel model = new DeviceViewModel(device);
            deviceModels.add(model);
            
            // Initialize receivers for command pattern support
            if (device instanceof Light) {
                lightReceivers.put(device.getId(), new LightReceiver((Light) device));
            } else if (device instanceof Thermostat) {
                thermostatReceivers.put(device.getId(), new ThermostatReceiver((Thermostat) device));
            }
        }
        updateDeviceList();
    }
    
    private void updateDeviceList() {
        // Store current group selections before clearing
        Map<String, String> currentGroupSelections = new HashMap<>();
        for (javafx.scene.Node node : deviceListContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox deviceItem = (VBox) node;
                // Find the ComboBox in the device item
                ComboBox<String> groupCombo = findGroupComboInDeviceItem(deviceItem);
                if (groupCombo != null && groupCombo.getValue() != null) {
                    // Extract device ID from the device item to map the selection
                    String deviceId = extractDeviceIdFromDeviceItem(deviceItem);
                    if (deviceId != null) {
                        currentGroupSelections.put(deviceId, groupCombo.getValue());
                    }
                }
            }
        }
        
        deviceListContainer.getChildren().clear();
        if (deviceModels.isEmpty()) {
            Label emptyLabel = new Label("No devices yet. Add some devices to get started!");
            emptyLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
            deviceListContainer.getChildren().add(emptyLabel);
            return;
        }
        for (DeviceViewModel model : deviceModels) {
            VBox deviceItem = createDeviceItem(model);
            
            // Only restore group selection if the device is still in a group
            String currentGroup = findDeviceGroup(model.getId());
            if (currentGroup != null && currentGroupSelections.containsKey(model.getId())) {
                ComboBox<String> groupCombo = findGroupComboInDeviceItem(deviceItem);
                if (groupCombo != null) {
                    String previousSelection = currentGroupSelections.get(model.getId());
                    // Check if the previous selection is still valid
                    if (groupNames.contains(previousSelection)) {
                        groupCombo.setValue(previousSelection);
                    }
                }
            }
            
            deviceListContainer.getChildren().add(deviceItem);
        }
    }
    
    /**
     * Find the group ComboBox within a device item VBox
     */
    @SuppressWarnings("unchecked")
    private ComboBox<String> findGroupComboInDeviceItem(VBox deviceItem) {
        for (javafx.scene.Node child : deviceItem.getChildren()) {
            if (child instanceof HBox) {
                HBox hbox = (HBox) child;
                for (javafx.scene.Node hboxChild : hbox.getChildren()) {
                    if (hboxChild instanceof HBox) {
                        HBox nestedHbox = (HBox) hboxChild;
                        for (javafx.scene.Node nestedChild : nestedHbox.getChildren()) {
                            if (nestedChild instanceof ComboBox) {
                                ComboBox<?> combo = (ComboBox<?>) nestedChild;
                                if (combo.getPromptText() != null && combo.getPromptText().equals("Select Group")) {
                                    return (ComboBox<String>) combo;
                                }
                            }
                        }
                    }
                    if (hboxChild instanceof ComboBox) {
                        ComboBox<?> combo = (ComboBox<?>) hboxChild;
                        if (combo.getPromptText() != null && combo.getPromptText().equals("Select Group")) {
                            return (ComboBox<String>) combo;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Extract device ID from a device item VBox by looking for labels with device name
     */
    private String extractDeviceIdFromDeviceItem(VBox deviceItem) {
        for (javafx.scene.Node child : deviceItem.getChildren()) {
            if (child instanceof HBox) {
                HBox hbox = (HBox) child;
                for (javafx.scene.Node hboxChild : hbox.getChildren()) {
                    if (hboxChild instanceof VBox) {
                        VBox vbox = (VBox) hboxChild;
                        for (javafx.scene.Node vboxChild : vbox.getChildren()) {
                            if (vboxChild instanceof Label) {
                                Label label = (Label) vboxChild;
                                String labelText = label.getText();
                                // Find device by name to get ID
                                for (DeviceViewModel model : deviceModels) {
                                    if (model.getName().equals(labelText)) {
                                        return model.getId();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private VBox createDeviceItem(DeviceViewModel model) {
        VBox container = new VBox(12);
        container.setStyle("-fx-padding: 16; -fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);");
        
        // Device header with enhanced info
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label iconLabel = new Label(model.getType() == DeviceType.LIGHT ? "💡" : "🌡️");
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        VBox deviceInfo = new VBox(4);
        Label nameLabel = new Label(model.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.statusProperty());
        statusLabel.styleProperty().bind(
            Bindings.when(model.isOnProperty())
                .then("-fx-text-fill: #28a745; -fx-font-size: 12px; -fx-font-weight: bold;")
                .otherwise("-fx-text-fill: #dc3545; -fx-font-size: 12px; -fx-font-weight: bold;")
        );
        deviceInfo.getChildren().addAll(nameLabel, statusLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Quick toggle button
        Button toggleBtn = new Button();
        toggleBtn.textProperty().bind(
            Bindings.when(model.isOnProperty()).then("Turn Off").otherwise("Turn On")
        );
        toggleBtn.styleProperty().bind(
            Bindings.when(model.isOnProperty())
                .then("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 4;")
                .otherwise("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 4;")
        );
        toggleBtn.setOnAction(e -> toggleDevice(model));
        
        headerBox.getChildren().addAll(iconLabel, deviceInfo, spacer, toggleBtn);
        
        // Device-specific controls
        VBox deviceControls = new VBox(8);
        deviceControls.setStyle("-fx-padding: 12; -fx-background-color: #f8f9fa; -fx-background-radius: 6; -fx-border-color: #dee2e6; -fx-border-radius: 6; -fx-border-width: 1;");
        
        if (model.getType() == DeviceType.LIGHT) {
            deviceControls.getChildren().add(createIntegratedLightControls(model));
        } else if (model.getType() == DeviceType.THERMOSTAT) {
            deviceControls.getChildren().add(createIntegratedThermostatControls(model));
        }
        
        // Action buttons row (Group management + State controls)
        HBox actionBox = new HBox(12);
        actionBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        actionBox.setStyle("-fx-padding: 8 0 0 0;");
        
        // Group management section
        HBox groupBox = new HBox(8);
        groupBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // Always show both add and remove controls
        Label addToGroupLabel = new Label("Add to group:");
        addToGroupLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: black;");
        ComboBox<String> addToGroupCombo = new ComboBox<>(groupNames);
        addToGroupCombo.setPromptText("Select Group");
        addToGroupCombo.setStyle("-fx-font-size: 11px; -fx-text-fill: black;");
        addToGroupCombo.setOnAction(e -> {
            String groupName = addToGroupCombo.getValue();
            if (groupName != null) {
                IDevice device = findDeviceById(model.getId());
                if (device != null) {
                    addDeviceToGroup(groupName, device);
                    logActivity("Added " + model.getName() + " to group " + groupName);
                    updateDeviceList();
                }
            }
        });
        
        // Remove from group button - always visible but enabled/disabled based on group membership
        String currentGroup = findDeviceGroup(model.getId());
        Button removeFromGroupBtn = new Button("Remove from Group");
        removeFromGroupBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 3;");
        
        // Set button state based on current group membership
        if (currentGroup != null) {
            removeFromGroupBtn.setText("Remove from " + currentGroup);
            removeFromGroupBtn.setDisable(false); // Enable if in a group
        } else {
            removeFromGroupBtn.setText("Remove from Group");
            removeFromGroupBtn.setDisable(true); // Disable if not in a group
        }
        
        removeFromGroupBtn.setOnAction(e -> {
            removeDeviceFromGroup(model.getId());
            // Clear dropdown selection to allow re-adding to a group
            updateDeviceList();
        });
        
        groupBox.getChildren().addAll(addToGroupLabel, addToGroupCombo, removeFromGroupBtn);
        
        // State management buttons
        HBox stateBox = new HBox(6);
        stateBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Button saveStateBtn = new Button("Save State");
        saveStateBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 4 8; -fx-background-radius: 3;");
        saveStateBtn.setOnAction(e -> handleSaveDeviceState(model));
        
        Button restoreStateBtn = new Button("Restore State");
        restoreStateBtn.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 4 8; -fx-background-radius: 3;");
        restoreStateBtn.setOnAction(e -> handleRestoreDeviceState(model));
        
        // Delete device button
        Button deleteDeviceBtn = new Button("🚮 Delete");
        deleteDeviceBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 4 8; -fx-background-radius: 3; -fx-font-weight: bold;");
        deleteDeviceBtn.setOnAction(e -> {
            // Show confirmation dialog before deletion
            javafx.scene.control.Alert confirmDialog = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Delete Device");
            confirmDialog.setHeaderText("Delete " + model.getName() + "?");
            confirmDialog.setContentText("This will permanently remove the device and all its associations. This action cannot be undone.");
            
            java.util.Optional<javafx.scene.control.ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                handleDeleteDevice(model.getId());
            }
        });
        
        stateBox.getChildren().addAll(saveStateBtn, restoreStateBtn, deleteDeviceBtn);
        
        // Add spacer between group controls and state buttons
        Region actionSpacer = new Region();
        HBox.setHgrow(actionSpacer, Priority.ALWAYS);
        
        actionBox.getChildren().addAll(groupBox, actionSpacer, stateBox);
        
        container.getChildren().addAll(headerBox, deviceControls, actionBox);
        return container;
    }
    

    
    private void toggleDevice(DeviceViewModel model) {
        IDevice device = findDeviceById(model.getId());
        if (device instanceof Light) {
            LightReceiver receiver = getLightReceiver(model.getId());
            if (receiver != null) {
                if (model.isOn()) {
                    executeCommand(new BasicCommands.TurnOffCommand(receiver));
                } else {
                    executeCommand(new BasicCommands.TurnOnCommand(receiver));
                }
            }
        } else if (device instanceof Thermostat) {
            ThermostatReceiver receiver = getThermostatReceiver(model.getId());
            if (receiver != null) {
                if (model.isOn()) {
                    executeCommand(new BasicCommands.TurnOffCommand(receiver));
                } else {
                    executeCommand(new BasicCommands.TurnOnCommand(receiver));
                }
            }
        }
    }
    

    

    
    /**
     * Create integrated light controls for device list items
     */
    private VBox createIntegratedLightControls(DeviceViewModel model) {
        VBox controls = new VBox(6);
        
        IDevice device = findDeviceById(model.getId());
        if (!(device instanceof Light)) return controls;
        Light light = (Light) device;
        
        // Brightness control
        HBox brightnessBox = new HBox(8);
        brightnessBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label brightnessLabel = new Label("Brightness:");
        brightnessLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: black;");
        Slider brightnessSlider = new Slider(0, 100, light.getBrightness());
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setShowTickMarks(true);
        brightnessSlider.setMajorTickUnit(25);
        brightnessSlider.setMinorTickCount(4);
        brightnessSlider.setPrefWidth(120);
        Label brightnessValue = new Label(light.getBrightness() + "%");
        brightnessValue.setStyle("-fx-font-size: 10px; -fx-min-width: 30; -fx-text-fill: black;");
        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                brightnessValue.setText((int) newVal.doubleValue() + "%"));
        brightnessSlider.setOnMouseReleased(e -> {
            int brightness = (int) brightnessSlider.getValue();
            LightReceiver receiver = getLightReceiver(model.getId());
            if (receiver != null) {
                executeCommand(new LightCommands.SetBrightnessCommand(receiver, brightness));
            }
        });
        brightnessBox.getChildren().addAll(brightnessLabel, brightnessSlider, brightnessValue);
        
        // Color control
        HBox colorBox = new HBox(8);
        colorBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label colorLabel = new Label("Color:");
        colorLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: black;");
        ComboBox<LightColor> colorCombo = new ComboBox<>(FXCollections.observableArrayList(LightColor.values()));
        colorCombo.setValue(light.getColor());
        colorCombo.setStyle("-fx-font-size: 10px; -fx-text-fill: black;");
        colorCombo.setOnAction(e -> {
            LightColor color = colorCombo.getValue();
            if (color != null) {
                LightReceiver receiver = getLightReceiver(model.getId());
                if (receiver != null) {
                    executeCommand(new LightCommands.SetColorCommand(receiver, color.toString()));
                }
            }
        });
        colorBox.getChildren().addAll(colorLabel, colorCombo);
        
        controls.getChildren().addAll(brightnessBox, colorBox);
        return controls;
    }
    
    /**
     * Create integrated thermostat controls for device list items
     */
    private VBox createIntegratedThermostatControls(DeviceViewModel model) {
        VBox controls = new VBox(6);
        
        IDevice device = findDeviceById(model.getId());
        if (!(device instanceof Thermostat)) return controls;
        Thermostat thermostat = (Thermostat) device;
        
        // Temperature control
        HBox tempBox = new HBox(8);
        tempBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label tempLabel = new Label("Target:");
        tempLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: black;");
        Spinner<Double> tempSpinner = new Spinner<>(50.0, 90.0, thermostat.getTargetTemperature(), 1.0);
        tempSpinner.setEditable(true);
        tempSpinner.setPrefWidth(80);
        tempSpinner.setStyle("-fx-font-size: 10px; -fx-text-fill: black;");
        tempSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ThermostatReceiver receiver = getThermostatReceiver(model.getId());
                if (receiver != null) {
                    executeCommand(new ThermostatCommands.SetTargetTemperatureCommand(receiver, newVal));
                }
            }
        });
        Label tempUnit = new Label("°F");
        tempUnit.setStyle("-fx-font-size: 10px; -fx-text-fill: black;");
        tempBox.getChildren().addAll(tempLabel, tempSpinner, tempUnit);
        
        // Mode control
        HBox modeBox = new HBox(8);
        modeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label modeLabel = new Label("Mode:");
        modeLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: black;");
        ComboBox<ThermostatMode> modeCombo = new ComboBox<>(FXCollections.observableArrayList(ThermostatMode.values()));
        modeCombo.setValue(thermostat.getMode());
        modeCombo.setStyle("-fx-font-size: 10px; -fx-text-fill: black;");
        modeCombo.setOnAction(e -> {
            ThermostatMode mode = modeCombo.getValue();
            if (mode != null) {
                ThermostatReceiver receiver = getThermostatReceiver(model.getId());
                if (receiver != null) {
                    executeCommand(new ThermostatCommands.SetModeCommand(receiver, mode.toString()));
                }
            }
        });
        modeBox.getChildren().addAll(modeLabel, modeCombo);
        
        controls.getChildren().addAll(tempBox, modeBox);
        return controls;
    }
    
    // Enhanced Automation UI Setup
    private void setupAutomationUI() {
        automationRuleContainer.getChildren().clear();
        
        // Title
        Label title = new Label("Automation Rules");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-padding: 0 0 10 0;");
        
        // Create New Rule Button
        Button createRuleBtn = new Button("+ Create Rule");
        createRuleBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 100;");
        createRuleBtn.setOnAction(e -> showCreateRuleDialog());
        
        // Rules List
        Label rulesLabel = new Label("Active Rules:");
        rulesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 10 0 5 0;");
        
        ListView<String> rulesListView = new ListView<>(ruleNames);
        rulesListView.setPrefHeight(80);
        rulesListView.setStyle("-fx-font-size: 11px;");
        
        // Rule management buttons
        HBox ruleButtonsBox = new HBox(5);
        Button deleteRuleBtn = new Button("Delete");
        deleteRuleBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px;");
        deleteRuleBtn.setOnAction(e -> {
            String selectedRule = rulesListView.getSelectionModel().getSelectedItem();
            if (selectedRule != null) {
                deleteAutomationRule(selectedRule);
            }
        });
        
        Button viewRuleBtn = new Button("View");
        viewRuleBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 10px;");
        viewRuleBtn.setOnAction(e -> {
            String selectedRule = rulesListView.getSelectionModel().getSelectedItem();
            if (selectedRule != null) {
                viewAutomationRule(selectedRule);
            }
        });
        
        ruleButtonsBox.getChildren().addAll(deleteRuleBtn, viewRuleBtn);
        
        automationRuleContainer.getChildren().addAll(title, createRuleBtn, rulesLabel, rulesListView, ruleButtonsBox);
    }
    
    private void showCreateRuleDialog() {
        if (deviceModels.isEmpty()) {
            logActivity("No devices available. Add some devices first to create automation rules.");
            return;
        }
        
        // Create a new stage for the rule creation dialog
        javafx.stage.Stage dialog = new javafx.stage.Stage();
        dialog.setTitle("Create Automation Rule");
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        
        VBox dialogContent = new VBox(15);
        dialogContent.setStyle("-fx-padding: 20; -fx-background-color: white;");
        
        // Rule Name
        Label nameLabel = new Label("Rule Name:");
        nameLabel.setStyle("-fx-font-weight: bold;");
        TextField ruleNameField = new TextField();
        ruleNameField.setPromptText("Enter rule name (optional)");
        
        // Trigger Section
        Label triggerLabel = new Label("WHEN (Trigger):");
        triggerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        HBox triggerBox = new HBox(10);
        triggerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        ComboBox<String> triggerDeviceCombo = new ComboBox<>();
        for (DeviceViewModel model : deviceModels) {
            triggerDeviceCombo.getItems().add(model.getName());
        }
        triggerDeviceCombo.setPromptText("Select trigger device");
        
        Label whenLabel = new Label("turns");
        ComboBox<String> triggerStateCombo = new ComboBox<>();
        triggerStateCombo.getItems().addAll("ON", "OFF");
        triggerStateCombo.setPromptText("Select state");
        
        triggerBox.getChildren().addAll(triggerDeviceCombo, whenLabel, triggerStateCombo);
        
        // Action Section
        Label actionLabel = new Label("THEN (Action):");
        actionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        ComboBox<String> actionCombo = new ComboBox<>();
        actionCombo.getItems().addAll("Turn On", "Turn Off", "Toggle");
        actionCombo.setPromptText("Select action");
        
        ComboBox<String> targetDeviceCombo = new ComboBox<>();
        for (DeviceViewModel model : deviceModels) {
            targetDeviceCombo.getItems().add(model.getName());
        }
        targetDeviceCombo.setPromptText("Select target device");
        
        actionBox.getChildren().addAll(actionCombo, targetDeviceCombo);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button createBtn = new Button("Create Rule");
        createBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        createBtn.setOnAction(e -> {
            if (validateAndCreateRule(ruleNameField.getText(), 
                                   triggerDeviceCombo.getValue(),
                                   triggerStateCombo.getValue(),
                                   actionCombo.getValue(),
                                   targetDeviceCombo.getValue())) {
                dialog.close();
            }
        });
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        cancelBtn.setOnAction(e -> dialog.close());
        
        buttonBox.getChildren().addAll(createBtn, cancelBtn);
        
        dialogContent.getChildren().addAll(
            nameLabel, ruleNameField,
            new javafx.scene.control.Separator(),
            triggerLabel, triggerBox,
            new javafx.scene.control.Separator(), 
            actionLabel, actionBox,
            new javafx.scene.control.Separator(),
            buttonBox
        );
        
        javafx.scene.Scene dialogScene = new javafx.scene.Scene(dialogContent, 400, 350);
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.show();
    }
    
    private boolean validateAndCreateRule(String ruleName, String triggerDeviceName, 
                                        String triggerState, String action, String targetDeviceName) {
        // Validation
        if (triggerDeviceName == null || triggerState == null || action == null || targetDeviceName == null) {
            logActivity("Please fill in all fields to create the automation rule");
            return false;
        }
        
        if (triggerDeviceName.equals(targetDeviceName)) {
            logActivity("Trigger and target device cannot be the same");
            return false;
        }
        
        // Find devices
        IDevice triggerDevice = findDeviceByName(triggerDeviceName);
        IDevice targetDevice = findDeviceByName(targetDeviceName);
        
        if (triggerDevice == null || targetDevice == null) {
            logActivity("Could not find the selected devices");
            return false;
        }
        
        // Create rule name if not provided
        if (ruleName == null || ruleName.trim().isEmpty()) {
            ruleName = "Rule " + (++ruleCounter);
        }
        
        // Create the automation rule
        RuleBuilder ruleBuilder = new RuleBuilder();
        AutomationRule rule = ruleBuilder
                .named(ruleName)
                .when(triggerDevice, triggerState)
                .then(action.toLowerCase(), targetDevice)
                .build();
        
        // Add to engine and UI
        automationEngine.addRule(rule);
        automationRules.put(ruleName, rule);
        ruleNames.add(ruleName);
        
        logActivity("Created automation rule: " + rule.toString());
        updateStatus("Automation rule '" + ruleName + "' created successfully");
        
        return true;
    }
    
    private void deleteAutomationRule(String ruleName) {
        AutomationRule rule = automationRules.get(ruleName);
        if (rule != null) {
            automationRules.remove(ruleName);
            ruleNames.remove(ruleName);
            // Note: AutomationEngine doesn't have a remove method, so we'd need to clear and re-add
            automationEngine.clearRules();
            for (AutomationRule remainingRule : automationRules.values()) {
                automationEngine.addRule(remainingRule);
            }
            logActivity("Deleted automation rule: " + ruleName);
            updateStatus("Rule deleted");
        }
    }
    
    private void viewAutomationRule(String ruleName) {
        AutomationRule rule = automationRules.get(ruleName);
        if (rule != null) {
            logActivity("Rule Details: " + rule.toString());
        }
    }
    
    private IDevice findDeviceByName(String deviceName) {
        return facade.getAllDevices().stream()
                .filter(device -> device.getName().equals(deviceName))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get or create a command receiver for a device to enable undo/redo functionality
     */
    private LightReceiver getLightReceiver(String deviceId) {
        return lightReceivers.computeIfAbsent(deviceId, id -> {
            IDevice device = findDeviceById(id);
            if (device instanceof Light) {
                return new LightReceiver((Light) device);
            }
            return null;
        });
    }
    
    private ThermostatReceiver getThermostatReceiver(String deviceId) {
        return thermostatReceivers.computeIfAbsent(deviceId, id -> {
            IDevice device = findDeviceById(id);
            if (device instanceof Thermostat) {
                return new ThermostatReceiver((Thermostat) device);
            }
            return null;
        });
    }
    
    /**
     * Execute a command through the command manager for undo/redo support
     */
    public void executeCommand(DeviceCommand command) {
        if (command != null) {
            String result = commandManager.executeCommand(command);
            logActivity("Command: " + result);
        }
    }
    
    // Scenes UI
    private void setupSceneUI() {
        sceneContainer.getChildren().clear();
        Label title = new Label("Quick Scenes");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
        Button eveningScene = new Button("Evening Relax");
        eveningScene.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-weight: bold;");
        eveningScene.setOnAction(e -> applyEveningScene());
        Button movieScene = new Button("Movie Night");
        movieScene.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white; -fx-font-weight: bold;");
        movieScene.setOnAction(e -> applyMovieScene());
        sceneContainer.getChildren().addAll(title, eveningScene, movieScene);
    }
    
    private void applyEveningScene() {
        String selectedGroup = sceneGroupSelector != null ? sceneGroupSelector.getValue() : null;
        if (selectedGroup == null || !deviceGroups.containsKey(selectedGroup)) {
            logActivity("Select a group to apply the scene");
            return;
        }
        DeviceGroup group = deviceGroups.get(selectedGroup);
        
        // Create scene for tracking purposes
        Scene eveningScene = new Scene.Builder("Evening Relax")
                .lightsOn(true)
                .lightBrightness(40)
                .lightColor(LightColor.WARM_WHITE)
                .thermostatOn(true)
                .thermostatTarget(72.0)
                .thermostatMode(ThermostatMode.HEAT)
                .build();
        
        // Apply scene using decorator with command support
        GroupSceneDecorator decoratedGroup = new GroupSceneDecorator(group);
        decoratedGroup.setCommandExecutionDependencies(this, this);
        decoratedGroup.applyScene(eveningScene);
        
        scenes.put("Evening Relax", eveningScene);
        logActivity("Applied Evening Relax scene to " + selectedGroup);
        updateStatus("Evening Relax scene activated");
    }
    
    private void applyMovieScene() {
        String selectedGroup = sceneGroupSelector != null ? sceneGroupSelector.getValue() : null;
        if (selectedGroup == null || !deviceGroups.containsKey(selectedGroup)) {
            logActivity("Select a group to apply the scene");
            return;
        }
        DeviceGroup group = deviceGroups.get(selectedGroup);
        
        // Create scene for tracking purposes
        Scene movieScene = new Scene.Builder("Movie Night")
                .lightsOff(true)
                .thermostatOn(true)
                .thermostatTarget(68.0)
                .thermostatMode(ThermostatMode.COOL)
                .build();
        
        // Apply scene using decorator with command support
        GroupSceneDecorator decoratedGroup = new GroupSceneDecorator(group);
        decoratedGroup.setCommandExecutionDependencies(this, this);
        decoratedGroup.applyScene(movieScene);
        
        scenes.put("Movie Night", movieScene);
        logActivity("Applied Movie Night scene to " + selectedGroup);
        updateStatus("Movie Night scene activated");
    }
    
    // DeviceCommandFactory interface implementation
    @Override
    public DeviceCommand createTurnOnCommand(IDevice device) {
        if (device instanceof Light) {
            LightReceiver receiver = getLightReceiver(device.getId());
            return receiver != null ? new BasicCommands.TurnOnCommand(receiver) : null;
        } else if (device instanceof Thermostat) {
            ThermostatReceiver receiver = getThermostatReceiver(device.getId());
            return receiver != null ? new BasicCommands.TurnOnCommand(receiver) : null;
        }
        return null;
    }

    @Override
    public DeviceCommand createTurnOffCommand(IDevice device) {
        if (device instanceof Light) {
            LightReceiver receiver = getLightReceiver(device.getId());
            return receiver != null ? new BasicCommands.TurnOffCommand(receiver) : null;
        } else if (device instanceof Thermostat) {
            ThermostatReceiver receiver = getThermostatReceiver(device.getId());
            return receiver != null ? new BasicCommands.TurnOffCommand(receiver) : null;
        }
        return null;
    }

    @Override
    public DeviceCommand createSetBrightnessCommand(Light light, Integer brightness) {
        LightReceiver lightReceiver = getLightReceiver(light.getId());
        return lightReceiver != null ? new LightCommands.SetBrightnessCommand(lightReceiver, brightness) : null;
    }

    @Override
    public DeviceCommand createSetColorCommand(Light light, LightColor color) {
        LightReceiver lightReceiver = getLightReceiver(light.getId());
        return lightReceiver != null ? new LightCommands.SetColorCommand(lightReceiver, color.toString()) : null;
    }

    @Override
    public DeviceCommand createSetTargetTemperatureCommand(Thermostat thermostat, Double temperature) {
        ThermostatReceiver thermostatReceiver = getThermostatReceiver(thermostat.getId());
        return thermostatReceiver != null ? new ThermostatCommands.SetTargetTemperatureCommand(thermostatReceiver, temperature) : null;
    }

    @Override
    public DeviceCommand createSetModeCommand(Thermostat thermostat, ThermostatMode mode) {
        ThermostatReceiver thermostatReceiver = getThermostatReceiver(thermostat.getId());
        return thermostatReceiver != null ? new ThermostatCommands.SetModeCommand(thermostatReceiver, mode.toString()) : null;
    }
    
    // Groups
    @FXML
    private void handleCreateGroup() {
        String groupName = "Group " + (++groupCounter);
        DeviceGroup group = new DeviceGroup("group_" + groupCounter, groupName);
        deviceGroups.put(groupName, group);
        groupNames.add(groupName);
        logActivity("Created new group: " + groupName);
        updateStatus("Group created");
    }
    
    /**
     * Delete a group completely from the system
     * Removes all devices from the group first, then deletes the group
     */
    private void handleDeleteGroup(String groupName) {
        DeviceGroup group = deviceGroups.get(groupName);
        if (group == null) {
            logActivity("Group not found: " + groupName);
            updateStatus("Group not found");
            return;
        }
        
        // 1. Remove all devices from the group (but keep devices in system)
        java.util.List<IDeviceComponent> devicesInGroup = new java.util.ArrayList<>(group.getChildren());
        for (IDeviceComponent component : devicesInGroup) {
            group.removeComponent(component);
            if (component instanceof DeviceAdapter) {
                DeviceAdapter adapter = (DeviceAdapter) component;
                logActivity("Removed " + adapter.getDevice().getName() + " from group " + groupName);
            }
        }
        
        // 2. Remove group from deviceGroups map
        deviceGroups.remove(groupName);
        
        // 3. Remove group name from UI list
        groupNames.remove(groupName);
        
        // 4. Clean up any scene selectors that might reference this group
        if (sceneGroupSelector != null && groupName.equals(sceneGroupSelector.getValue())) {
            sceneGroupSelector.setValue(null);
        }
        
        // 5. Update device list to refresh group assignments
        updateDeviceList();
        
        // 6. Log and update status
        logActivity("Deleted group: " + groupName + " (removed " + devicesInGroup.size() + " devices from group)");
        updateStatus("Group deleted: " + groupName);
    }
    
    /**
     * Setup custom cell factory for group ListView to include delete buttons
     */
    private void setupGroupListCellFactory() {
        groupListView.setCellFactory(listView -> new javafx.scene.control.ListCell<String>() {
            private final HBox container = new HBox(8);
            private final Label groupLabel = new Label();
            private final Button deleteButton = new Button("Delete");
            
            {
                // Style the container
                container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                container.setStyle("-fx-padding: 4 8;");
                
                // Style the group label
                groupLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50;");
                HBox.setHgrow(groupLabel, javafx.scene.layout.Priority.ALWAYS);
                
                // Add spacer to push delete button to far right
                javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                
                // Style the delete button with increased width for full text visibility
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px; " +
                                    "-fx-padding: 4 8; -fx-background-radius: 3; -fx-min-width: 50; -fx-pref-width: 50;");
                deleteButton.setTooltip(new javafx.scene.control.Tooltip("Delete this group"));
                
                // Add click handler for delete button
                deleteButton.setOnAction(e -> {
                    String groupName = getItem();
                    if (groupName != null) {
                        // Show confirmation dialog
                        javafx.scene.control.Alert confirmDialog = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                        confirmDialog.setTitle("Delete Group");
                        confirmDialog.setHeaderText("Delete group '" + groupName + "'?");
                        confirmDialog.setContentText("This will remove all devices from the group and delete the group. Devices will remain in the system.");
                        
                        java.util.Optional<javafx.scene.control.ButtonType> result = confirmDialog.showAndWait();
                        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                            handleDeleteGroup(groupName);
                        }
                    }
                });
                
                container.getChildren().addAll(groupLabel, spacer, deleteButton);
            }
            
            @Override
            protected void updateItem(String groupName, boolean empty) {
                super.updateItem(groupName, empty);
                
                if (empty || groupName == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    groupLabel.setText(groupName);
                    setGraphic(container);
                    setText(null);
                }
            }
        });
    }
    
    private void addDeviceToGroup(String groupName, IDevice device) {
        DeviceGroup group = deviceGroups.get(groupName);
        if (group == null) return;
        group.addComponent(new DeviceAdapter(device));
        // No UI list reset here; group list content didn't change
    }
    
    private IDevice findDeviceById(String deviceId) {
        return facade.getAllDevices().stream()
                .filter(device -> device.getId().equals(deviceId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Find which group a device belongs to
     */
    private String findDeviceGroup(String deviceId) {
        for (Map.Entry<String, DeviceGroup> entry : deviceGroups.entrySet()) {
            DeviceGroup group = entry.getValue();
            if (group.getChildren().stream()
                    .anyMatch(component -> component instanceof DeviceAdapter && 
                             ((DeviceAdapter) component).getDevice().getId().equals(deviceId))) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Remove a device from its current group
     */
    private void removeDeviceFromGroup(String deviceId) {
        String currentGroupName = findDeviceGroup(deviceId);
        if (currentGroupName == null) {
            logActivity("Device " + deviceId + " is not in any group");
            return;
        }
        
        DeviceGroup group = deviceGroups.get(currentGroupName);
        if (group != null) {
            // Find the device adapter component to remove
            IDeviceComponent componentToRemove = null;
            for (IDeviceComponent component : group.getChildren()) {
                if (component instanceof DeviceAdapter && 
                    ((DeviceAdapter) component).getDevice().getId().equals(deviceId)) {
                    componentToRemove = component;
                    break;
                }
            }
            
            if (componentToRemove != null) {
                // Use the proper removeComponent method
                group.removeComponent(componentToRemove);
                
                IDevice device = findDeviceById(deviceId);
                if (device != null) {
                    logActivity("Removed " + device.getName() + " from group " + currentGroupName);
                    updateStatus("Device removed from group " + currentGroupName);
                }
            } else {
                logActivity("Failed to find device " + deviceId + " in group " + currentGroupName);
            }
        }
    }
    
    private void handleUIUpdate(UIUpdateObserver.UIUpdate update) {
        Platform.runLater(() -> {
            for (DeviceViewModel model : deviceModels) {
                if (model.getId().equals(update.getDeviceId())) {
                    IDevice device = facade.getAllDevices().stream()
                            .filter(d -> d.getId().equals(update.getDeviceId()))
                            .findFirst()
                            .orElse(null);
                    if (device != null) {
                        model.updateFromDevice(device);
                        
                        // Trigger automation rules on device state changes (only for main state changes, not properties)
                        if (update.getPropertyName() == null || update.getPropertyName().isEmpty()) {
                            automationEngine.processStateChange(device, update.getNewValue().toString());
                        }
                    }
                    break;
                }
            }
            updateDeviceList();
            logActivity(update.getDeviceName() + ": " + update.getOldValue() + " → " + update.getNewValue());
            if (uiUpdateCallback != null) uiUpdateCallback.accept(update);
        });
    }
    
    @FXML
    private void handleAddLight() {
        IDevice newDevice = facade.createDevice(DeviceType.LIGHT, "Light " + (deviceModels.size() + 1));
        newDevice.addObserver(uiObserver);
        
        // Initialize receiver for command pattern support
        if (newDevice instanceof Light) {
            lightReceivers.put(newDevice.getId(), new LightReceiver((Light) newDevice));
        }
        
        DeviceViewModel newModel = new DeviceViewModel(newDevice);
        deviceModels.add(newModel);
        updateDeviceList();
        updateStatus("Added new light");
        logActivity("Added new light: " + newDevice.getName());
    }
    
    @FXML
    private void handleAddThermostat() {
        IDevice newDevice = facade.createDevice(DeviceType.THERMOSTAT, "Thermostat " + (deviceModels.size() + 1));
        newDevice.addObserver(uiObserver);
        
        // Initialize receiver for command pattern support
        if (newDevice instanceof Thermostat) {
            thermostatReceivers.put(newDevice.getId(), new ThermostatReceiver((Thermostat) newDevice));
        }
        
        DeviceViewModel newModel = new DeviceViewModel(newDevice);
        deviceModels.add(newModel);
        updateDeviceList();
        updateStatus("Added new thermostat");
        logActivity("Added new thermostat: " + newDevice.getName());
    }
    
    /**
     * Delete a device completely from the system
     * Removes device from registry, groups, UI, and cleans up all associations
     */
    private void handleDeleteDevice(String deviceId) {
        // Find the device first
        IDevice deviceToDelete = findDeviceById(deviceId);
        if (deviceToDelete == null) {
            logActivity("Device not found: " + deviceId);
            updateStatus("Device not found");
            return;
        }
        
        String deviceName = deviceToDelete.getName();
        
        // 1. Remove from any group first
        removeDeviceFromGroup(deviceId);
        
        // 2. Remove UI observer
        deviceToDelete.removeObserver(uiObserver);
        
        // 3. Remove command receivers for undo/redo functionality
        lightReceivers.remove(deviceId);
        thermostatReceivers.remove(deviceId);
        
        // 4. Remove from saved states (memento pattern)
        savedStates.remove(deviceId);
        
        // 5. Remove from device models list (UI)
        deviceModels.removeIf(model -> model.getId().equals(deviceId));
        
        // 6. Remove from device registry (core system)
        facade.deleteDevice(deviceId);
        
        // 7. Clean up any automation rules that reference this device
        cleanupAutomationRulesForDevice(deviceId, deviceName);
        
        // 8. Update UI and log
        updateDeviceList();
        updateStatus("Device deleted: " + deviceName);
        logActivity("Deleted device: " + deviceName + " (ID: " + deviceId + ")");
    }
    
    /**
     * Clean up automation rules that reference a deleted device
     */
    private void cleanupAutomationRulesForDevice(String deviceId, String deviceName) {
        // Find and remove automation rules that reference this device
        java.util.List<String> rulesToRemove = new java.util.ArrayList<>();
        
        for (Map.Entry<String, AutomationRule> entry : automationRules.entrySet()) {
            AutomationRule rule = entry.getValue();
            // Check if rule references the deleted device (this is a simplified check)
            // In a real implementation, you'd need to check the rule's trigger and target devices
            String ruleString = rule.toString().toLowerCase();
            if (ruleString.contains(deviceName.toLowerCase()) || ruleString.contains(deviceId)) {
                rulesToRemove.add(entry.getKey());
            }
        }
        
        // Remove the identified rules
        for (String ruleName : rulesToRemove) {
            deleteAutomationRule(ruleName);
            logActivity("Removed automation rule '" + ruleName + "' due to device deletion");
        }
    }
    
    @FXML
    private void handleSaveState() {
        if (selectedDevice != null) {
            IDevice device = findDeviceById(selectedDevice.getId());
            if (device != null) {
                DeviceMemento memento = null;
                
                // Use the appropriate receiver to save complete state
                if (device instanceof Light) {
                    LightReceiver receiver = getLightReceiver(selectedDevice.getId());
                    if (receiver != null) {
                        memento = receiver.saveState();
                    }
                } else if (device instanceof Thermostat) {
                    ThermostatReceiver receiver = getThermostatReceiver(selectedDevice.getId());
                    if (receiver != null) {
                        memento = receiver.saveState();
                    }
                }
                
                if (memento != null) {
                    savedStates.put(selectedDevice.getId(), memento);
                    logActivity("Saved complete state for " + selectedDevice.getName());
                    updateStatus("Device state saved");
                } else {
                    logActivity("Failed to save state for " + selectedDevice.getName());
                    updateStatus("Failed to save device state");
                }
            }
        } else {
            logActivity("Please select a device first");
        }
    }
    
    @FXML
    private void handleRestoreState() {
        if (selectedDevice != null && savedStates.containsKey(selectedDevice.getId())) {
            IDevice device = findDeviceById(selectedDevice.getId());
            if (device != null) {
                DeviceMemento memento = savedStates.get(selectedDevice.getId());
                boolean restored = false;
                
                // Use the appropriate receiver to restore complete state
                if (device instanceof Light) {
                    LightReceiver receiver = getLightReceiver(selectedDevice.getId());
                    if (receiver != null) {
                        receiver.restoreState(memento);
                        restored = true;
                    }
                } else if (device instanceof Thermostat) {
                    ThermostatReceiver receiver = getThermostatReceiver(selectedDevice.getId());
                    if (receiver != null) {
                        receiver.restoreState(memento);
                        restored = true;
                    }
                }
                
                if (restored) {
                    logActivity("Restored complete state for " + selectedDevice.getName());
                    updateStatus("Device state restored");
                } else {
                    logActivity("Failed to restore state for " + selectedDevice.getName());
                    updateStatus("Failed to restore device state");
                }
            }
        } else {
            logActivity("No saved state found for selected device");
        }
    }
    
    @FXML
    private void handleUndo() {
        String result = commandManager.undo();
        logActivity("Undo: " + result);
        updateStatus("Action undone");
    }
    
    @FXML
    private void handleRedo() {
        String result = commandManager.redo();
        logActivity("Redo: " + result);
        updateStatus("Action redone");
    }
    
    private void handleSaveDeviceState(DeviceViewModel model) {
        IDevice device = findDeviceById(model.getId());
        if (device != null) {
            DeviceMemento memento = null;
            
            // Use the appropriate receiver to save complete state
            if (device instanceof Light) {
                LightReceiver receiver = getLightReceiver(model.getId());
                if (receiver != null) {
                    memento = receiver.saveState();
                }
            } else if (device instanceof Thermostat) {
                ThermostatReceiver receiver = getThermostatReceiver(model.getId());
                if (receiver != null) {
                    memento = receiver.saveState();
                }
            }
            
            if (memento != null) {
                savedStates.put(model.getId(), memento);
                logActivity("Saved complete state for " + model.getName());
                updateStatus("Device state saved for " + model.getName());
            } else {
                logActivity("Failed to save state for " + model.getName());
                updateStatus("Failed to save state for " + model.getName());
            }
        }
    }
    
    private void handleRestoreDeviceState(DeviceViewModel model) {
        if (savedStates.containsKey(model.getId())) {
            IDevice device = findDeviceById(model.getId());
            if (device != null) {
                DeviceMemento memento = savedStates.get(model.getId());
                boolean restored = false;
                
                // Use the appropriate receiver to restore complete state
                if (device instanceof Light) {
                    LightReceiver receiver = getLightReceiver(model.getId());
                    if (receiver != null) {
                        receiver.restoreState(memento);
                        restored = true;
                    }
                } else if (device instanceof Thermostat) {
                    ThermostatReceiver receiver = getThermostatReceiver(model.getId());
                    if (receiver != null) {
                        receiver.restoreState(memento);
                        restored = true;
                    }
                }
                
                if (restored) {
                    logActivity("Restored complete state for " + model.getName());
                    updateStatus("Device state restored for " + model.getName());
                } else {
                    logActivity("Failed to restore state for " + model.getName());
                    updateStatus("Failed to restore state for " + model.getName());
                }
            }
        } else {
            logActivity("No saved state found for " + model.getName());
            updateStatus("No saved state available for " + model.getName());
        }
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    private void logActivity(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        systemLogArea.appendText("[" + timestamp + "] " + message + "\n");
    }
}