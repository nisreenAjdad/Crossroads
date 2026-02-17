package com.example.traffic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simple Traffic Generator: simulates vehicles arriving at the intersection
 * from random directions at random intervals.
 */
public class TrafficGenerator {
    private final Random random;
    private final List<Vehicle> vehicles;
    private final String[] directions = {"North", "South", "East", "West"};
    private double generationRate; // vehicles per time unit
    private static final int MAX_VEHICLES_PER_LANE = 5;
    private static final int MAX_TOTAL_VEHICLES = 30;

    public TrafficGenerator(double generationRate) {
        this.random = new Random();
        this.vehicles = new ArrayList<>();
        this.generationRate = generationRate;
    }

    /**
     * Generate new vehicles for this time step based on the generation rate.
     * Returns a list of newly created vehicles.
     */
    public List<Vehicle> generateVehicles() {
        List<Vehicle> newVehicles = new ArrayList<>();
        int vehiclesToCreate = (int) generationRate;
        double fractionalPart = generationRate - vehiclesToCreate;
        if (random.nextDouble() < fractionalPart) {
            vehiclesToCreate++;
        }
        for (int i = 0; i < vehiclesToCreate; i++) {
            if (vehicles.size() >= MAX_TOTAL_VEHICLES) {
                break;
            }
            String direction = directions[random.nextInt(directions.length)];
            int lane = random.nextInt(2);
            if (!canSpawn(direction, lane)) {
                continue;
            }
            Vehicle v = new Vehicle(direction, lane);
            vehicles.add(v);
            newVehicles.add(v);
        }
        return newVehicles;
    }

    /**
     * Update vehicle positions (move them closer to the intersection and through it).
     * Vehicles move slowly for realistic behavior (0.5 per frame).
     * Position: -50 to 0 = off-screen approach, 0-70 = approach, 70-100 = at intersection, >100 = exited
     */
    public void updateVehicles() {
        final double speedPerTick = 2.0; // pixels per frame (~20 px/sec)
        for (Vehicle v : vehicles) {
            if (!v.isStopped()) {
                v.setPosition(v.getPosition() + speedPerTick); // move forward at realistic pace
            }
        }
        // Remove vehicles that have passed through the intersection completely
        vehicles.removeIf(v -> v.getPosition() >= Vehicle.getEndPosition());
    }

    private boolean canSpawn(String direction, int lane) {
        long count = vehicles.stream()
                .filter(v -> v.getDirection().equals(direction) && v.getLane() == lane)
                .count();
        return count < MAX_VEHICLES_PER_LANE;
    }

    public List<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    public void setGenerationRate(double rate) {
        this.generationRate = rate;
    }

    public double getGenerationRate() {
        return generationRate;
    }
}
