import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Stable roommates problem
 * 
 * @author Aashish Dhungana
 * 
 */

public class Roommates {

	public int[][] preferences;
	public AVAILABILITY[] availability;
	public List<Nodes> allNodes = new ArrayList<>();
	Result[][] results;
	ArrayList<MatchedPair> matchedPair = new ArrayList<>();

	enum AVAILABILITY {
		FREE, SEMIENGAGED
	}

	public void initialize(List<Nodes> allNodes, Result[][] result) {
		this.allNodes = allNodes;
		preferences = new int[allNodes.size()][this.allNodes.size()];
		availability = new AVAILABILITY[allNodes.size()];

		this.results = new Result[allNodes.size()][allNodes.size()];
		// checkIfNull(this.results);
		for (int i = 0; i < allNodes.size(); i++) {
			availability[i] = AVAILABILITY.FREE;
			for (int j = 0; j < allNodes.size() - 1; j++) {
				preferences[i][j] = -1;
			}
			preferences[i][allNodes.size() - 1] = i;
		}

		this.results = result;
	
		

	}

	public Roommates(String filename, List<Nodes> allNodes, Result[][] result) throws IOException {

		// System.out.println(this.results[0][61].sourceNode.index);
		initialize(allNodes, result);

		int count = 0;
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		if (reader != null) {
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				for (int i = 0; i < values.length; i++) {
					preferences[count][i] = Integer.parseInt(values[i].trim());

				}
				count++;
			}

		}

		reader.close();

