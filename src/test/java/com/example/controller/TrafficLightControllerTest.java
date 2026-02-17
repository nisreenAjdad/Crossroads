package com.example.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrafficLightControllerTest {
    @Test
    void controllerInitialization() {
        TrafficLightController controller = new TrafficLightController(850);
        assertEquals(850, controller.getCycleTime());
    }

    @Test
    void lightPhaseTransition() {
        TrafficLightController controller = new TrafficLightController(850);
        
        // Test N/S GREEN phase (0-224) - 45 seconds
        for (int i = 0; i < 112; i++) {
            controller.update();
        }
        assertEquals(TrafficLightController.LightState.GREEN, controller.getLight("North"), "Should be GREEN during N/S green phase");
        assertEquals(TrafficLightController.LightState.RED, controller.getLight("East"), "East should be RED during N/S phase");
        
        // Test N/S YELLOW transition (225-324) - 20 seconds
        for (int i = 0; i < 113; i++) {
            controller.update();
        }
        assertEquals(TrafficLightController.LightState.YELLOW, controller.getLight("North"), "Should transition to YELLOW at position 225");
        
        // Test ALL-RED safety phase (325-424) - 20 seconds
        for (int i = 0; i < 100; i++) {
            controller.update();
        }
        assertEquals(TrafficLightController.LightState.RED, controller.getLight("North"), "Should be all-RED for safety");
        assertEquals(TrafficLightController.LightState.RED, controller.getLight("East"), "Should be all-RED for safety");
        
        // Test E/W GREEN phase (425+) - 45 seconds
        for (int i = 0; i < 100; i++) {
            controller.update();
        }
        assertEquals(TrafficLightController.LightState.GREEN, controller.getLight("East"), "Should be GREEN for E/W phase");
        assertEquals(TrafficLightController.LightState.RED, controller.getLight("North"), "Should be RED for N/S during E/W phase");
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
