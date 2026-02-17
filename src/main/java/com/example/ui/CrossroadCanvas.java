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
    static final int ROAD_WIDTH = 100;
    static final int INTERSECTION_SIZE = ROAD_WIDTH;
    static final int VEHICLE_WIDTH = 30;
    static final int VEHICLE_HEIGHT = 20;
    static final int LIGHT_RADIUS = 15;
    static final int EDGE_BUFFER = 200;
    static final int PREFERRED_WIDTH = 800;
    static final int PREFERRED_HEIGHT = 700;
    static final double STOP_LINE_PIXEL_OFFSET = 20.0;
    private static final Color GRASS_COLOR = new Color(66, 140, 66);

    private List<Vehicle> vehicles;
    private TrafficLightController controller;
    private int centerX;
    private int centerY;

    public CrossroadCanvas() {
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
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

        drawGrass(g2d);
        drawRoads(g2d);
        drawIntersection(g2d);
        drawCenterLines(g2d);
        drawStopLines(g2d);
        drawTrafficLights(g2d);
        drawVehicles(g2d);
        drawLabels(g2d);
    }

    private void drawGrass(Graphics2D g) {
        g.setColor(GRASS_COLOR);
        int halfRoad = ROAD_WIDTH / 2;

        // Top-left
        g.fillRect(0, 0, centerX - halfRoad, centerY - halfRoad);
        // Top-right
        g.fillRect(centerX + halfRoad, 0, getWidth() - (centerX + halfRoad), centerY - halfRoad);
        // Bottom-left
        g.fillRect(0, centerY + halfRoad, centerX - halfRoad, getHeight() - (centerY + halfRoad));
        // Bottom-right
        g.fillRect(centerX + halfRoad, centerY + halfRoad, getWidth() - (centerX + halfRoad),
                getHeight() - (centerY + halfRoad));
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
        int squareSize = ROAD_WIDTH;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(centerX - squareSize / 2, centerY - squareSize / 2,
            squareSize, squareSize);
    }

    private void drawCenterLines(Graphics2D g) {
        drawStripedLine(g, centerX, centerY - INTERSECTION_SIZE / 2,
                centerX, centerY + INTERSECTION_SIZE / 2);
        drawStripedLine(g, centerX - INTERSECTION_SIZE / 2, centerY,
                centerX + INTERSECTION_SIZE / 2, centerY);
    }

    private void drawStopLines(Graphics2D g) {
        Stroke previous = g.getStroke();
        Color previousColor = g.getColor();
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(6));

        int halfRoad = ROAD_WIDTH / 2;
        int offset = (int) STOP_LINE_PIXEL_OFFSET;

        // Northbound stop line (horizontal line above the north light)
        int northY = centerY - INTERSECTION_SIZE / 2 - offset;
        g.drawLine(centerX - halfRoad, northY, centerX + halfRoad, northY);

        // Southbound stop line
        int southY = centerY + INTERSECTION_SIZE / 2 + offset;
        g.drawLine(centerX - halfRoad, southY, centerX + halfRoad, southY);

        // Eastbound stop line (vertical near east light)
        int eastX = centerX + INTERSECTION_SIZE / 2 + offset;
        g.drawLine(eastX, centerY - halfRoad, eastX, centerY + halfRoad);

        // Westbound stop line
        int westX = centerX - INTERSECTION_SIZE / 2 - offset;
        g.drawLine(westX, centerY - halfRoad, westX, centerY + halfRoad);

        g.setStroke(previous);
        g.setColor(previousColor);
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

        for (Vehicle v : dirVehicles) {
            double along = getAlongRoadCoordinate(direction, v.getPosition());
            double laneCenter = getPerpendicularCenter(direction);
            int x;
            int y;

            if (direction.equals("North") || direction.equals("South")) {
                y = (int) Math.round(along);
                x = (int) Math.round(laneCenter - VEHICLE_WIDTH / 2.0);
            } else {
                x = (int) Math.round(along);
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

    private double getPerpendicularCenter(String direction) {
        return switch (direction) {
            case "North" -> centerX - ROAD_WIDTH / 4.0; // stay on left half
            case "South" -> centerX + ROAD_WIDTH / 4.0; // stay on right half
            case "East" -> centerY - ROAD_WIDTH / 4.0;  // stay on upper half
            case "West" -> centerY + ROAD_WIDTH / 4.0;  // stay on lower half
            default -> centerX;
        };
    }

    private double getAlongRoadCoordinate(String direction, double position) {
        double start = Vehicle.getStartPosition();
        double intersectionStart = Vehicle.getIntersectionStart();
        double intersectionEnd = Vehicle.getIntersectionEnd();
        double end = Vehicle.getEndPosition();
        double clamped = Math.max(start, Math.min(end, position));

        return switch (direction) {
            case "North" -> mapSegments(clamped,
                    start, intersectionStart, -EDGE_BUFFER, centerY - INTERSECTION_SIZE / 2.0,
                    intersectionEnd, centerY + INTERSECTION_SIZE / 2.0,
                    end, getHeight() + EDGE_BUFFER);
            case "South" -> mapSegments(clamped,
                    start, intersectionStart, getHeight() + EDGE_BUFFER, centerY + INTERSECTION_SIZE / 2.0,
                    intersectionEnd, centerY - INTERSECTION_SIZE / 2.0,
                    end, -EDGE_BUFFER);
            case "East" -> mapSegments(clamped,
                    start, intersectionStart, getWidth() + EDGE_BUFFER, centerX + INTERSECTION_SIZE / 2.0,
                    intersectionEnd, centerX - INTERSECTION_SIZE / 2.0,
                    end, -EDGE_BUFFER);
            case "West" -> mapSegments(clamped,
                    start, intersectionStart, -EDGE_BUFFER, centerX - INTERSECTION_SIZE / 2.0,
                    intersectionEnd, centerX + INTERSECTION_SIZE / 2.0,
                    end, getWidth() + EDGE_BUFFER);
            default -> 0.0;
        };
    }

    private double mapSegments(double value,
                               double approachStart, double approachEnd, double approachPixelStart, double approachPixelEnd,
                               double exitSegmentEnd, double exitPixelEnd,
                               double totalEnd, double totalPixelEnd) {
        if (value <= approachEnd) {
            return interpolate(value, approachStart, approachEnd, approachPixelStart, approachPixelEnd);
        } else if (value <= exitSegmentEnd) {
            return interpolate(value, approachEnd, exitSegmentEnd, approachPixelEnd, exitPixelEnd);
        }
        return interpolate(value, exitSegmentEnd, totalEnd, exitPixelEnd, totalPixelEnd);
    }

    private double interpolate(double value, double inStart, double inEnd, double outStart, double outEnd) {
        if (inEnd - inStart == 0) {
            return outEnd;
        }
        double ratio = (value - inStart) / (inEnd - inStart);
        ratio = Math.max(0.0, Math.min(1.0, ratio));
        return outStart + ratio * (outEnd - outStart);
    }

    private void drawLabels(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        g.drawString("NORTH", centerX - ROAD_WIDTH / 2 - 70, 20);
        g.drawString("SOUTH", centerX + ROAD_WIDTH / 2 + 10, getHeight() - 1);
        g.drawString("EAST", getWidth() - 60, centerY - ROAD_WIDTH / 2 - 10);
        g.drawString("WEST", 10,centerY + ROAD_WIDTH / 2 + 25);
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
