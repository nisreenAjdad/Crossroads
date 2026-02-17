package com.example.traffic;

/**
 * Represents a vehicle in the traffic simulation.
 */
public class Vehicle {
    private static int idCounter = 0;
    public static final double APPROACH_DISTANCE = 220.0;
    public static final double INTERSECTION_DISTANCE = 150.0;
    public static final double EXIT_DISTANCE = 220.0;
    private static final double START_POSITION = -APPROACH_DISTANCE;
    private static final double END_POSITION = INTERSECTION_DISTANCE + EXIT_DISTANCE;
    private final int id;
    private final String direction; // "North", "South", "East", "West"
    private final int lane; // 0 or 1 representing split lanes
    private double position; // position along the road from START_POSITION to END_POSITION
    private boolean stopped;
    private final String destination; // where vehicle is heading after intersection

    public Vehicle(String direction) {
        this(direction, 0);
    }

    public Vehicle(String direction, int lane) {
        this.id = ++idCounter;
        this.direction = direction;
        this.lane = Math.max(0, Math.min(1, lane));
        this.position = START_POSITION; // Start well before the visible edge
        this.stopped = false;
        // Vehicle travels through intersection to opposite direction
        this.destination = getOppositeDirection(direction);
    }

    public static double getStartPosition() {
        return START_POSITION;
    }

    public static double getEndPosition() {
        return END_POSITION;
    }

    public static double getPathLength() {
        return END_POSITION - START_POSITION;
    }

    public static double getIntersectionStart() {
        return 0.0;
    }

    public static double getIntersectionEnd() {
        return INTERSECTION_DISTANCE;
    }

    public int getId() {
        return id;
    }

    public String getDirection() {
        return direction;
    }

    public int getLane() {
        return lane;
    }

    public String getDestination() {
        return destination;
    }

    private static String getOppositeDirection(String dir) {
        return switch (dir) {
            case "North" -> "South";
            case "South" -> "North";
            case "East" -> "West";
            case "West" -> "East";
            default -> dir;
        };
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position; // Allow negative positions (off-screen approach) and beyond 100 (exiting)
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Override
    public String toString() {
        return String.format("Vehicle[id=%d, dir=%s, lane=%d, pos=%.1f, stopped=%b]",
            id, direction, lane, position, stopped);
    }
}
