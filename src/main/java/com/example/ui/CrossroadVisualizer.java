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
        generator = new TrafficGenerator(2.5);
        controller = new TrafficLightController(20);

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
        animationTimer = new Timer(50, e -> {
            updateSimulation();
            canvas.updateSimulation(generator.getVehicles(), controller);
            updateStats();
        });
        animationTimer.start();
    }

    private void updateSimulation() {
        // Generate new vehicles
        generator.generateVehicles();

        // Update controller
        controller.update();

        // Move vehicles
        generator.updateVehicles();

        // Stop vehicles at red lights (simple logic)
        for (var vehicle : generator.getVehicles()) {
            if (controller.getLight(vehicle.getDirection()) == TrafficLightController.LightState.RED) {
                if (vehicle.getPosition() >= 80) {
                    vehicle.setStopped(true);
                }
            } else {
                vehicle.setStopped(false);
            }
        }
    }

    private void updateStats() {
        int vehicleCount = generator.getVehicles().size();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CrossroadVisualizer());
    }
}
