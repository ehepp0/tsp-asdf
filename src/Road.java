/**
 * Represents a road for a town graph.
 * A road is an edge connecting two vertices (towns) existing in the graph.
 * @author Rick
 *
 */
public class Road implements Comparable<Road> {
	private Town source, destination;
	private int distance;
	private String name;
	/**
	 * Creates a new road.
	 * @param source town the road originates from
	 * @param destination town the road points to
	 * @param distance the road's distance in miles
	 * @param name the name of the road
	 */
	public Road(Town source, Town destination, int distance, String name) {
		this.source = source;
		this.destination = destination;
		this.distance = distance;
		this.name = source + " to " + destination;
	}
	/**
	 * Creates a new road with a default distance of 1 mile.
	 * @param source town the road originates from
	 * @param destination town the road points to
	 * @param name name the name of the road
	 */
	public Road(Town source, Town destination, String name) {
		this(source, destination, 1, name);
	}
	/**
	 * Determines if a town is either the source or destination of the road.
	 * @param town town to check
	 * @return true if the road contains the town, false otherwise
	 */
	public boolean contains(Town town) {
		return source.equals(town) || destination.equals(town);
	}
	/**
	 * Gets the town this road originates from
	 * @return source town of the road
	 */
	public Town getSource() {
		return source;
	}
	/**
	 * Gets the town this road points to
	 * @return destination town of the road
	 */
	public Town getDestination() {
		return destination;
	}
	/**
	 * Gets the distance in miles of the road
	 * @return the distance in miles of the road
	 */
	public int getWeight() {
		return distance;
	}
	/**
	 * Gets the name of the road
	 * @return the road's name
	 */
	public String getName() {
		return name;
	}
	@Override
	public int compareTo(Road o) {
		if (this.equals(o)) {
			return 0;
		}
		return 1;
	}
	@Override
	public boolean equals(Object o) {
		try {
			Road other = (Road) o;
			return source.equals(other.getSource()) && destination.equals(other.getDestination());
		} catch (ClassCastException e) {
			return false;
		}
	}
	@Override
	public String toString() {
		return source + " to " + destination + ": " + distance + " mi";
	}
}
