package Dijkstra;

public class Edge {
    private Vertix source;
    private Vertix destination;

    private double distance; // Represents the distance calculated from coordinates
    private double cost;     // Represents the cost provided from the file
    private double time;     // Represents the time provided from the file or calculated

    public Edge(Vertix source, Vertix destination, double cost, double time) {
        this.source = source;
        this.destination = destination;
        this.cost = cost; // Cost provided from the file
        this.time = time; // Time provided from the file
        this.distance = calculateDistance(); // Always calculate distance
    }

    /**
     * Calculate the distance between the source and destination
     * using the Haversine formula.
     */
    private double calculateDistance() {
        double latSource = Math.toRadians(source.getCapital().getLatitude());
        double longSource = Math.toRadians(source.getCapital().getLongitude());
        double latDes = Math.toRadians(destination.getCapital().getLatitude());
        double longDes = Math.toRadians(destination.getCapital().getLongitude());

        double diffBetweenLat = latDes - latSource;
        double diffBetweenLong = longDes - longSource;

        double res = Math.pow(Math.sin(diffBetweenLat / 2), 2)
                + Math.pow(Math.sin(diffBetweenLong / 2), 2) * Math.cos(latSource) * Math.cos(latDes);
        double rad = 6371; // Radius of Earth in kilometers
        double c = 2 * Math.asin(Math.sqrt(res));
        return rad * c; // Distance in kilometers
    }

    

    // Getters and Setters
    public Vertix getSource() {
        return source;
    }

    public Vertix getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
