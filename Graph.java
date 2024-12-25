package Dijkstra;


public class Graph {
    private int numberOfVertices;
    private HashTable hashTable;

    public Graph(int numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
        hashTable = new HashTable(this.numberOfVertices);
    }

    public void addVertix(Vertix vertix) {
        hashTable.put(vertix);
    }

    public Vertix getVertix(String CapitalName) {
        return hashTable.getVertex(CapitalName);
    }

    public HashTable getHashTable() {
        return hashTable;
    }
    public void addEdge(String sourceName, String destinationName, double cost, double time) {
        Vertix source = getVertix(sourceName);
        Vertix destination = getVertix(destinationName);

        if (source == null || destination == null) {
            System.err.println("Source or destination vertex not found: " + sourceName + ", " + destinationName);
            return;
        }

        if (source.equals(destination)) {
            System.out.println("Self-loops are not allowed: " + sourceName + " -> " + destinationName);
            return;
        }

        Edge edge = new Edge(source, destination, cost, time);
        source.addEdge(edge);
    }


    public HeapNode getOptimalPath(String source, String destination, String priority) {
        Vertix sourceVertix = this.getVertix(source);

        if (sourceVertix == null) {
            System.err.println("Source vertex not found: " + source);
            return null;
        }

        Heap heap = new Heap(numberOfVertices);
        LinkedList path = new LinkedList();

        path.addLast(new Edge(sourceVertix, sourceVertix, 0, 0)); // Self-loop with zero cost/time
        HeapNode startNode = new HeapNode(sourceVertix, 0, path);
        heap.insert(startNode);

        while (!heap.isEmpty()) {
            HeapNode currentNode = heap.remove();

            // If the destination is reached
            if (currentNode.getVertix().getCapital().getCapitalName().equals(destination)) {
                hashTable.setAllVerticesToFalse(); // Reset visited status for all vertices
                return currentNode;
            }

            if (currentNode.getVertix().isVisited()) {
                continue;
            }

            currentNode.getVertix().setVisited(true);

            LinkedList neighbors = currentNode.getVertix().getVertices();
            LinkedListNode neighborNode = neighbors.getFirstNode();

            while (neighborNode != null) {
                Edge edge = neighborNode.getEdge();

                if (edge.getSource().equals(edge.getDestination())) {
                    neighborNode = neighborNode.getNext();
                    continue; // Skip self-loops
                }

                LinkedList newPath = new LinkedList();
                newPath.addAll(currentNode.getPath());
                newPath.addLast(edge);

                // Determine the weight based on priority
                double weight = switch (priority.toLowerCase()) {
                    case "cost" -> edge.getCost();
                    case "time" -> edge.getTime();
                    case "distance" -> edge.getDistance();
                    default -> throw new IllegalArgumentException("Invalid priority: " + priority);
                };

                double newMetric = currentNode.getCost() + weight;

                HeapNode newHeapNode = new HeapNode(
                    edge.getDestination(),
                    newMetric,
                    newPath
                );

                heap.insert(newHeapNode);
                neighborNode = neighborNode.getNext();
            }
        }

        hashTable.setAllVerticesToFalse(); // Reset visited status for all vertices
        return null; // No path found
    }

    
}
