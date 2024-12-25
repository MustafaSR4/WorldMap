package Dijkstra;

import java.util.ArrayList;
import java.util.List;

public class GraphPathFinder {
    private Graph graph;
    private List<List<String>> allRoutes = new ArrayList<>();
    private List<Double> routeDistances = new ArrayList<>();
    private static final int MAX_DEPTH = 10; // Maximum recursion depth to prevent infinite loops

    public GraphPathFinder(Graph graph) {
        this.graph = graph;
    }

    // Find all possible routes from source to destination
    public void findAllRoutes(String source, String destination) {
        List<String> currentPath = new ArrayList<>();
        findRoutesRecursive(source, destination, currentPath, 0.0, 0);
    }

    // Recursive method to find all routes
    private void findRoutesRecursive(String current, String destination, List<String> currentPath, double currentDistance, int depth) {
        // Depth limitation to avoid infinite recursion
        if (depth > MAX_DEPTH) {
            System.out.println("Max depth reached for current path: " + currentPath);
            return;
        }

        currentPath.add(current);

        // If destination is reached, save the route and distance
        if (current.equals(destination)) {
            allRoutes.add(new ArrayList<>(currentPath));
            routeDistances.add(currentDistance);
        } else {
            // Get the current vertex
            Vertix currentVertix = graph.getVertix(current);
            if (currentVertix != null) {
                // Traverse all neighbors
                LinkedListNode node = currentVertix.getVertices().getFirstNode();
                while (node != null) {
                    Edge edge = node.getEdge();
                    String nextCity = edge.getDestination().getCapital().getCapitalName();

                    // Avoid cycles by checking if the city is already in the current path
                    if (!currentPath.contains(nextCity)) {
                        findRoutesRecursive(nextCity, destination, currentPath, currentDistance + edge.getCost(), depth + 1);
                    } else {
                        System.out.println("Cycle detected, skipping: " + nextCity);
                    }

                    node = node.getNext(); // Move to the next neighbor
                }
            }
        }

        // Backtrack to explore other routes
        currentPath.remove(currentPath.size() - 1);
    }

    // Display all possible routes
    public void displayRoutes() {
        if (allRoutes.isEmpty()) {
            System.out.println("No routes found.");
            return;
        }

        System.out.println("All Possible Routes:");
        for (int i = 0; i < allRoutes.size(); i++) {
            System.out.println("Route " + (i + 1) + ": " + String.join(" → ", allRoutes.get(i)) +
                               " | Distance: " + String.format("%.2f", routeDistances.get(i)) + " km");
        }
    }

    // Display the shortest route among all possible routes
    public void displayShortestRoute() {
        if (allRoutes.isEmpty()) {
            System.out.println("No routes found to determine the shortest path.");
            return;
        }

        double minDistance = Double.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < routeDistances.size(); i++) {
            if (routeDistances.get(i) < minDistance) {
                minDistance = routeDistances.get(i);
                minIndex = i;
            }
        }

        if (minIndex != -1) {
            System.out.println("Shortest Route: " + String.join(" → ", allRoutes.get(minIndex)) +
                               " | Distance: " + String.format("%.2f", minDistance) + " km");
        } else {
            System.out.println("No route found to determine the shortest path.");
        }
    }
}
