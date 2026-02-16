package com.example.viz;

import com.example.traffic.Vehicle;
import com.example.controller.TrafficLightController;
import java.util.List;

/**
 * Results Visualization on a Map: displays the simulation state
 * including vehicle positions and traffic light states.
 */
public class SimulationVisualizer {

    /**
     * Render a simple text-based map showing:
     * - Vehicle positions on each approach
     * - Current light state for each direction
     */
    public static String renderMap(List<Vehicle> vehicles, TrafficLightController controller) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Crossroads Traffic Simulation ===\n");

        // Group vehicles by direction
        java.util.Map<String, List<Vehicle>> vehiclesByDir = new java.util.HashMap<>();
        for (String dir : new String[]{"North", "South", "East", "West"}) {
            vehiclesByDir.put(dir, new java.util.ArrayList<>());
        }
        for (Vehicle v : vehicles) {
            vehiclesByDir.get(v.getDirection()).add(v);
        }

        // Render each direction
        for (String dir : new String[]{"North", "South", "East", "West"}) {
            TrafficLightController.LightState light = controller.getLight(dir);
            List<Vehicle> dirVehicles = vehiclesByDir.get(dir);

            sb.append(String.format("[%s] Light: %s | Vehicles: %d\n",
                    dir, light, dirVehicles.size()));

            for (Vehicle v : dirVehicles) {
                double pos = v.getPosition();
                String bar = buildProgressBar(pos);
                sb.append(String.format("  %s stopped=%b\n", bar, v.isStopped()));
            }
        }

        sb.append("====================================\n");
        return sb.toString();
    }

    private static String buildProgressBar(double position) {
        int barLength = 20;
        int filledLength = (int) (position / 100.0 * barLength);
        StringBuilder bar = new StringBuilder("|");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filledLength ? "=" : "-");
        }
        bar.append("|");
        return bar.toString();
    }

    /**
     * Print a summary of the current simulation state.
     */
    public static void printSummary(List<Vehicle> vehicles, TrafficLightController controller) {
        System.out.println(renderMap(vehicles, controller));
    }
}
