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
├── App.java                    # Main entry point; runs simulation demo
├── traffic/
│   ├── Vehicle.java            # Represents a vehicle
│   └── TrafficGenerator.java    # Generates and moves vehicles
├── controller/
│   └── TrafficLightController.java  # Manages traffic light states
└── viz/
    └── SimulationVisualizer.java    # Renders simulation state

src/test/java/com/example/
├── traffic/
│   ├── VehicleTest.java
│   └── TrafficGeneratorTest.java
├── controller/
│   └── TrafficLightControllerTest.java
└── viz/
    └── SimulationVisualizerTest.java
```

## Requirements

- **Java:** Java 21 (configured in `build.gradle`)
- **Gradle wrapper:** Required for reproducible builds. The wrapper files (`gradlew`, `gradlew.bat`, and `gradle/wrapper/*`) enable running builds without installing Gradle system-wide.

## Quick Start

### Build the project

```powershell
.\gradlew build
```

### Run the simulation

```powershell
.\gradlew run
```

This runs a 10-step simulation showing:
- Vehicle generation from each direction
- Traffic light state transitions (North/South → East/West → repeat)
- Vehicle movement along each approach
- Real-time visualization of the crossroad state

### Run tests

```powershell
.\gradlew test
```






