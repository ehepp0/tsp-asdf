import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class TownGraphManager {
	private TownGraph townGraph;
	/**
	 * Creates a new town graph manager.
	 * This creates a new town graph for the class to manipulate.
	 */
	public TownGraphManager() {
		townGraph = new TownGraph();
	}
	
	public boolean addRoad(String town1, String town2, int weight, String roadName) {
		Town t1 = getTown(town1);
		Town t2 = getTown(town2);
		try {
			return townGraph.addEdge(t1, t2, weight, roadName) != null;
		} catch (NullPointerException e) {
			//
		} catch (IllegalArgumentException e) {
			//
		}
		return false;
	}

	public String getRoad(String town1, String town2) {
		Town t1 = getTown(town1);
		Town t2 = getTown(town2);
		return townGraph.getEdge(t1, t2).getName();
	}

	public boolean addTown(String v) {
		return townGraph.addVertex(new Town(v));
	}

	public Town getTown(String name) {
		for (Town town: townGraph.vertexSet()) {
			if (town.getName().equals(name)) {
				return town;
			}
		}
		return null;
	}

	public boolean containsTown(String v) {
		return townGraph.containsVertex(new Town(v));
	}

	public boolean containsRoadConnection(String town1, String town2) {
		Town t1 = getTown(town1);
		Town t2 = getTown(town2);
		return townGraph.containsEdge(t1, t2);
	}

	public ArrayList<String> allRoads() {
		ArrayList<String> result = new ArrayList<>();
		for (Road r: townGraph.edgeSet()) {
			result.add(r.getName());
		}
		Collections.sort(result);
		return result;
	}

	public boolean deleteRoadConnection(String town1, String town2, String road) {
		Town t1 = getTown(town1);
		Town t2 = getTown(town2);
		return townGraph.removeEdge(t1, t2, -1, road) != null;
	}

	public boolean deleteTown(String v) {
		return townGraph.removeVertex(new Town(v));
	}

	public ArrayList<String> allTowns() {
		ArrayList<String> result = new ArrayList<>();
		for (Town t: townGraph.vertexSet()) {
			result.add(t.getName());
		}
		Collections.sort(result);
		return result;
	}

	public void populateTownGraph(File selectedFile) throws FileNotFoundException {
		Scanner fileScanner = new Scanner(selectedFile);
		//read each line from the file
		while (fileScanner.hasNextLine()) {
			//lines should be formatted as follows:
			//roadName,weight;source;destination
			String[] roadInfo = fileScanner.nextLine().split(";");
			String town1name = roadInfo[1];
			String town2name = roadInfo[2];
			String roadName = roadInfo[0].split(",")[0];
			int roadWeight = Integer.parseInt(roadInfo[0].split(",")[1]);
			//add the towns first
			townGraph.addVertex(new Town(town1name));
			townGraph.addVertex(new Town(town2name));
			//add the road
			townGraph.addEdge(getTown(town1name), getTown(town2name), roadWeight, roadName);
		}
		fileScanner.close();
	}
	
	public void populateTownGraph(int[][] adjacencyMatrix, String[] townNames) {
		//make sure info is valid
		if (townNames.length != adjacencyMatrix.length) {
			throw new RuntimeException("town names list doesn't match adjacency matrix");
		}
		for (int[] row: adjacencyMatrix) {
			if (row.length != adjacencyMatrix.length) {
				throw new RuntimeException("adjacency matrix is not square");
			}
		}
		int vertexCount = townNames.length;
		//add all the towns
		for (String town: townNames) {
			townGraph.addVertex(new Town(town));
		}
		//add all the vertices
		for (int source = 0; source < vertexCount; source++) {
			for (int dest = 0; dest < vertexCount; dest++) {
				if (source != dest) {
					int weight = adjacencyMatrix[source][dest];
					if (weight > 0) {
						Town sourceTown = getTown(townNames[source]);
						Town destTown = getTown(townNames[dest]);
						townGraph.addEdge(sourceTown, destTown, weight, "placeholder road name");
					}
				}
			}
		}
	}
	
	public ArrayList<Town> nearestNeighbor(String townName) {
		return townGraph.nearestNeighbor(getTown(townName));
	}
	
	public ArrayList<Town> branchAndBound(String townName) {
		return townGraph.branchAndBound(getTown(townName));
	}
	
	public int measureCycle(ArrayList<Town> path) {
		return townGraph.measureCycle(path);
	}
	
	public ArrayList<Road> getRoads(ArrayList<Town> path) {
		return townGraph.getRoadsInCycle(path);
	}
}
