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
     * Realistic traffic light cycle with all-red clearance:
     * - GREEN: 45 seconds (225 units @ 5Hz refresh)
     * - YELLOW: 20 seconds (100 units)
     * - ALL-RED: 20 seconds (100 units) safety clearance
     * - Full cycle: 850 units (170 seconds) - N/S 425 + E/W 425
     */
    public void update() {
        timeInCycle = (timeInCycle + 1) % cycleTime;

        // Phase durations for realistic intersection (at 5Hz refresh = 200ms timer)
        int greenDuration = 225;    // 45 seconds green per direction
        int yellowDuration = 100;   // 20 seconds yellow
        int allRedDuration = 100;   // 20 seconds all-red safety clearance
        
        int phaseLength = greenDuration + yellowDuration + allRedDuration; // 24 units per full phase

        if (timeInCycle < greenDuration) {
            // Phase 1: North/South GREEN, others RED
            lights.put("North", LightState.GREEN);
            lights.put("South", LightState.GREEN);
            lights.put("East", LightState.RED);
            lights.put("West", LightState.RED);
        } else if (timeInCycle < greenDuration + yellowDuration) {
            // Phase 2: North/South YELLOW (warning), others RED
            lights.put("North", LightState.YELLOW);
            lights.put("South", LightState.YELLOW);
            lights.put("East", LightState.RED);
            lights.put("West", LightState.RED);
        } else if (timeInCycle < greenDuration + yellowDuration + allRedDuration) {
            // Phase 3: ALL RED (safety clearance for intersection clearing)
            lights.put("North", LightState.RED);
            lights.put("South", LightState.RED);
            lights.put("East", LightState.RED);
            lights.put("West", LightState.RED);
        } else if (timeInCycle < phaseLength + greenDuration) {
            // Phase 4: East/West GREEN, others RED
            lights.put("North", LightState.RED);
            lights.put("South", LightState.RED);
            lights.put("East", LightState.GREEN);
            lights.put("West", LightState.GREEN);
        } else if (timeInCycle < phaseLength + greenDuration + yellowDuration) {
            // Phase 5: East/West YELLOW (warning), others RED
            lights.put("North", LightState.RED);
            lights.put("South", LightState.RED);
            lights.put("East", LightState.YELLOW);
            lights.put("West", LightState.YELLOW);
        } else {
            // Phase 6: ALL RED (safety clearance for intersection clearing)
            lights.put("North", LightState.RED);
            lights.put("South", LightState.RED);
            lights.put("East", LightState.RED);
            lights.put("West", LightState.RED);
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
