package com.example.ui;

import com.example.traffic.TrafficGenerator;
import com.example.controller.TrafficLightController;
import javax.swing.*;
import java.awt.*;

/**
 * Swing-based Application for visualizing the crossroad traffic simulation.
 */
public class CrossroadVisualizer extends JFrame {
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 800;
    private static final double MIN_VEHICLE_GAP = 25.0;
    private static final double STOP_LINE_PIXEL_OFFSET = CrossroadCanvas.STOP_LINE_PIXEL_OFFSET;

    private TrafficGenerator generator;
    private TrafficLightController controller;
    private CrossroadCanvas canvas;
    private JLabel statsLabel;
    private Timer animationTimer;

    public CrossroadVisualizer() {
        initializeComponents();
        startSimulation();
    }

    private void initializeComponents() {
        setTitle("Crossroads Traffic Simulation - Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        // Initialize simulation components
        generator = new TrafficGenerator(0.25);  // Less dense traffic by default
        controller = new TrafficLightController(850); // 850 units per full cycle: 425 N/S (225 green + 100 yellow + 100 all-red) + 425 E/W

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Canvas for visualization
        canvas = new CrossroadCanvas();
        mainPanel.add(canvas, BorderLayout.CENTER);

        // Stats panel at bottom
        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        panel.setPreferredSize(new Dimension(0, 70));
        statsLabel = new JLabel("Vehicles: 0 | Cycle: 0/0 | Lights: [N: RED] [S: RED] [E: RED] [W: RED]");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(statsLabel);
        return panel;
    }

    private void startSimulation() {
        animationTimer = new Timer(200, e -> {
            updateSimulation();
        });
        animationTimer.start();
    }

    private void updateSimulation() {
        generator.generateVehicles();
        controller.update();
        generator.updateVehicles();

        // Snapshot vehicles for consistent rendering + stats in this frame
        java.util.List<com.example.traffic.Vehicle> vehiclesSnapshot = generator.getVehicles();
        applyTrafficRules(vehiclesSnapshot);

        canvas.updateSimulation(vehiclesSnapshot, controller);
        updateStats(vehiclesSnapshot);
    }

    private void updateStats(java.util.List<com.example.traffic.Vehicle> vehiclesSnapshot) {
        int vehicleCount = vehiclesSnapshot.size();
        String northLight = controller.getLight("North").toString();
        String southLight = controller.getLight("South").toString();
        String eastLight = controller.getLight("East").toString();
        String westLight = controller.getLight("West").toString();

        String stats = String.format(
                "Vehicles: %d | Cycle: %d/%d | Lights: [N: %s] [S: %s] [E: %s] [W: %s]",
                vehicleCount, controller.getTimeInCycle(), controller.getCycleTime(),
                northLight, southLight, eastLight, westLight
        );
        statsLabel.setText(stats);
    }

    private void applyTrafficRules(java.util.List<com.example.traffic.Vehicle> vehiclesSnapshot) {
        java.util.Map<String, java.util.List<com.example.traffic.Vehicle>> byDirection = new java.util.HashMap<>();
        for (String dir : new String[]{"North", "South", "East", "West"}) {
            byDirection.put(dir, new java.util.ArrayList<>());
        }

        for (var vehicle : vehiclesSnapshot) {
            byDirection.computeIfAbsent(vehicle.getDirection(), k -> new java.util.ArrayList<>()).add(vehicle);
        }

        double verticalStopLineBuffer = computeStopLineBuffer(true);
        double horizontalStopLineBuffer = computeStopLineBuffer(false);
        double verticalVehicleBuffer = computeVehicleLengthBuffer(true);
        double horizontalVehicleBuffer = computeVehicleLengthBuffer(false);

        for (var entry : byDirection.entrySet()) {
            String direction = entry.getKey();
            var dirVehicles = entry.getValue();
            dirVehicles.sort(java.util.Comparator.comparingDouble(com.example.traffic.Vehicle::getPosition).reversed());

            double stopLineBuffer = isVerticalDirection(direction) ? verticalStopLineBuffer : horizontalStopLineBuffer;
            double vehicleLengthBuffer = isVerticalDirection(direction) ? verticalVehicleBuffer : horizontalVehicleBuffer;
            double stopLinePosition = com.example.traffic.Vehicle.getIntersectionStart() - stopLineBuffer;
            if (requiresFrontOffset(direction)) {
                stopLinePosition -= vehicleLengthBuffer;
            }

            com.example.traffic.Vehicle previous = null;
            for (var vehicle : dirVehicles) {
                double position = vehicle.getPosition();
                double intersectionStart = com.example.traffic.Vehicle.getIntersectionStart();
                double intersectionEnd = com.example.traffic.Vehicle.getIntersectionEnd();
                boolean beforeIntersection = position < intersectionStart;
                boolean inIntersection = position >= intersectionStart && position <= intersectionEnd;
                boolean nearStopLine = beforeIntersection && position >= stopLinePosition;
                boolean vehicleAhead = previous != null && (previous.getPosition() - vehicle.getPosition()) < MIN_VEHICLE_GAP;

                TrafficLightController.LightState light = controller.getLight(vehicle.getDirection());
                boolean shouldStop;
                if (vehicleAhead && beforeIntersection) {
                    shouldStop = true;
                } else if (light == TrafficLightController.LightState.RED && nearStopLine) {
                    shouldStop = true;
                } else if (light == TrafficLightController.LightState.YELLOW && nearStopLine) {
                    shouldStop = true;
                } else {
                    shouldStop = false;
                }

                if (inIntersection) {
                    shouldStop = false; // always clear the intersection once entered
                }

                vehicle.setStopped(shouldStop);

                if (shouldStop && beforeIntersection) {
                    double permittedPosition = stopLinePosition;
                    if (previous != null) {
                        permittedPosition = Math.min(permittedPosition, previous.getPosition() - MIN_VEHICLE_GAP);
                    }
                    if (vehicle.getPosition() > permittedPosition) {
                        vehicle.setPosition(permittedPosition);
                    }
                }
                previous = vehicle;
            }
        }
    }

    private double computeStopLineBuffer(boolean vertical) {
        double dimension = vertical ? canvas.getHeight() : canvas.getWidth();
        if (dimension <= 0) {
            dimension = vertical ? CrossroadCanvas.PREFERRED_HEIGHT : CrossroadCanvas.PREFERRED_WIDTH;
        }
        double approachPixels = dimension / 2.0 - CrossroadCanvas.ROAD_WIDTH / 2.0 + CrossroadCanvas.EDGE_BUFFER;
        double approachDistance = com.example.traffic.Vehicle.APPROACH_DISTANCE;
        return approachDistance * (STOP_LINE_PIXEL_OFFSET / approachPixels);
    }

    private double computeVehicleLengthBuffer(boolean vertical) {
        double dimension = vertical ? canvas.getHeight() : canvas.getWidth();
        if (dimension <= 0) {
            dimension = vertical ? CrossroadCanvas.PREFERRED_HEIGHT : CrossroadCanvas.PREFERRED_WIDTH;
        }
        double approachPixels = dimension / 2.0 - CrossroadCanvas.ROAD_WIDTH / 2.0 + CrossroadCanvas.EDGE_BUFFER;
        double approachDistance = com.example.traffic.Vehicle.APPROACH_DISTANCE;
        double vehiclePixels = vertical ? CrossroadCanvas.VEHICLE_HEIGHT : CrossroadCanvas.VEHICLE_WIDTH;
        return approachDistance * (vehiclePixels / approachPixels);
    }

    private boolean isVerticalDirection(String direction) {
        return "North".equals(direction) || "South".equals(direction);
    }

    private boolean requiresFrontOffset(String direction) {
        return "North".equals(direction) || "West".equals(direction);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CrossroadVisualizer());
    }
}
