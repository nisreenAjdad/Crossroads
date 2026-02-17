package com.example.ui;

import com.example.traffic.Vehicle;
import com.example.controller.TrafficLightController;
import javax.swing.JPanel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Swing-based renderer for the crossroad simulation.
 * Draws vehicles as rectangles and traffic lights as circles.
 */
public class CrossroadCanvas extends JPanel {
    private static final int ROAD_WIDTH = 60;
    private static final int INTERSECTION_SIZE = 150;
    private static final int VEHICLE_WIDTH = 30;
    private static final int VEHICLE_HEIGHT = 20;
    private static final int LIGHT_RADIUS = 15;

    private List<Vehicle> vehicles;
    private TrafficLightController controller;
    private int centerX;
    private int centerY;

    public CrossroadCanvas() {
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(800, 700));
    }

    public void updateSimulation(List<Vehicle> vehicles, TrafficLightController controller) {
        this.vehicles = vehicles;
        this.controller = controller;
        this.centerX = getWidth() / 2;
        this.centerY = getHeight() / 2;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (vehicles == null || controller == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        drawRoads(g2d);
        drawIntersection(g2d);
        drawTrafficLights(g2d);
        drawVehicles(g2d);
        drawLabels(g2d);
    }

    private void drawRoads(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);

        // North road
        g.fillRect(centerX - ROAD_WIDTH / 2, 0, ROAD_WIDTH, centerY - INTERSECTION_SIZE / 2);

        // South road
        g.fillRect(centerX - ROAD_WIDTH / 2, centerY + INTERSECTION_SIZE / 2, ROAD_WIDTH,
                getHeight() - (centerY + INTERSECTION_SIZE / 2));

        // East road
        g.fillRect(centerX + INTERSECTION_SIZE / 2, centerY - ROAD_WIDTH / 2,
                getWidth() - (centerX + INTERSECTION_SIZE / 2), ROAD_WIDTH);

        // West road
        g.fillRect(0, centerY - ROAD_WIDTH / 2, centerX - INTERSECTION_SIZE / 2, ROAD_WIDTH);
    }

    private void drawIntersection(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillRect(centerX - INTERSECTION_SIZE / 2, centerY - INTERSECTION_SIZE / 2,
                INTERSECTION_SIZE, INTERSECTION_SIZE);

        // Draw road markings
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRect(centerX - INTERSECTION_SIZE / 2, centerY - INTERSECTION_SIZE / 2,
                INTERSECTION_SIZE, INTERSECTION_SIZE);
    }

    private void drawTrafficLights(Graphics2D g) {
        // North light (top)
        drawLight(g, centerX, centerY - INTERSECTION_SIZE / 2 - 40,
                controller.getLight("North"));

        // South light (bottom)
        drawLight(g, centerX, centerY + INTERSECTION_SIZE / 2 + 40,
                controller.getLight("South"));

        // East light (right)
        drawLight(g, centerX + INTERSECTION_SIZE / 2 + 40, centerY,
                controller.getLight("East"));

        // West light (left)
        drawLight(g, centerX - INTERSECTION_SIZE / 2 - 40, centerY,
                controller.getLight("West"));
    }

    private void drawLight(Graphics2D g, int x, int y,
                          TrafficLightController.LightState state) {
        Color color = switch (state) {
            case GREEN -> Color.GREEN;
            case RED -> Color.RED;
            case YELLOW -> Color.YELLOW;
        };

        g.setColor(color);
        g.fillOval(x - LIGHT_RADIUS, y - LIGHT_RADIUS, LIGHT_RADIUS * 2, LIGHT_RADIUS * 2);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawOval(x - LIGHT_RADIUS, y - LIGHT_RADIUS, LIGHT_RADIUS * 2, LIGHT_RADIUS * 2);
    }

    private void drawVehicles(Graphics2D g) {
        Map<String, java.util.List<Vehicle>> vehiclesByDir = groupVehiclesByDirection(vehicles);

        drawVehiclesForDirection(g, vehiclesByDir, "North", true, false);
        drawVehiclesForDirection(g, vehiclesByDir, "South", true, true);
        drawVehiclesForDirection(g, vehiclesByDir, "East", false, true);
        drawVehiclesForDirection(g, vehiclesByDir, "West", false, false);
    }

    private void drawVehiclesForDirection(Graphics2D g, Map<String, java.util.List<Vehicle>> vehiclesByDir,
                                         String direction, boolean isVertical, boolean isReversed) {
        java.util.List<Vehicle> dirVehicles = vehiclesByDir.getOrDefault(direction, new java.util.ArrayList<>());

        for (Vehicle v : dirVehicles) {
            double pos = v.getPosition();
            int x, y;

            if (isVertical) {
                if (direction.equals("North")) {
                    y = (int) (centerY - (pos / 100.0) * (centerY - INTERSECTION_SIZE / 2 + 50));
                    x = centerX - VEHICLE_WIDTH / 2;
                } else {
                    y = (int) (centerY + (pos / 100.0) * (getHeight() - centerY - INTERSECTION_SIZE / 2 - 50));
                    x = centerX - VEHICLE_WIDTH / 2;
                }
            } else {
                if (direction.equals("East")) {
                    x = (int) (centerX + (pos / 100.0) * (getWidth() - centerX - INTERSECTION_SIZE / 2 - 50));
                    y = centerY - VEHICLE_HEIGHT / 2;
                } else {
                    x = (int) (centerX - (pos / 100.0) * (centerX - INTERSECTION_SIZE / 2 + 50));
                    y = centerY - VEHICLE_HEIGHT / 2;
                }
            }

            g.setColor(Color.BLUE);
            g.fillRect(x, y, VEHICLE_WIDTH, VEHICLE_HEIGHT);

            g.setColor(Color.DARK_GRAY);
            g.setStroke(new BasicStroke(1));
            g.drawRect(x, y, VEHICLE_WIDTH, VEHICLE_HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("V" + v.getId(), x + 5, y + 14);
        }
    }

    private void drawLabels(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        g.drawString("NORTH", centerX - 30, 20);
        g.drawString("SOUTH", centerX - 30, getHeight() - 10);
        g.drawString("EAST", getWidth() - 60, centerY + 10);
        g.drawString("WEST", 10, centerY + 10);
    }

    private Map<String, java.util.List<Vehicle>> groupVehiclesByDirection(List<Vehicle> vehicles) {
        Map<String, java.util.List<Vehicle>> map = new HashMap<>();
        for (String dir : new String[]{"North", "South", "East", "West"}) {
            map.put(dir, new java.util.ArrayList<>());
        }
        for (Vehicle v : vehicles) {
            map.get(v.getDirection()).add(v);
        }
        return map;
    }
}
