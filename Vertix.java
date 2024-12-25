package Dijkstra;

public class Vertix {
	private Capital Capital;
	private LinkedList vertices = new LinkedList();
	private boolean visited;

	public Vertix(Capital Capital) {
		this.Capital = Capital;
		this.visited = false;
	}
	public void addEdge(Edge edge) {
	    // Prevent self-loops
	    if (edge.getSource().equals(edge.getDestination())) {
	        System.out.println("Skipping self-loop: " + edge.getSource().getCapital().getCapitalName());
	        return;
	    }
	    this.vertices.addLast(edge);
	}


	
	public Capital getCapital() {
		return Capital;
	}

	public LinkedList getVertices() {
		return vertices;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	@Override
	public int hashCode() {
		return Capital.getCapitalName().hashCode();
	}
}
