import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

public class TownGraph {
	private HashMap<Town, LinkedList<Road>> adjacencies;
	private static final int NO_PATH = Integer.MAX_VALUE;

	public TownGraph() {
		adjacencies = new HashMap<>();
	}
	
	public Road getEdge(Town sourceVertex, Town destinationVertex) {
		//check if road exists
		if (!(containsEdge(sourceVertex, destinationVertex))) {
			return null;
		}
		//go through adjacencies list for source vertex
		for (Road r: edgesOf(sourceVertex)) {
			//return edge if found
			if (r.getDestination().equals(destinationVertex)) {
				return r;
			}
		}
		return null;
	}

	public Road addEdge(Town sourceVertex, Town destinationVertex, int weight, String description) {
		//check if towns exist
		if (sourceVertex == null || destinationVertex == null) {
			throw new NullPointerException();
		}
		if (!(containsVertex(sourceVertex) && containsVertex(destinationVertex))) {
			throw new IllegalArgumentException();
		}
		//remove any edge this would be replacing
		removeEdge(sourceVertex, destinationVertex, -1, null);
		//make edge
		Road result = new Road(sourceVertex, destinationVertex, weight, description);
		//add edge
		adjacencies.get(sourceVertex).add(result);
		return result;
	}

	public boolean addVertex(Town v) {
		if (containsVertex(v)) {
			return false;
		}
		adjacencies.put(v, new LinkedList<>());
		return true;
	}

