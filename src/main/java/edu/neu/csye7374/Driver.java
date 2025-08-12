package edu.neu.csye7374;

import javafx.application.Application;

/**
 * 
 * @author Yash Zaveri
 * 
 */

public class Driver {
	public static void main(String[] args) {
		System.out.println("============Main Execution Start===================\n\n");

         //Add your code in between these two print statements
		 
		// Check if UI mode is requested
        if (args.length > 0 && args[0].equals("--ui")) {
            // Launch JavaFX UI using Application.launch()
            Application.launch(SmartHomeUI.class, args);
        } else {
            // Run console demo
            Demo.demonstrateDesignPatterns();
        }
		 
		System.out.println("\n\n============Main Execution End===================");
	}

}