		//phase1();
	}

	
	public int getIndexOf(int[] preferences, int x) {
		for (int i = 0; i < preferences.length; i++) {
			if (preferences[i] == x) {
				return i;
			}
		}
		return -1;
	}

	// phase 1 of roommates algorithm
	public boolean phase1() {
		int x = nextPerson();
		while (x != -1) {
			int y = getHead(x);
			int z = getSemiEngaged(y);
			if (z != -1) {
				assignFree(z); // y rejects z
			}
			assignSemiEngaged(x); // assign x to be semiengaged to y
			int xIndex = getIndexOf(preferences[y], x);
			// System.out.println("# proposal: " + (x.+1) + " --> " + (y+1));
			for (int i = xIndex + 1; i < preferences[y].length; i++) {
				int deletedId = preferences[y][i];
				if (deletedId != -1) {
					delete(deletedId, y); // delete pair
					
				}
			}
			x = nextPerson(); // get next free person
		}
		boolean stat = resultPhase1();
		return stat;
	}

	// get the first person in preference list of someone
	public int getHead(int id) {
		int[] personPreferences = preferences[id];
		for (int i = 0; i < personPreferences.length; i++) {
			int head = personPreferences[i];
			if (head != -1) {
				return head;
			}
		}
		return -1;
	}

	// get the second person in preference list of someone
	public int getSecond(int id) {
		int[] personPreferences = preferences[id];
		for (int i = 0; i < personPreferences.length; i++) {
			int n = personPreferences[i];
			if (n != -1 && n != getHead(id)) {
				return n;
			}
		}
		return -1;
	}

	// delete a pair
	public void delete(int a, int b) {
		int[] aArray = preferences[a];
		int[] bArray = preferences[b];
		int aPosition = getIndexOf(bArray, a);
		int bPosition = getIndexOf(aArray, b);
		aArray[bPosition] = -1;
		bArray[aPosition] = -1;

	}

	// get the next free person
	
	public int nextPerson() {
		for (int i = 0; i < preferences.length; i++) {
			if (availability[i] == AVAILABILITY.FREE && !isListEmpty(i)) {
				return i;
			}
		}
		return -1;
	}

	// check if someone list is empty
	public boolean isListEmpty(int id) {
		int[] personPreferences = preferences[id];
		for (int i = 0; i < personPreferences.length; i++) {
			if (personPreferences[i] != -1) {
				return false;
			}
		}
		return true;
	}

	// check if someone list has only one entry
	public boolean isListSingle(int id) {
		int numEmpty = 0;
		int[] personPreferences = preferences[id];
		for (int i = 0; i < personPreferences.length; i++) {
			if (personPreferences[i] == -1) {
				numEmpty++;
			}
		}
		if (numEmpty == personPreferences.length - 1) {
			return true;
		} else {
			return false;
		}
	}

	// check if all lists have only one entry
	public boolean isAllListsSingle() {
		for (int i = 0; i < preferences.length; i++) {
			if (!isListSingle(i)) {
				return false;
			}
		}
		return true;
	}

	// check if an empty list exists
	public boolean existEmptyList() {
		for (int i = 0; i < preferences.length; i++) {
			if (isListEmpty(i)) {
				return true;
			}
		}
		return false;
	}

	// get the next list that have many entries
	public int nextListWithMultipleEntries() {
		for (int i = 0; i < preferences.length; i++) {
			if (!isListSingle(i) && !isListEmpty(i)) {
				return i;
			}
		}
		return -1;
	}

	// assign a person to be semiengaged
	public void assignSemiEngaged(int id) {
		availability[id] = AVAILABILITY.SEMIENGAGED;
	}

	// assign a person to be free
	public void assignFree(int id) {
		availability[id] = AVAILABILITY.FREE;
	}

	public ArrayList<MatchedPair> getMatchedPair() {
		return this.matchedPair;
	}

	// get the next free person which have in his/her head the person passed in
	// parameter
	// we use this to get a person current partner and then to break the
	// marriage
	public int getSemiEngaged(int id) {
		for (int i = 0; i < preferences.length; i++) {
			if (availability[i] == AVAILABILITY.SEMIENGAGED && getHead(i) == id) {
				return i;
			}
		}
		return -1;
	}

	// print the result for phase 1. if necessary, phase 2 is called
	public boolean resultPhase1() {
		boolean stat = false;
		int numEmpty = 0;
		int numSingle = 0;
		for (int i = 0; i < preferences.length; i++) {
			if (isListEmpty(i)) {
				numEmpty++;
			} else if (isListSingle(i)) {
				numSingle++;
			}
		}
		if (numEmpty > 0) {
			System.out.println("- result: no stable matching.");
			stat = false;
			return stat;
			//System.exit(1);
		} else if (numSingle == preferences.length) {

			System.out.println("- result: stable matching found:");
			stat = true;
			
			
			printFinalResult();
			return stat;

		} else {

			stat = phase2();
			
			return stat;
		}
	}

	// get the list by passing the head value
	public int getIdFromHead(int head) {
		for (int i = 0; i < preferences.length; i++) {
			if (getHead(i) == head) {
				return i;
			}
		}
		return -1;
	}

	
	// phase1 phase 2 -- find rotations and reduce lists
	public boolean phase2() {
		boolean stat = false;
		int counterRotations = 1;
		while (!isAllListsSingle() && !existEmptyList()) {
			int id = nextListWithMultipleEntries();
			int head = getHead(id);
			List<Integer> xSet = new ArrayList<Integer>();
			List<Integer> ySet = new ArrayList<Integer>();
			xSet.add(id);
			ySet.add(head);
			int second = getSecond(id);
			while (!ySet.contains(second)) {
				int nextX = getIdFromHead(second);
				xSet.add(nextX);
				ySet.add(second);
				second = getSecond(nextX);
			}
//			/System.out.print("#rotation(" + counterRotations + "): ");
			for (int i = 0; i < ySet.size(); i++) {
				System.out.print("{" + (xSet.get(i) + 1) + "," + (ySet.get(i) + 1) + "} ");
				if (i == 0) {
					handleRotations(ySet.get(i), xSet.get(xSet.size() - 1));
				} else {
					handleRotations(ySet.get(i), xSet.get(i - 1));
				}
			}
			counterRotations++;
			System.out.println();
		}
		if (existEmptyList()) {
			System.out.println("- result: no stable matching.");
			stat = false;
			return stat;
			//System.exit(0);
		} else {
			System.out.println("- result: stable matching found phase 2:");
			printFinalResult();
			stat= true;
			return stat;
		}
	}

	// handle rotations, removing required people from required lists
	public void handleRotations(int id, int removedFrom) {
		int[] list = preferences[id];
		int removeFromIndex = getIndexOf(list, removedFrom);
		for (int i = removeFromIndex + 1; i < list.length; i++) {
			int n = list[i];
			if (n != -1) {
				removeFromList(n, id);
				list[i] = -1;
			}
		}
	}

	// remove an entry from a list
	public void removeFromList(int id, int removed) {
		int[] list = preferences[id];
		int pos = getIndexOf(list, removed);// list.indexOf(removed);
		list[pos] = -1;
	}

	// print the final result
	public void printFinalResult() {

		List<Integer> included = new ArrayList<>();
		for (int i = 0; i < this.allNodes.size(); i++) {
			int matched = getHead(i);
			

			if (!included.contains(matched) && !included.contains(i)) {
				Result resA = new Result();
				resA = this.results[i][matched];
				Result resB = new Result();
				resB = this.results[matched][i];
				// checkIfNull(this.results);
				if (this.results[i][matched] == null || this.results[matched][i] == null) {
					System.out.println(this.results[i][matched]);
					System.exit(0);
				}

				MatchedPair pair = new MatchedPair(i, matched, resA, resB);
				System.out.println(i + " - > " + matched);

				if (pair != null) {
					this.matchedPair.add(pair);
					included.add(matched);
					included.add(i);
				}
			}
		}

		System.out.println("***********STABLE MATCH COMPLETE *************************");
		//printFullTable();
	}

	// print the full table
	public void printFullTable() {
		for (int i = 0; i < preferences.length; i++) {
			System.out.print((i + 1) + "\t" + availability[i] + "\t");
			for (Integer id : preferences[i]) {
				System.out.print((id + 1) + " ");
			}
			System.out.println();
		}
	}

}