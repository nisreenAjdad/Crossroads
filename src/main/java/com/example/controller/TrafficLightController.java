package com.example.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic Traffic Light Control Algorithm: a simple clock-driven controller
 * that switches lights on a fixed schedule without considering traffic data.
 */
public class TrafficLightController {
    private final Map<String, LightState> lights;
    private int cycleTime; // total cycle time in time units
    private int timeInCycle; // current position in the cycle

    public enum LightState {
        GREEN, RED, YELLOW
    }

    public TrafficLightController(int cycleTime) {
        this.cycleTime = cycleTime;
        this.timeInCycle = 0;
        this.lights = new HashMap<>();
        // Initialize all directions to RED
        lights.put("North", LightState.RED);
        lights.put("South", LightState.RED);
        lights.put("East", LightState.RED);
        lights.put("West", LightState.RED);
    }

    /**
     * Update the controller for one time step.
     * Uses a simple two-phase scheme: North/South green, then East/West green.
     */
    public void update() {
        timeInCycle = (timeInCycle + 1) % cycleTime;

        int halfCycle = cycleTime / 2;
        if (timeInCycle < halfCycle) {
            // North/South green phase
            lights.put("North", LightState.GREEN);
            lights.put("South", LightState.GREEN);
            lights.put("East", LightState.RED);
            lights.put("West", LightState.RED);
        } else {
            // East/West green phase
            lights.put("North", LightState.RED);
            lights.put("South", LightState.RED);
            lights.put("East", LightState.GREEN);
            lights.put("West", LightState.GREEN);
        }
    }

    public LightState getLight(String direction) {
        return lights.getOrDefault(direction, LightState.RED);
    }

    public void setLight(String direction, LightState state) {
        lights.put(direction, state);
    }

    public int getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(int cycleTime) {
        this.cycleTime = Math.max(1, cycleTime);
    }

    public int getTimeInCycle() {
        return timeInCycle;
    }
}
