import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ConsoleDriver {

	public static void main(String[] args) {
		//make a town graph manager
		TownGraphManager tgm = new TownGraphManager();
		
		
		
		/*
		//TODO this section populates graph from adjacency matrix
		String[] townNames = {
				"Rockville",
				"Silver Spring",
				"Philadelphia",
				"Pittsburgh",
				"Baltimore",
				"Cleveland",
				"New York City",
		};
		int[][] adjacencies = {
				{  0,  13, 142, 225,  40, 352, 227},//Rockville
				{ 13,   0, 136, 237,  34, 363, 222},//Silver Spring
				{141, 135,   0, 305, 101, 432,  97},//Philadelphia
				{226, 237, 304,   0, 248, 133, 371},//Pittsburgh
				{ 40,  34, 106, 248,   0, 374, 192},//Baltimore
				{352, 364, 431, 133, 375,   0, 462},//Cleveland
				{228, 222,  97, 370, 188, 462,   0}//New York City
		};
		try {
			tgm.populateTownGraph(adjacencies, townNames);
			System.out.println("graph populated successfully\n");
		} catch (RuntimeException e) {
			System.out.println("graph failed to populate");
			System.exit(1);
		}
		*/
		
		
		//TODO this section populates graph from file tsp.txt
		try {
			tgm.populateTownGraph(new File("tsp.txt"));
			System.out.println("graph populated successfully\n");
		} catch (FileNotFoundException e) {
			System.out.println("graph failed to populate");
			System.exit(1);
		}
		
		
		
		
		
		
		
		//find nearest neighbor path from Rockville
		System.out.println("Nearest neighbor starting from Rockville:");
		ArrayList<Town> nn = tgm.nearestNeighbor("Rockville");
		for (Road r: tgm.getRoads(nn)) {
			System.out.println(r);
		}
		System.out.println("total: " + tgm.measureCycle(nn));
		System.out.println();
		//find branch and bound path from Rockville
		System.out.println("Branch and bound:");
		ArrayList<Town> bnb = tgm.branchAndBound("Rockville");
		for (Road r: tgm.getRoads(bnb)) {
			System.out.println(r);
		}
		System.out.println("total: " + tgm.measureCycle(bnb));
	}
	
}
