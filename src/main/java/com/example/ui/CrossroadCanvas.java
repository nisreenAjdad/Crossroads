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
    private static final int ROAD_WIDTH = 100;
    private static final int INTERSECTION_SIZE = 150;
    private static final int VEHICLE_WIDTH = 30;
    private static final int VEHICLE_HEIGHT = 20;
    private static final int LIGHT_RADIUS = 15;
    private static final int EDGE_BUFFER = 200;

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
        drawStripedLine(g, centerX, 0, centerX, centerY - INTERSECTION_SIZE / 2);

        // South road
        g.fillRect(centerX - ROAD_WIDTH / 2, centerY + INTERSECTION_SIZE / 2, ROAD_WIDTH,
                getHeight() - (centerY + INTERSECTION_SIZE / 2));
        drawStripedLine(g, centerX, centerY + INTERSECTION_SIZE / 2, centerX, getHeight());

        // East road
        g.fillRect(centerX + INTERSECTION_SIZE / 2, centerY - ROAD_WIDTH / 2,
                getWidth() - (centerX + INTERSECTION_SIZE / 2), ROAD_WIDTH);
        drawStripedLine(g, centerX + INTERSECTION_SIZE / 2, centerY, getWidth(), centerY);

        // West road
        g.fillRect(0, centerY - ROAD_WIDTH / 2, centerX - INTERSECTION_SIZE / 2, ROAD_WIDTH);
        drawStripedLine(g, 0, centerY, centerX - INTERSECTION_SIZE / 2, centerY);
    }

    private void drawStripedLine(Graphics2D g, int x1, int y1, int x2, int y2) {
        Stroke previous = g.getStroke();
        Color previousColor = g.getColor();
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{15f, 12f}, 0));
        g.drawLine(x1, y1, x2, y2);
        g.setStroke(previous);
        g.setColor(previousColor);
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

        drawVehiclesForDirection(g, vehiclesByDir, "North");
        drawVehiclesForDirection(g, vehiclesByDir, "South");
        drawVehiclesForDirection(g, vehiclesByDir, "East");
        drawVehiclesForDirection(g, vehiclesByDir, "West");
    }

    private void drawVehiclesForDirection(Graphics2D g, Map<String, java.util.List<Vehicle>> vehiclesByDir,
                                          String direction) {
        java.util.List<Vehicle> dirVehicles = vehiclesByDir.getOrDefault(direction, new java.util.ArrayList<>());

        double normalizedRange = Vehicle.getPathLength();
        double start = Vehicle.getStartPosition();

        for (Vehicle v : dirVehicles) {
            double normalized = (v.getPosition() - start) / normalizedRange;
            normalized = Math.max(0.0, Math.min(1.0, normalized));
            int x;
            int y;

            if (direction.equals("North")) {
                double laneCenter = getPerpendicularCenter(direction, v.getLane());
                double travelPixels = getHeight() + 2.0 * EDGE_BUFFER;
                double startY = -EDGE_BUFFER;
                y = (int) Math.round(startY + normalized * travelPixels);
                x = (int) Math.round(laneCenter - VEHICLE_WIDTH / 2.0);
            } else if (direction.equals("South")) {
                double laneCenter = getPerpendicularCenter(direction, v.getLane());
                double travelPixels = getHeight() + 2.0 * EDGE_BUFFER;
                double startY = getHeight() + EDGE_BUFFER;
                y = (int) Math.round(startY - normalized * travelPixels);
                x = (int) Math.round(laneCenter - VEHICLE_WIDTH / 2.0);
            } else if (direction.equals("East")) {
                double laneCenter = getPerpendicularCenter(direction, v.getLane());
                double travelPixels = getWidth() + 2.0 * EDGE_BUFFER;
                double startX = getWidth() + EDGE_BUFFER;
                x = (int) Math.round(startX - normalized * travelPixels);
                y = (int) Math.round(laneCenter - VEHICLE_HEIGHT / 2.0);
            } else { // West
                double laneCenter = getPerpendicularCenter(direction, v.getLane());
                double travelPixels = getWidth() + 2.0 * EDGE_BUFFER;
                double startX = -EDGE_BUFFER;
                x = (int) Math.round(startX + normalized * travelPixels);
                y = (int) Math.round(laneCenter - VEHICLE_HEIGHT / 2.0);
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

    private double getPerpendicularCenter(String direction, int lane) {
        double offset = lane == 0 ? -ROAD_WIDTH / 12.0 : ROAD_WIDTH / 12.0;
        return switch (direction) {
            case "North" -> centerX - ROAD_WIDTH / 4.0 + offset; // stay on left half
            case "South" -> centerX + ROAD_WIDTH / 4.0 + offset; // stay on right half
            case "East" -> centerY - ROAD_WIDTH / 4.0 + offset;  // stay on upper half
            case "West" -> centerY + ROAD_WIDTH / 4.0 + offset;  // stay on lower half
            default -> centerX;
        };
    }

    private void drawLabels(Graphics2D g) {
        g.setColor(Color.WHITE);
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
