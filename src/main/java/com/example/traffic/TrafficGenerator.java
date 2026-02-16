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
        int vehiclesToCreate = (int) (generationRate * random.nextDouble());
        for (int i = 0; i < vehiclesToCreate; i++) {
            String direction = directions[random.nextInt(directions.length)];
            Vehicle v = new Vehicle(direction);
            vehicles.add(v);
            newVehicles.add(v);
        }
        return newVehicles;
    }

    /**
     * Update vehicle positions (move them closer to the intersection).
     */
    public void updateVehicles() {
        for (Vehicle v : vehicles) {
            if (!v.isStopped()) {
                v.setPosition(v.getPosition() + 2.0); // move forward
            }
        }
        // Remove vehicles that have passed through the intersection
        vehicles.removeIf(v -> v.getPosition() >= 100);
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
