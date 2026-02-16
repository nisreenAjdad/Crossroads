package com.example.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrafficLightControllerTest {
    @Test
    void controllerInitialization() {
        TrafficLightController controller = new TrafficLightController(20);
        assertEquals(20, controller.getCycleTime());
    }

    @Test
    void lightPhaseTransition() {
        TrafficLightController controller = new TrafficLightController(20);
        
        // Simulate first phase (North/South green)
        controller.update();
        assertEquals(TrafficLightController.LightState.GREEN, controller.getLight("North"));
        assertEquals(TrafficLightController.LightState.GREEN, controller.getLight("South"));
        assertEquals(TrafficLightController.LightState.RED, controller.getLight("East"));
        assertEquals(TrafficLightController.LightState.RED, controller.getLight("West"));
        
        // Advance to second phase (East/West green)
        for (int i = 0; i < 10; i++) {
            controller.update();
        }
        assertEquals(TrafficLightController.LightState.GREEN, controller.getLight("East"));
        assertEquals(TrafficLightController.LightState.GREEN, controller.getLight("West"));
        assertEquals(TrafficLightController.LightState.RED, controller.getLight("North"));
        assertEquals(TrafficLightController.LightState.RED, controller.getLight("South"));
    }

    @Test
    void cycleTimeUpdate() {
        TrafficLightController controller = new TrafficLightController(10);
        controller.setCycleTime(20);
        assertEquals(20, controller.getCycleTime());
    }

    @Test
    void setLight() {
        TrafficLightController controller = new TrafficLightController(10);
        controller.setLight("North", TrafficLightController.LightState.GREEN);
        assertEquals(TrafficLightController.LightState.GREEN, controller.getLight("North"));
    }
}
