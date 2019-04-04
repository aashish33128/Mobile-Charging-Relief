import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author Aashish Dhungana
 *  Provides all the valid Node objects from the output file
 *         returned by python script.
 */
public class NodeSamples {

	ArrayList<Nodes> allNodes = new ArrayList<>();
	long startTime;
	long endTime;
	
	
	public NodeSamples(long startTime, long endTime)
	{
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public int loadData(String filename) {

		try {

			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = "";

			while ((line = reader.readLine()) != null) {
				Nodes node = processLine(line);
				if (node != null && node.chargingPatterns.size() > 0 && !allNodes.contains(node)) {
					allNodes.add(node);
				}

			}
			reader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allNodes.size();

	}

	
	public int loadData(String filename, boolean newData) {

		try {

			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = "";

			while ((line = reader.readLine()) != null) {
				Nodes node = processLineForNewData(line);
				if (node != null && node.chargingPatterns.size() > 0 && !allNodes.contains(node)) {
					allNodes.add(node);
				}

			}
			reader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allNodes.size();

	}

	/**
	 * Certain line contains invalid date. As we need time info, we ignore such
	 * lines.
	 */
	public boolean checkIfValidTimes(String[] times) {

		for (int i = 1; i < times.length; i++) {
			if (times[i].compareTo("(invalid date)") == 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * process each line from all-selected file and get the charging info for
	 * each node. Requires time as well as level at each point. Need to discard
	 * small change as it might be fluctuations in real data.
	 */
	public Nodes processLine(String lines) {
		String listCharge = lines.replace(", ** ,", "##").split("##")[0];
		String listTimes = lines.split("\\*\\*")[1];
		String[] line = listCharge.split(",");
		String[] time = listTimes.split(",");
		Nodes node = new Nodes(this.startTime,this.endTime);
		Date baseTime = Utilities.getDate(time[1]);
		
		int i = 1;
		ArrayList<ChargingPattern> listOfPatterns = new ArrayList<>();
		try {
			boolean valid = checkIfValidTimes(time);

			if (valid) {
				// Since the index goes to i+2
				while (i <= line.length - 3) {

					int initPoint = Integer.parseInt(line[i].trim());
					String initTime = time[i];

					int secondPoint = Integer.parseInt(line[i + 1]);

					String secondTime = time[i + 1];

					int totalCharge = 0;
					int totalDischarge = 0;
					int finalPoint = 0;
					String finalTime = "";
					ChargingPattern cp = null;
					
					if (initPoint > secondPoint) {
						totalCharge = 0;
						totalDischarge = initPoint - secondPoint;
						finalPoint = secondPoint;
						finalTime = secondTime;
						i++;
						cp = new ChargingPattern(initPoint, initPoint, initPoint, finalPoint);
						cp.totalCharge = 0;
						cp.totalDischarge = totalDischarge;
						
						cp.cts = Utilities.getMinutes(initTime,baseTime);
						
						
						cp.cte = Utilities.getMinutes(initTime,baseTime);
						cp.dts = Utilities.getMinutes(initTime,baseTime);
						cp.dte = Utilities.getMinutes(finalTime,baseTime);

						// Starts From Discharging, Can occur at the beginning.

					} else {
						int thirdPoint = Integer.parseInt(line[i + 2]);
						String thirdTime = time[i + 2];

						totalCharge = secondPoint - initPoint;
						totalDischarge = secondPoint - thirdPoint;
						finalPoint = thirdPoint;
						finalTime = thirdTime;
						i = i + 2;
						cp = new ChargingPattern(initPoint, secondPoint, secondPoint, finalPoint);
						cp.totalCharge = totalCharge;
						cp.totalDischarge = totalDischarge;
						cp.cts = Utilities.getMinutes(initTime,baseTime);
						
						cp.cte = Utilities.getMinutes(secondTime,baseTime);
						cp.dts = Utilities.getMinutes(secondTime,baseTime);
						cp.dte = Utilities.getMinutes(finalTime,baseTime);
					}

					
					if (finalTime.isEmpty()) {
						System.out.println("Error for " + line[0]);
					}

					if (cp != null)
						listOfPatterns.add(cp);

				}
		// Create a new Node Object.

				node.nodeId = line[0];
				//System.out.println(node.nodeId);
				node.chargingPatterns = listOfPatterns;
			//	node.totalCycles = node.getTotalCycles();

			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Exception In " + listCharge + "at index " + i + "," + line.length);
			e.printStackTrace();
			// /System.exit(0);
		}
		return node;
	}
	
	
	
	
	/**
	 * process each line from all-selected file and get the charging info for
	 * each node. Requires time as well as level at each point. Need to discard
	 * small change as it might be fluctuations in real data.
	 */
	public Nodes processLineForNewData(String lines) {
		String listCharge = lines.replace("**", "##").split("##")[0];
		String listTimes = lines.replace("**", "##").split("##")[1];
		String[] line = listCharge.split(",");
		String[] time = listTimes.split(",");
		Nodes node = new Nodes(this.startTime,this.endTime);
		//Date baseTime = Utilities.getDate(time[1]);
		int i = 0;
		ArrayList<ChargingPattern> listOfPatterns = new ArrayList<>();
		try {
			boolean valid = checkIfValidTimes(time);

			if (valid) {
				// Since the index goes to i+2
				while (i <= line.length - 3) {

					int initPoint = Integer.parseInt(line[i].trim());
					String initTime = time[i];

					int secondPoint = Integer.parseInt(line[i + 1].trim());

					String secondTime = time[i + 1];

					int totalCharge = 0;
					int totalDischarge = 0;
					int finalPoint = 0;
					String finalTime = "";
					ChargingPattern cp = null;
					
					if (initPoint > secondPoint) {
						totalCharge = 0;
						totalDischarge = initPoint - secondPoint;
						finalPoint = secondPoint;
						finalTime = secondTime;
						i++;
						cp = new ChargingPattern(initPoint, initPoint, initPoint, finalPoint);
						cp.totalCharge = 0;
						cp.totalDischarge = totalDischarge;
						
						cp.cts = Long.valueOf(initTime.trim());
						
						
						cp.cte = Long.valueOf(initTime.trim());
						cp.dts = Long.valueOf(initTime.trim());
						cp.dte = Long.valueOf(finalTime.trim());

						// Starts From Discharging, Can occur at the beginning.

					} else {
						int thirdPoint = Integer.parseInt(line[i + 2].trim());
						String thirdTime = time[i + 2];

						totalCharge = secondPoint - initPoint;
						totalDischarge = secondPoint - thirdPoint;
						finalPoint = thirdPoint;
						finalTime = thirdTime;
						i = i + 2;
						cp = new ChargingPattern(initPoint, secondPoint, secondPoint, finalPoint);
						cp.totalCharge = totalCharge;
						cp.totalDischarge = totalDischarge;
						cp.cts = Long.valueOf(initTime.trim());
						
						cp.cte =Long.valueOf(secondTime.trim());// Utilities.getMinutes(secondTime,baseTime);
						cp.dts = Long.valueOf(secondTime.trim());
						cp.dte = Long.valueOf(finalTime.trim());
					}

					
					if (finalTime.isEmpty()) {
						System.out.println("Error for " + line[0]);
					}

					if (cp != null)
						listOfPatterns.add(cp);

				}
		// Create a new Node Object.

				node.nodeId = line[0];
				node.chargingPatterns = listOfPatterns;
			//	node.totalCycles = node.getTotalCycles();

			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Exception In " + listCharge + "at index " + i + "," + line.length);
			e.printStackTrace();
			// /System.exit(0);
		}
		return node;
	}

}
