package com.example.viz;

import com.example.traffic.Vehicle;
import com.example.controller.TrafficLightController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationVisualizerTest {
    @Test
    void renderMapOutput() {
        List<Vehicle> vehicles = new ArrayList<>();
        Vehicle v1 = new Vehicle("North");
        v1.setPosition(50);
        vehicles.add(v1);

        TrafficLightController controller = new TrafficLightController(20);
        controller.update();

        String output = SimulationVisualizer.renderMap(vehicles, controller);
        assertNotNull(output);
        assertTrue(output.contains("North"));
        assertTrue(output.contains("GREEN") || output.contains("RED"));
    }

    @Test
    void printSummaryDoesNotThrow() {
        List<Vehicle> vehicles = new ArrayList<>();
        TrafficLightController controller = new TrafficLightController(10);
        assertDoesNotThrow(() -> SimulationVisualizer.printSummary(vehicles, controller));
    }
}
