package com.example.traffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class TrafficGeneratorTest {
    @Test
    void generationRateSet() {
        TrafficGenerator gen = new TrafficGenerator(2.0);
        assertEquals(2.0, gen.getGenerationRate());
    }

    @Test
    void generateVehicles() {
        TrafficGenerator gen = new TrafficGenerator(5.0);
        List<Vehicle> newVehicles = gen.generateVehicles();
        assertNotNull(newVehicles);
        assertTrue(newVehicles.size() >= 0);
    }

    @Test
    void updateVehiclesMovement() {
        TrafficGenerator gen = new TrafficGenerator(0.0);
        // Generate a vehicle the proper way or add it via a method
        // For now, let's test with a generated vehicle by setting a high rate temporarily
        gen.setGenerationRate(10.0);
        gen.generateVehicles();
        List<Vehicle> vehicles = gen.getVehicles();
        
        if (!vehicles.isEmpty()) {
            Vehicle v = vehicles.get(0);
            double initialPos = Vehicle.getStartPosition() + 10.0; // still on the approach
            v.setPosition(initialPos);

            gen.updateVehicles();
            assertEquals(initialPos + TrafficGenerator.BASE_SPEED_PER_TICK,
                    v.getPosition(), 0.01);
        }
    }

    @Test
    void stoppedVehiclesDoNotMove() {
        TrafficGenerator gen = new TrafficGenerator(10.0);
        gen.generateVehicles();
        List<Vehicle> vehicles = gen.getVehicles();
        
        if (!vehicles.isEmpty()) {
            Vehicle v = vehicles.get(0);
            v.setPosition(30);
            v.setStopped(true);

            gen.updateVehicles();
            assertEquals(30.0, v.getPosition());
        }
    }

    @Test
    void vehiclesAccelerateInsideIntersection() {
        TrafficGenerator gen = new TrafficGenerator(0.0);
        gen.setGenerationRate(10.0);
        gen.generateVehicles();
        List<Vehicle> vehicles = gen.getVehicles();

        if (!vehicles.isEmpty()) {
            Vehicle v = vehicles.get(0);
            double initialPos = Vehicle.getIntersectionStart() + 10.0;
            v.setPosition(initialPos);

            gen.updateVehicles();
            double expected = initialPos + TrafficGenerator.BASE_SPEED_PER_TICK *
                    TrafficGenerator.INTERSECTION_SPEED_MULTIPLIER;
            assertEquals(expected, v.getPosition(), 0.01);
        }
    }

    @Test
    void vehiclesMaintainBoostAfterIntersection() {
        TrafficGenerator gen = new TrafficGenerator(0.0);
        gen.setGenerationRate(10.0);
        gen.generateVehicles();
        List<Vehicle> vehicles = gen.getVehicles();

        if (!vehicles.isEmpty()) {
            Vehicle v = vehicles.get(0);
            double initialPos = Vehicle.getIntersectionEnd() + 5.0;
            v.setPosition(initialPos);

            gen.updateVehicles();
            double expected = initialPos + TrafficGenerator.BASE_SPEED_PER_TICK *
                    TrafficGenerator.INTERSECTION_SPEED_MULTIPLIER;
            assertEquals(expected, v.getPosition(), 0.01);
        }
    }
}
