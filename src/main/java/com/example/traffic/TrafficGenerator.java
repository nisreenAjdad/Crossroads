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
    private static final int MAX_VEHICLES_PER_DIRECTION = 8;
    private static final int MAX_TOTAL_VEHICLES = 10;
    static final double BASE_SPEED_PER_TICK = 2.0;
    static final double INTERSECTION_SPEED_MULTIPLIER = 1.75;

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
            if (!canSpawn(direction)) {
                continue;
            }
            Vehicle v = new Vehicle(direction);
            vehicles.add(v);
            newVehicles.add(v);
        }
        return newVehicles;
    }

    /**
     * Update vehicle positions (move them closer to the intersection and through it).
     * Vehicles move at a steady approach speed and accelerate slightly inside the
     * intersection to keep traffic flowing.
     */
    public void updateVehicles() {
        final double intersectionStart = Vehicle.getIntersectionStart();
        for (Vehicle v : vehicles) {
            if (!v.isStopped()) {
                double speed = BASE_SPEED_PER_TICK;
                double position = v.getPosition();
                if (position >= intersectionStart) {
                    speed *= INTERSECTION_SPEED_MULTIPLIER; // keep higher pace once crossing begins
                }
                v.setPosition(position + speed);
            }
        }
        // Remove vehicles that have passed through the intersection completely
        vehicles.removeIf(v -> v.getPosition() >= Vehicle.getEndPosition());
    }

    private boolean canSpawn(String direction) {
        long count = vehicles.stream()
                .filter(v -> v.getDirection().equals(direction))
                .count();
        return count < MAX_VEHICLES_PER_DIRECTION;
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
