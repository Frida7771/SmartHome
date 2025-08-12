[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/vN5dLYMT)
# DesignPatterns
# Smart Home Automation System

A comprehensive Java-based smart home automation system that demonstrates the implementation of multiple design patterns. This project provides a flexible, extensible architecture for home automation with support for different device types, automation rules, scenes, and both console and graphical user interfaces.

## Features

- **Device Management**: Add, control, and remove smart devices (lights, thermostats)
- **Device Groups**: Create and manage groups of devices that can be controlled together
- **Scenes**: Apply predefined settings to multiple devices at once
- **Automation Rules**: Create trigger-based automation rules
- **Command History**: Support for undo/redo functionality
- **State Persistence**: Save and restore device states

## Design Patterns Implemented

This project demonstrates the implementation of numerous design patterns:

- **Creational Patterns**: Factory, Builder, Prototype, Singleton
- **Structural Patterns**: Adapter, Composite, Decorator, Facade, Flyweight
- **Behavioral Patterns**: Command, Observer, State, Strategy, Template Method, Memento
- **Architectural Patterns**: MVC (Model-View-Controller)

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- JavaFX (included in Maven dependencies)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/CSYE7374-Design-Patterns-SummerFull2025/final-project-milestone-1-group-1
cd final-project-milestone-1-group-1
```

### Build the Project

```bash
mvn clean install
```

### Run the Application

#### Console Mode

To run the application in console mode (demonstrating design patterns):

```bash
mvn exec:java -Dexec.mainClass="edu.neu.csye7374.Driver"
```

#### GUI Mode

To run the application with the graphical user interface:

```bash
mvn exec:java -Dexec.mainClass="edu.neu.csye7374.Driver" -Dexec.args="--ui"
```

Alternatively, you can use:

```bash
mvn javafx:run
```

## Using the Application

### GUI Mode

1. **Add Devices**:
   - Click "Add Light" or "Add Thermostat" buttons to create new devices

2. **Control Devices**:
   - Toggle power: Use the "Turn On" / "Turn Off" button for each device
   - Adjust light brightness: Use the brightness slider
   - Change light color: Select from the color dropdown
   - Set thermostat temperature: Use the temperature spinner
   - Change thermostat mode: Select from the mode dropdown

3. **Create Groups**:
   - Click "Create Group" to create a new device group
   - Add devices to groups using the dropdown in each device card

4. **Apply Scenes**:
   - Select a group from the dropdown in the Scenes section
   - Click "Evening Relax" or "Movie Night" to apply predefined scenes

5. **Create Automation Rules**:
   - Click "Create Rule" in the Automation Rules section
   - Define trigger conditions and actions
   - Rules will execute automatically when conditions are met

6. **Undo/Redo Actions**:
   - Use the "Undo" and "Redo" buttons to reverse or reapply changes

7. **Save/Restore Device States**:
   - Use "Save State" and "Restore State" buttons for each device

### Console Mode

The console mode demonstrates all implemented design patterns with detailed output showing the behavior of each pattern.

## Project Structure

- `src/main/java/edu/neu/csye7374/`
  - `core/`: Core interfaces and abstract classes
    - `builder/`: Builder pattern implementations
    - `command/`: Command pattern implementations
    - `device/`: Device interfaces and base classes
    - `factory/`: Factory pattern implementations
    - `memento/`: Memento pattern implementations
    - `MVC/`: Model-View-Controller components
    - `observer/`: Observer pattern implementations
    - `registry/`: Device registry (Singleton)
    - `strategy/`: Strategy pattern implementations
    - `template/`: Template method pattern implementations
  - `devices/`: Device implementations
    - `concrete/`: Concrete device implementations (Light, Thermostat)
    - `group/`: Composite and Decorator implementations
    - `state/`: State pattern implementations
  - `flyweight/`: Flyweight pattern implementation
  - `userInterface/`: UI components


## Group Members:
Kartikey Vijayakumar Hebbar, Gautam Bidari, Bo Li, Fangyi Gao, Yash Deshpande​


## License

This project is licensed under the GNU General Public License - see the LICENSE file for details.

## Acknowledgments

- This project was developed as part of the Design Patterns course at Northeastern University
- Special thanks to all contributors and the professor and TAs for guidance
