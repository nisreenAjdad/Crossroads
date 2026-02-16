package com.example.traffic;

/**
 * Represents a vehicle in the traffic simulation.
 */
public class Vehicle {
    private static int idCounter = 0;
    private final int id;
    private final String direction; // "North", "South", "East", "West"
    private double position; // position along the road (0 = far, 100 = at intersection)
    private boolean stopped;

    public Vehicle(String direction) {
        this.id = ++idCounter;
        this.direction = direction;
        this.position = 0.0;
        this.stopped = false;
    }

    public int getId() {
        return id;
    }

    public String getDirection() {
        return direction;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = Math.max(0, Math.min(100, position));
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Override
    public String toString() {
        return String.format("Vehicle[id=%d, dir=%s, pos=%.1f, stopped=%b]",
                id, direction, position, stopped);
    }
}
