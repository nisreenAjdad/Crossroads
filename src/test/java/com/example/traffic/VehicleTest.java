package com.example.traffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {
    @Test
    void vehicleCreation() {
        Vehicle v = new Vehicle("North");
        assertEquals("North", v.getDirection());
        assertEquals(Vehicle.getStartPosition(), v.getPosition());
        assertFalse(v.isStopped());
    }

    @Test
    void vehiclePositionUpdate() {
        Vehicle v = new Vehicle("East");
        v.setPosition(50);
        assertEquals(50.0, v.getPosition());
    }

    @Test
    void vehiclePositionUnbounded() {
        Vehicle v = new Vehicle("West");
        v.setPosition(150); // exceeds previous max
        assertEquals(150.0, v.getPosition());

        v.setPosition(-100); // negative allowed (off-screen approach)
        assertEquals(-100.0, v.getPosition());
    }

    @Test
    void vehicleStopped() {
        Vehicle v = new Vehicle("South");
        assertFalse(v.isStopped());
        v.setStopped(true);
        assertTrue(v.isStopped());
    }
}