	public boolean containsEdge(Town sourceVertex, Town destinationVertex) {
		//check if towns exist- if towns don't exist, road can't exist
		if (!(containsVertex(sourceVertex) && containsVertex(destinationVertex))) {
			return false;
		}
		//go through adjacencies list for source vertex
		for (Road r: edgesOf(sourceVertex)) {
			//return true if found
			if (r.getDestination().equals(destinationVertex)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsVertex(Town v) {
		return adjacencies.keySet().contains(v);
	}

	public Set<Road> edgeSet() {
		HashSet<Road> result = new HashSet<>();
		for (Town t: adjacencies.keySet()) {
			for (Road r: edgesOf(t)) {
				if (!result.contains(r)) {
					result.add(r);
				}
			}
		}
		return result;
	}

	public Set<Road> edgesOf(Town vertex) {
		HashSet<Road> result = new HashSet<>();
		for (Road r: adjacencies.get(vertex)) {
			result.add(r);
		}
		return result;
	}

	public Road removeEdge(Town sourceVertex, Town destinationVertex, int weight, String description) {
		//check if road exists
		if (!containsEdge(sourceVertex, destinationVertex)) {
			return null;
		}
		//make a list of roads to delete
		LinkedList<Road> edges = new LinkedList<>();
		//find road in source adjacency list
		for (Road r: edgesOf(sourceVertex)) {
			boolean weightMatch = weight < 0 || weight == r.getWeight();
			boolean nameMatch = description == null || description.equals(r.getName());
			boolean townMatch = r.getSource().equals(sourceVertex) 
					&& r.getDestination().equals(destinationVertex);
			boolean reverseTownMatch = r.getSource().equals(sourceVertex) 
					&& r.getDestination().equals(destinationVertex);
			if (weightMatch && nameMatch && (townMatch || reverseTownMatch)) {
				edges.add(r);
			}
		}
		//remove roads
		for (Road r: edges) {
			adjacencies.get(r.getSource()).remove(r);
		}
		try {
			return edges.getFirst();
		} catch (NoSuchElementException e) {
			return null;
		}
		
	}

	public boolean removeVertex(Town v) {
		//check if town exists
		if (v == null || !containsVertex(v)) {
			return false;
		}
		//go through edge list for v, remove all edges
		//remove reverse direction edges too
		for (Road r: edgesOf(v)) {
			removeEdge(v, r.getDestination(), r.getWeight(), r.getName());
			removeEdge(r.getDestination(), v, r.getWeight(), r.getName());
		}
		//remove town from key set
		adjacencies.remove(v);
		return true;
	}

	public Set<Town> vertexSet() {
		//keyset can be changed by other methods
		//so we need to make a copy instead of returning the keyset itself
		Set<Town> result = new HashSet<>();
		for (Town town: adjacencies.keySet()) {
			result.add(town);
		}
		return result;
	}
	
	public ArrayList<Town> nearestNeighbor(Town sourceVertex) {
		//make a list to store the order of towns visited
		ArrayList<Town> cycle = new ArrayList<>();
		//add the first town
		cycle.add(sourceVertex);
		while (cycle.size() < vertexSet().size()) {
			//find the nearest neighbor to the last entry in the path
			//keep track of current nearest neighbor and distance to it
			Town nearestNeighbor = null;
			int distanceToNearest = NO_PATH;
			for (Town neighbor: vertexSet()) {
				if (!cycle.contains(neighbor)) {
					//if a town hasn't been visited yet, compare to nearest neighbor
					Road roadToNeighbor = getEdge(cycle.get(cycle.size()-1), neighbor);
					if (roadToNeighbor.getWeight() < distanceToNearest) {
						//update nearest neighbor and distance to it as appropriate
						nearestNeighbor = neighbor;
						distanceToNearest = roadToNeighbor.getWeight();
					}
				}
			}
			if (nearestNeighbor == null) {
				throw new RuntimeException("nearest neighbor failed: no path");
			}
			cycle.add(nearestNeighbor);
		}
		return cycle;
	}
	
	public ArrayList<Town> branchAndBound(Town sourceVertex) {
		//create an initial partial solution containing sourceVertex
		ArrayList<Town> partialSolution = new ArrayList<>();
		partialSolution.add(sourceVertex);
		//branch and bound can also run with no starting vertex
		//a cycle can start/end at any town and still travel the same roads
		//including a starting town will just determine which town it starts at
		ArrayList<Town> path = branchAndBound(partialSolution, NO_PATH);
		//recursive method will include the first town again at the end
		//remove the town at the end for an ordered list of towns with no repeats
		path.remove(path.size()-1);
		return path;
	}
	
	private ArrayList<Town> branchAndBound(ArrayList<Town> partialSolution, int bestWeight) {
		//base case 1: partial cycle is already worse than the best complete cycle found
		if (measurePath(partialSolution) >= bestWeight) {
			return null;
		}
		//base case 2: all towns are visited
		if (partialSolution.size() == vertexSet().size()) {
			//add the first town to the end of the path
			partialSolution.add(partialSolution.get(0));
			//this is a full solution now
			return partialSolution;
		}
		//recursive case: still need to add towns to the partial solution
		//save the best solution and its total path weight
		int currentBestWeight = bestWeight;
		ArrayList<Town> bestSolution = null;
		//look at all unvisited towns:
		for (Town candidateTown: vertexSet()) {
			if (partialSolution.contains(candidateTown)) {
				//don't revisit any towns
			} else {
				//copy the partial solution so we can add the next town to it
				//type cast is safe because it is a copy of another object of the same type
				@SuppressWarnings("unchecked")
				ArrayList<Town> nextSolution = (ArrayList<Town>) partialSolution.clone();
				//add candidate town to path
				nextSolution.add(candidateTown);
				//find remaining solution from this path
				nextSolution = branchAndBound(nextSolution, currentBestWeight);
				//if this solution is better than the current best one, save it
				if (measurePath(nextSolution) < currentBestWeight) {
					bestSolution = nextSolution;
					currentBestWeight = measurePath(nextSolution);
				}
			}
		}
		return bestSolution;
	}
	
	private int measurePath(ArrayList<Town> path) {
		if (path == null) {
			return NO_PATH;
		}
		if (path.size() < 2) {
			return 0;
		}
		//start counter at 0
		int total = 0;
		//for each town in the cycle
		for (int i = 0; i < path.size()-1; i++) {
			//add the length of the road from the current town to the next town
			Town currentTown = path.get(i);
			Town nextTown = path.get(i+1);
			total += getEdge(currentTown, nextTown).getWeight();
		}
		return total;
	}
	
	public int measureCycle(ArrayList<Town> path) {
		if (path == null) {
			return NO_PATH;
		}
		if (path.size() < 2) {
			return 0;
		}
		//get path distance
		int total = measurePath(path);
		//if there is no path, return no-path value
		if (total == NO_PATH) {
			return total;
		}
		Town firstTown = path.get(0);
		Town lastTown = path.get(path.size()-1);
		//add the length of the road from the last town to the first town
		//this makes a cycle
		//if there is no road to complete the cycle, return no-path value
		try {
			total += getEdge(lastTown, firstTown).getWeight();
		} catch (NullPointerException e) {
			return NO_PATH;
		}
		
		return total;
	}
	
	public ArrayList<Road> getRoadsInCycle(ArrayList<Town> cycle) {
		//make a list to store roads in order
		ArrayList<Road> result = new ArrayList<>();
		//for each town in the cycle:
		for (int i = 0; i < cycle.size()-1; i++) {
			//add the road from the current town to the next town
			Town currentTown = cycle.get(i);
			Town nextTown = cycle.get(i+1);
			result.add(getEdge(currentTown, nextTown));
		}
		//add the road from the last town to the first town
		//this makes a cycle
		Town firstTown = cycle.get(0);
		Town lastTown = cycle.get(cycle.size()-1);
		result.add(getEdge(lastTown, firstTown));
		return result;
	}
}
