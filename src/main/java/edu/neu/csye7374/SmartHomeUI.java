package edu.neu.csye7374;

import edu.neu.csye7374.core.facade.UIFacade;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Natural Smart Home Application
 * Starts with an empty home for users to build their smart home system
 */
public class SmartHomeUI extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize the smart home system with an empty home
        initializeSmartHomeSystem();
        
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(
            SmartHomeUI.class.getResource("/edu/neu/csye7374/userInterface/SmartHomeUI.fxml")
        );
        Parent root = loader.load();
        
        // Set up the stage
        primaryStage.setTitle("Smart Home Control Center");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }
    
    private void initializeSmartHomeSystem() {
        UIFacade facade = UIFacade.getInstance();
        
        // Start with an empty home - no pre-initialized devices
        // Users will add devices through the UI
        
        System.out.println("Smart Home System initialized - Empty home ready for setup");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}