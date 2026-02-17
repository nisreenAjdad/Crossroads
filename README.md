# Crossroads

A traffic-light simulation project to practice Gen-AI skills. This application demonstrates a basic traffic control system at a crossroad with three main components.

## Application Overview

This project provides a foundational environment for experimenting with traffic-control strategies at a single crossroad intersection. It consists of three main components:

- **Simple Traffic Generator** (`traffic` package)  
  Simulates vehicles approaching the intersection from random directions at random intervals, mimicking real-world traffic patterns.

- **Basic Traffic Light Control Algorithm** (`controller` package)  
  A clock-driven controller that switches lights on a fixed schedule without considering traffic sensor data. This provides a baseline to improve upon.

- **Results Visualization on a Map** (`viz` package)  
  Displays simulation state including vehicle positions and light states in a simple text-based map view.

## Project Structure

```
src/main/java/com/example/
├── App.java                    # Main entry point; launches Swing UI
├── traffic/
│   ├── Vehicle.java            # Represents a vehicle
│   └── TrafficGenerator.java    # Generates and moves vehicles
├── controller/
│   └── TrafficLightController.java  # Manages traffic light states
├── viz/
│   └── SimulationVisualizer.java    # Text-based console visualization
└── ui/
    ├── CrossroadVisualizer.java     # Main Swing window (JFrame)
    └── CrossroadCanvas.java         # Canvas rendering (JPanel with Graphics2D)

src/test/java/com/example/
├── traffic/
│   ├── VehicleTest.java
│   └── TrafficGeneratorTest.java
├── controller/
│   └── TrafficLightControllerTest.java
└── viz/
    └── SimulationVisualizerTest.java
```

## Features

### Simulation Engine
- Vehicle generation with random arrivals and directions
- Clock-driven traffic light controller (fixed schedule)
- Vehicle movement and intersection logic

### Visualization (`ui` package - NEW)
- **Swing-based GUI** displaying the crossroad in real-time
- Vehicles rendered as blue rectangles moving on road approaches
- Traffic lights shown as colored circles (green/red/yellow)
- Statistics panel showing:
  - Current vehicle count
  - Cycle time progress
  - Individual light states per direction
- Smooth 20 FPS animation
- Simple traffic logic: vehicles stop at red lights, move on green

## Requirements

- **Java:** Java 21 (configured in `build.gradle`)
- **Gradle wrapper:** Required for reproducible builds. The wrapper files (`gradlew`, `gradlew.bat`, and `gradle/wrapper/*`) enable running builds without installing Gradle system-wide.

## Quick Start

### Build the project

```powershell
.\gradlew build
```

### Run the Swing UI (recommended)

```powershell
.\gradlew run
```

This launches an interactive window showing:
- The crossroad intersection with four road approaches
- Real-time vehicle movement 
- Traffic light states for each direction
- Live statistics panel
- Vehicles automatically stop/go based on signal state

### Run tests

```powershell
.\gradlew test
```

Tests verify:
- Vehicle position and movement
- Traffic light phase transitions
- Traffic generator creation and updates
- Simulation visualization






