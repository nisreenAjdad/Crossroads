package com.example;

import com.example.traffic.TrafficGenerator;
import com.example.controller.TrafficLightController;
import com.example.viz.SimulationVisualizer;

/**
 * Main entry point for the Crossroads Traffic Simulation.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("=== Crossroads Traffic Simulation ===\n");

        // Initialize components
        TrafficGenerator generator = new TrafficGenerator(3.0); // 3 vehicles per time unit
        TrafficLightController controller = new TrafficLightController(20); // 20-unit cycle

        // Run simulation for a few steps
        int simulationSteps = 10;
        for (int step = 0; step < simulationSteps; step++) {
            System.out.println("\n--- Time Step " + step + " ---");

            // Generate new vehicles
            generator.generateVehicles();

            // Update traffic lights
            controller.update();

            // Update vehicle positions
            generator.updateVehicles();

            // Visualize current state
            SimulationVisualizer.printSummary(generator.getVehicles(), controller);

            try {
                Thread.sleep(500); // Pause for readability
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Simulation complete.");
    }

    public static String greet(String name) {
        return "Hello, " + name + "!";
    }
}
