/**
 * @author Aashish Dhungana
 * Simulates the Charging Skip Optimization algorithm with a dynamic programming approach
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationMain {
	List<Nodes> allNodes = new ArrayList<>();
	double tS = 0.5;
	double tE = 1;
	double lossRate_Beta = 0.0;
	ArrayList<ArrayList<Double>> yAxis_cc = new ArrayList<>();
	ArrayList<ArrayList<Double>> yAxis_ce = new ArrayList<>();
	ArrayList<ArrayList<Double>> yAxis_er = new ArrayList<>();
	// Main function for transfer efficiency
	static BufferedWriter writer = null;
	static BufferedWriter writer1 = null;
	ArrayList<Double> skipsPairs = new ArrayList<>();
	ArrayList<ArrayList<Double>> skipsPairs_in = new ArrayList<>();
	
	
	public Nodes updateNodes(Nodes node, int index) {
		
	
		node.index = index;
		return node;
	}

	
	/**
	 * 
	 * @param allNodes
	 * @return
	 * 
	 */
	public List<Nodes> selectNodes(List<Nodes> allNodes) {
		List<Nodes> allNodesSelected = new ArrayList<>();
		//int[] selectedNodes = { 2, 5, 3, 32, 7, 15, 9, 28, 13, 49, 18, 46, 20, 45, 34, 44,4,26,16,37,1,22,12,41};
		
		//These nodes are selected from the original data depending on their behaviour. Too much aggressive discharging and charging nodes are discarded.
		int[] selectedNodes = {0,4,1,110,5,32,6,87,8,75,29,41,11,17,12,182,13,175,14,107,15,145,153,109,18,36,19,102,21,35,22,152,24,168,25,49,26,176,27,121,28,122,30,169,31,52,33,79,74,96};
		
		Arrays.sort(selectedNodes);
		
		
		
		for (int i = 0; i < selectedNodes.length; i++) {
				allNodesSelected.add(allNodes.get(selectedNodes[i]));
			
			
			
		}
		//System.out.println();

		return allNodesSelected;

	}

	public static void main(String[] args) throws IOException {

		// System.out.println("*****STARTING EXPERIMENT*********");
		long startTime = 1440; // 24 hrs in minutes
		int numDays = 9;
		
		long endTime = numDays *  24 * 60 + startTime; // 96 hrs in minutes 5760/60 (4 days)
		List<Double> tSs = new ArrayList<>();
		
		/**
		 * To run for each transfer speed, uncomment this part
		 */
		
		/**
		 * tSs.add(0.2);
		 * tSs.add(0.4);		
		  tSs.add(0.6);
		  tSs.add(0.8);
		 */
		 
		
		tSs.add(1.0); 
		
		 BufferedWriter inSkips = new BufferedWriter(new FileWriter("skips_pairs.csv"));

		tSs.parallelStream().forEach((tS) -> {

	

			List<Double> tEs = new ArrayList<>();
			
			/**
			 * To run for each transfer efficiency, uncomment this part
			 */
			/**
			 * tEs.add(0.2);
			 * tEs.add(0.4);
			 * tEs.add(0.6);
			 * tEs.add(0.8);
			 */
			
			tEs.add(1.0);

		/*	String content1 = "Transfer Efficiency, Average Savings Cycles";
			String content2 = "Transfer Efficiency, Average Savings Energy";*/
			
			String content1 = "Days , Average Savings Cycles";
			String content2 = "Transfer Efficiency, Average Savings Energy";


			try {
				writer = new BufferedWriter(new FileWriter("avg_cycles_ts_haggle_"+tS + ".csv"));
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			try {
				writer1 = new BufferedWriter(new FileWriter("avg_energy_ts_haggle_"+tS + ".csv"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				writer.write(content1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer1.write(content2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.newLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				writer1.newLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			tEs.parallelStream().forEach((tE) -> {

				ApplicationMain main = new ApplicationMain();

				int dataset = 2; // 0 for mit; 1 for cambridge; 2 for haggle
				int count = 0;
				ArrayList<Double> y_temp = new ArrayList<>();
				ArrayList<Double> y_temp_energy = new ArrayList<>();
				main.yAxis_cc = new ArrayList<>();
				main.yAxis_ce = new ArrayList<>();
				while (count < 10) {
					main.checkAndDeleteFile();

					if (main.yAxis_cc.size() != count) {
						System.out.println("ERROR *** " + count + " , " + main.yAxis_cc.size());
						// System.exit(0);
					} else {
						for (int c = 0; c < count; c++) {
							if (main.yAxis_cc.get(c).size() < 1) {
								System.out.println("***ERROR***INNER : " + main.yAxis_cc.size() + " , "
										+ main.yAxis_cc.get(c).size());
								
							}
						}
					}

					System.out.println("-----------RUNNNING COUNT " + count + "---------------------------------");
					
					boolean stat = false;
					try {
						stat = main.effeciencySpeedBasedResults(tE, tS, y_temp, y_temp_energy, main.skipsPairs, startTime, endTime,
								dataset, numDays);
					//	stat = main.TimeBasedResults(y_temp, startTime, endTime, dataset, tE, tS, main.lossRate_Beta, numDays);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (stat) {
						count++;

					} else {
						System.out.println("-----SKIPPED-------------");
						///System.exit(0);
					}

				}

			try {
					writer.write(String.valueOf(tE) + " , " + Utilities.getAverage(main.yAxis_cc, 0));
					writer1.write(String.valueOf(tE) + " , " + Utilities.getAverage(main.yAxis_ce, 0));
					writer.newLine();
					writer1.newLine();
					
					System.out.println("Total Size " + main.skipsPairs_in.size());
					ArrayList<Double> finalArray = main.setAverageFortenRuns(main.skipsPairs_in);
					System.out.println("Final Size : " + finalArray.size());
					for(int i=0;i<finalArray.size();i++)
					{
					inSkips.write(String.valueOf("pair" + i + " , " + finalArray.get(i)));
					inSkips.newLine();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/**
				 * Generate Graph per day (Independant)
				 */
				
			/*	int day = 1;
				
				System.out.println("SiZE OF YAXIS " + main.yAxis_cc.size());
				for(int i=0;i<numDays;i++)
				{
					try {
						writer.write(String.valueOf(day) + " , " + Utilities.getAverage(main.yAxis_cc, i));
						writer.newLine();
						day = day +1;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	}
				*/
				/**
				 * Generate Graph per day (Cumulative)
				 */
				
			/*	int days = 1;
				
				System.out.println("SiZE OF YAXIS " + main.yAxis_cc.size());
				for(int i=0;i<numDays;i++)
				{
					try {
						writer1.write(String.valueOf(days) + " , " + Utilities.getAverage(main.yAxis_cc, i));
						writer1.newLine();
						days= days +1;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	}*/
				
				
				/**
				 * Generate Graph per 2 hours
				 */
				/*int hr = 0;
				
				System.out.println("SiZE OF YAXIS " + main.yAxis_cc.size());
				for(int i=0;i<=(numDays*24)/2;i++)
				{
					try {
						writer.write(String.valueOf(hr) + " , " + Utilities.getAverage(main.yAxis_cc, i));
						writer.newLine();
						hr = hr +2;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	}*/
				System.out.println("---------COMPLETE-----------------");
				// break;

			});

		});

		try {
			writer.close();
			writer1.close();
			inSkips.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// for(double tS = 0.0f; tS<=1.1f;tS=tS+0.2f)
		{

		}

		/*
		 * writer.write("1.0" + " , " + Utilities.getAverage(main.yAxis_cc, 0));
		 * writer1.write("1.0"+ " , " + Utilities.getAverage(main.yAxis_ce, 0));
		 * writer.newLine(); writer1.newLine();
		 */

	}

	
	public ArrayList<Double> setAverageFortenRuns(ArrayList<ArrayList<Double>> arrays)
	{
		
		ArrayList<Double> finalArray = new ArrayList<>();
		System.out.println("Arrays SIze " + arrays.get(0).size());
			for(int j=0; j<arrays.get(0).size();j++)
			{
				double sum = 0;
				for(int i=0;i<arrays.size();i++)
				{
				sum = sum + arrays.get(i).get(j);
				}
				
				sum = sum/(double)arrays.size();
				
				finalArray.add(sum);
			}
			
			
			System.out.println("Final Array " + finalArray.size());
			return finalArray;
		
		
	}
	public boolean effeciencySpeedBasedResults(double tE, double tS, ArrayList<Double> y_temp,
			ArrayList<Double> y_energy_temp, ArrayList<Double> skipPairs, long startTime, long endTime, int dataset, int numDays) throws IOException {
		System.out.println("*****STARTING EXPERIMENT*********");

		System.out.println("-------------------------------------******** RUNNING FOR TE *****--------------" + tE);
		// System.exit(0);
		// First Run for Selfish Case
		// System.out.println("***MAKING DECISIONS FOR CYCLES IN RANGE " +
		// startTime + " To " + endTime + "******");
		NodeSamples samples = new NodeSamples(startTime, endTime);
		int size = samples.loadData("D:/DTN Research/Energy Sharing Journal version/Python/mergedoutput.csv");
		// int size = samples.loadData("data.csv");
		 System.out.println("*****Total Node samples = " + size);
		this.allNodes = samples.allNodes;

		/**
		 * We need only the data for two days. Since the data has more charging
		 * information from 11:00 pm for the current day, we will start our day
		 * from 24 hrs.
		 */
		
		this.allNodes = Utilities.returnNodesWithTimeFrame(startTime, endTime, this.allNodes);
		this.allNodes = Utilities.checkForTimeGaps(this.allNodes);
		
		int countIndex = 0;
		for (int i = 0; i < this.allNodes.size(); i++) {
			updateNodes(allNodes.get(i), countIndex);
			countIndex++;
		}

		System.out.println("Num of Nodes " + this.allNodes.size());
		// System.exit(0);

		// getAverageChargingAndDischargingDuration();
		Result[][] res = new Result[this.allNodes.size()][this.allNodes.size()];
		this.initializeResult(res);

		System.out.println("***********Running for Selfish Case *******");

		ArrayList<Double> savings = new ArrayList<>();
		for (int j = 0; j < 1; j++) {
			double sum = 0;
			for (int i = 0; i < this.allNodes.size(); i++) {
				SelfishAlgorithm algoSelfish = new SelfishAlgorithm(this.allNodes.get(i), 0, startTime, endTime);
				algoSelfish.runAlgorithm();

				// System.out.println(" ??? " +
				// this.allNodes.get(i).selfishRes.totalSkips);
				double cycleSavings = ((double) this.allNodes.get(i).selfishRes.totalSkips
						/ (double) this.allNodes.get(i).totalCycles) * 100;
				
				
				sum = sum + cycleSavings;
				// System.out.println("Savings " + cycleSavings);
			}
			savings.add(sum / (double) this.allNodes.size());
		}
		double sum = 0;
		for (int i = 0; i < savings.size(); i++) {
			sum = sum + savings.get(i);
		}

		System.out.println("---------Total savings for selfish is " + (sum / (double) savings.size()));
		// System.exit(0);
		// getResultsForSelfish(startTime, endTime);
		boolean stat = this.init_Application(y_temp, y_energy_temp,skipPairs, tE, tS, startTime, endTime, res, dataset, numDays);
		return stat;
	}

	public boolean TimeBasedResults(ArrayList<Double> y_cc,/* ArrayList<Double> y_ce, ArrayList<Double> y_er,*/
			long startTime, long endTime, int dataset, double tE, double tS, double lossRate_Beta, int numDays) throws IOException {

		// System.out.println("***MAKING DECISIONS FOR CYCLES IN RANGE " +
		// startTime + " To " + endTime + "******");
		NodeSamples samples = new NodeSamples(startTime, endTime);
		int size = samples.loadData("D:/DTN Research/Energy Sharing Journal version/Python/mergedoutput.csv");
		// int size = samples.loadData("data.csv");
		 System.out.println("*****Total Node samples = " + size);
		this.allNodes = samples.allNodes;

		/**
		 * We need only the data for two days. Since the data has more charging
		 * information from 11:00 pm for the current day, we will start our day
		 * from 24 hrs.
		 */
		
		this.allNodes = Utilities.returnNodesWithTimeFrame(startTime, endTime, this.allNodes);
		this.allNodes = Utilities.checkForTimeGaps(this.allNodes);
		
		int countIndex = 0;
		for (int i = 0; i < this.allNodes.size(); i++) {
			updateNodes(allNodes.get(i), countIndex);
			countIndex++;
		}
		
		/*if(this.allNodes.size()%2!=0)
		{
			this.allNodes.remove(this.allNodes.size()-1);
			//this.allNodes = this.allNodes.subList(0, 100);
		}
		
		this.allNodes.remove(19);
		this.allNodes.remove(20);
		this.allNodes.remove(21);
		
		
		
		countIndex = 0;
		for (int i = 0; i < this.allNodes.size(); i++) {
			updateNodes(allNodes.get(i), countIndex);
			countIndex++;
		}
		
		//this.allNodes.clear();
		List<Nodes> finalList = new ArrayList<>();
		finalList = this.selectNodes(this.allNodes);
		
		
		countIndex = 0;
		
	
		for (int i = 0; i < finalList.size(); i++) {
			updateNodes(finalList.get(i), countIndex);
			countIndex++;
		}
		
		
		this.allNodes = finalList;*/
		
	/*	for(int i =0; i<finalList.size();i++)
		{
			System.out.print(finalList.get(i).nodeId + " \n");
		}
		*/
		
	//	this.allNodes = this.allNodes.subList(0, 100);
		
		System.out.println("Total Size after filter " + this.allNodes.size());
	
	//System.exit(0);
		//this.allNodes = this.selectNodes(this.allNodes);

		// check the last partial charging cycles.

		// checkLastPartialCycles();

		// this.testPattern(1);
		// System.out.println("*********Total Nodes within Time Frame :
		// ********* " + this.allNodes.size());
		Result[][] res = new Result[this.allNodes.size()][this.allNodes.size()];
		this.initializeResult(res);
		System.out.println("***********Running for Selfish Case *******");

		for (int i = 0; i < this.allNodes.size(); i++) {
			SelfishAlgorithm algoSelfish = new SelfishAlgorithm(this.allNodes.get(i), lossRate_Beta, startTime, endTime);
			algoSelfish.runAlgorithm();
		}

		int count = 0;

		// System.out.println("------------RuNNING COOPERATIVE------------- " +
		// this.allNodes.size());
		for (int i = 0; i < this.allNodes.size() - 1; i++) {
			for (int j = i + 1; j < this.allNodes.size(); j++) {
				count++;
				 System.out.println("--------Constructing decision blocks for"
				+" ------" + i + ", " + j);
				DecisionPoints dBlocks = new DecisionPoints(this.allNodes.get(i), this.allNodes.get(j), startTime,
						endTime);
				ArrayList<ChargingPattern> cpA = dBlocks.cpA;
				ArrayList<ChargingPattern> cpB = dBlocks.cpB;

				// System.out.println("Total Size of decision blocks with dummy
				// inserted " + cpA.size());

				// System.out.println("**SET MEETING INFO FOR SKIP
				// ALGORITHM**");
				MeetingTimeConfigurationManager config = new MeetingTimeConfigurationManager();
				MeetingTime meet = new MeetingTime();
				config.setMeetingParameters(dataset, meet, (int) startTime, numDays);

				// System.out.println("***RUNNING SKIP ALGORITHM ****");
				SkipAlgorithm algo = new SkipAlgorithm(this.allNodes.get(i), this.allNodes.get(j), cpA.size(), cpA, cpB,
						res, meet, startTime, endTime, tE, tS);
				algo.run();
				res = algo.getResult();
				System.out.println("Skip Algorithm Finished with " + count + " total pairs" + " total Results "
						+ res[0].length + "  " + res.length);

				/**
				 * Run pair selection method to select only the optimal pairs.
				 * Uses stable roommate matching algorithm
				 */

			}
			// break;

		}

		 this.writeResultsToFile("OriginalOutput.csv", res);

		// Run Roommate Matching Algorithm to find possible pairs for each
		// nodes.

		System.out.println("************Running Room mate matching problem************");

		Result[][] result = new Result[this.allNodes.size()][this.allNodes.size()];
		for (int i = 0; i < this.allNodes.size(); i++) {
			for (int j = i + 1; j < this.allNodes.size(); j++) {
				result[i][j] = new Result(res[i][j]);
				result[j][i] = new Result(res[j][i]);

			}
		}
		new GeneratePreferences(this.allNodes, result, tS);

		Roommates mates = new Roommates("preferences" +tS+".csv", this.allNodes, res);
		boolean stat = mates.phase1();
		if (stat) {
			ArrayList<MatchedPair> matchedPairs = mates.getMatchedPair();
			Graph graphPlot = new Graph(startTime, endTime, matchedPairs, this.allNodes, res);
			//y_cc = graphPlot.getSavingsCyclesPerTime("cooperative");
			//y_cc = graphPlot.getSavingsCyclesPerDay("cooperative",3); //numDays = 3.For Independant days Change this when start time and end time changes
			y_cc = graphPlot.getSavingsCyclesDay("cooperative",9); //numDays = 3. For cumulative days Change this when start time and end time changes
		/*	y_ce = graphPlot.getSavingsEnergyPerTime("cooperative");
			y_er = graphPlot.getEnergyReceivedPerTime("cooperative");*/
			this.yAxis_cc.add(y_cc);
			/*this.yAxis_ce.add(y_ce);
			this.yAxis_er.add(y_er);*/

			System.out.println();
			return true;
		} else {
			System.out.println("No stable matching");
		//	System.exit(0);
			return false;
		}

	}

	public void initializeResult(Result[][] res) {
		for (int i = 0; i < this.allNodes.size(); i++) {
			for (int j = i + 1; j < this.allNodes.size(); j++) {
				res[i][j] = new Result();
				res[j][i] = new Result();
			}
		}
	}

	public String getCapacity(Nodes node) {

		int min = 100;
		for (int i = 0; i < node.chargingPatterns.size(); i++) {

			if (node.chargingPatterns.get(i).dle < min) {
				min = node.chargingPatterns.get(i).dle;
			}
		}

		return String.valueOf(min);
	}

	/**
	 * 
	 * @param resA
	 * @param resB
	 * @return Total Spent for resA
	 */
	public String getSpent(Result resA, Result resB) {
		int totalSpent = 0;
		if (resA.totalReceivedEnergy > resB.totalReceivedEnergy) {
			totalSpent = resA.totalReceivedEnergy - resB.totalReceivedEnergy;
		}

		return String.valueOf(totalSpent);
	}

	public void debugEnergyExchange(Result[][] res) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("energyExchangesCapacities.csv"));
			writer.write(
					"Node A, Node B,Node A energy spending capacity, Node A energy spent, Node B energy spending capacity, Node B energy spent, Node A selfish skips, Node A total skips, Node B selfish skips, Node B total skips");
			writer.newLine();
			for (int i = 0; i < this.allNodes.size(); i++) {

				// System.out.println("i is " + i);
				for (int j = i + 1; j < this.allNodes.size(); j++) {
					if (res[i][j] != null) {
						String content = res[i][j].sourceNode.index + " , " + res[j][i].sourceNode.index + ","
								+ getCapacity(res[i][j].sourceNode) + "," + getSpent(res[i][j], res[j][i]) + ","
								+ getCapacity(res[j][i].sourceNode) + "," + getSpent(res[j][i], res[i][j]) + ", "
								+ res[i][j].sourceNode.selfishRes.totalSkips + ", " + res[i][j].totalSourceSkips + " , "
								+ res[j][i].sourceNode.selfishRes.totalSkips + ", " + res[j][i].totalSourceSkips;
						writer.write(content);
						writer.newLine();

					}
				}
			}

			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void writeResultsToFile(String filename, Result[][] res) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
			writer.write(
					"Node 1, Node 2, total cycles A ,  total source energy , total received energy ,Selfish A, Selfish A skip sequence, total Skips A,last charging type A,skip sequence A,  Original Skip Sequence A, Energy Sequence A,  total cycles B, total source energy, total received energy, total selfish skips B,  selfish B skip sequence, total Skips B,last charging type B,skip sequence B,  Original Skip Sequence B, Energy Sequence B, Skip Ratio A, Energy Ratio A , skip Ratio B, Energy Ratio B, Num Of meetings");
			writer.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < this.allNodes.size(); i++) {

			// System.out.println("i is " + i);
			for (int j = i + 1; j < this.allNodes.size(); j++) {
				if (res[i][j] != null) {

					StringBuilder content = new StringBuilder();
					content.append(res[i][j].sourceNode.index + ", " + res[j][i].sourceNode.index + ","
							+ res[i][j].totalSourceCycles + ", " + res[i][j].totalsourceEnergy + " , "
							+ res[i][j].totalReceivedEnergy + " , " + res[i][j].sourceNode.selfishRes.totalSkips
							+ " , ");

					for (int a = 0; a < res[i][j].sourceNode.selfishRes.originalSkips.size(); a++) {

						content.append(res[i][j].sourceNode.selfishRes.originalSkips.get(a) + " - ");
					}

					content.append(",");
					content.append(res[i][j].totalSourceSkips);

					content.append(",");

					for (int a = 0; a < res[i][j].dpSource.size() - 1; a++) {
						content.append(res[i][j].dpSource.get(a).patternType + " - ");
					}
					content.append(",");
					for (int a = 0; a < res[i][j].skipSequence_A.length - 1; a++) {

						content.append(res[i][j].skipSequence_A[a] + " - ");
					}

					content.append(",");
					// int sum = 0;
					// content.append(",");
					for (int a = 0; a < res[i][j].originalSkips_Source.size(); a++) {

						content.append(res[i][j].originalSkips_Source.get(a) + " - ");
					}

					content.append(",");
					for (int a = 0; a < res[i][j].energySequence_A.length - 1; a++) {

						content.append(res[i][j].energySequence_A[a] + " - ");
					}

					content.append(",");
					content.append(res[j][i].totalSourceCycles + " , " + res[j][i].totalsourceEnergy + " , "
							+ res[j][i].totalReceivedEnergy + ", " + res[j][i].sourceNode.selfishRes.totalSkips
							+ " , ");

					for (int a = 0; a < res[j][i].sourceNode.selfishRes.originalSkips.size(); a++) {

						content.append(res[j][i].sourceNode.selfishRes.originalSkips.get(a) + " - ");
					}

					content.append(",");
					content.append(res[j][i].totalSourceSkips + " , ");
					// content.append(",");

					for (int a = 0; a < res[j][i].dpSource.size() - 1; a++) {
						content.append(res[j][i].dpSource.get(a).patternType + " - ");
					}
					content.append(",");
					for (int a = 0; a < res[j][i].skipSequence_A.length - 1; a++) {

						content.append(res[j][i].skipSequence_A[a] + " - ");
					}
					content.append(",");
					for (int a = 0; a < res[j][i].originalSkips_Source.size(); a++) {

						content.append(res[j][i].originalSkips_Source.get(a) + " - ");
					}
					content.append(",");

					for (int a = 0; a < res[j][i].energySequence_A.length - 1; a++) {

						content.append(res[j][i].energySequence_A[a] + " - ");
					}

					content.append(", " + res[i][j].getSkipRatio() + " , " + res[i][j].getEnergyRatio() + ", "
							+ res[j][i].getSkipRatio() + " , " + res[j][i].getEnergyRatio() + " , "
							+ res[i][j].meet.hours.size());
					writer.write(content.toString());
					writer.newLine();
					// break;

				}

			}
			// break;
		}

		writer.close();
		// System.exit(0);
	}

	public void checkAndDeleteFile() {
		File f1 = new File("preferences.csv");
		if (f1.exists()) {
			f1.delete();
		}
		File f2 = new File("OriginalOutput.csv");
		if (f2.exists()) {
			f2.delete();
		}
		File f3 = new File("optimalMatching.csv");
		if (f3.exists()) {
			f3.delete();
		}
	}

	public boolean init_Application(ArrayList<Double> y_cycles, ArrayList<Double> y_energy, ArrayList<Double> skipPairs, double transferEfficiency,
			double transferSpeed, long startTime, long endTime, Result[][] res, int dataset, int numDays) throws IOException {
		int count = 0;
		initializeResult(res);
		// checkAndDeleteFile();
		for (int i = 0; i < this.allNodes.size() - 1; i++) {
			for (int j = i + 1; j < this.allNodes.size(); j++) {
				count++;
				// System.out.println("--------Constructing decision blocks for
				// ------" + i + ", " + j);
				DecisionPoints dBlocks = new DecisionPoints(this.allNodes.get(i), this.allNodes.get(j), startTime,
						endTime);
				ArrayList<ChargingPattern> cpA = dBlocks.cpA;
				ArrayList<ChargingPattern> cpB = dBlocks.cpB;

				// System.out.println("Total Size of decision blocks with dummy
				// inserted " + cpA.size());

				System.out.println("**SET MEETING INFO FOR SKIP ALGORITHM**");
				MeetingTimeConfigurationManager config = new MeetingTimeConfigurationManager();
				MeetingTime meet = new MeetingTime();
				config.setMeetingParameters(dataset, meet, (int) startTime, numDays);

				/* //**Debug Meeting Information **//*
													 * System.out.println(
													 * "Node A " + i + " , " +
													 * " Node B " + j);
													 * 
													 * System.out.println(
													 * "MIT INFO : ");
													 * MeetingTime m = new
													 * MeetingTime(); config.
													 * setMeetingParameters(1,
													 * m);
													 * 
													 * System.out.println();
													 * for(int hrs:m.hours) {
													 * System.out.println(hrs+
													 * " - "
													 * +m.duration.get(hrs)); }
													 * 
													 * System.out.println();
													 */
				// System.exit(0);

				System.out.println("***RUNNING SKIP ALGORITHM ****");
				SkipAlgorithm algo = new SkipAlgorithm(this.allNodes.get(i), this.allNodes.get(j), cpA.size(), cpA, cpB,
						res, meet, startTime, endTime, transferEfficiency, transferSpeed);
				algo.run();
				res = algo.getResult();
				System.out.println("Skip Algorithm Finished with " + count + " total pairs" + " total Results "
						+ res[0].length + "  " + res.length);
						// System.exit(0);
						/**
						 * Run pair selection method to select only the optimal
						 * pairs. Uses stable roommate matching algorithm
						 */
				// break;

			}
			// break;

		}

		this.writeResultsToFile("OriginalOutput.csv", res);
		this.debugEnergyExchange(res);
		System.out.println("************Running Room mate matching problem************");

		//System.exit(0);

		Result[][] result = new Result[this.allNodes.size()][this.allNodes.size()];
		for (int i = 0; i < this.allNodes.size(); i++) {
			for (int j = i + 1; j < this.allNodes.size(); j++) {
				result[i][j] = new Result(res[i][j]);
				result[j][i] = new Result(res[j][i]);

			}
		}
		new GeneratePreferences(this.allNodes, result, transferSpeed);

		Roommates mates = new Roommates("preferences" + transferSpeed + ".csv", this.allNodes, res);
		boolean stat = mates.phase1();

		if (stat) {

			ArrayList<MatchedPair> matchedPairs = mates.getMatchedPair();
			//findOptimalPairs(matchedPairs);
			Graph graphPlot = new Graph(startTime, endTime, matchedPairs, this.allNodes, res);
			y_cycles = graphPlot.graph13(endTime);
			skipPairs = graphPlot.getInSkips(endTime);
			y_energy = graphPlot.graph14(endTime);

			this.yAxis_cc.add(y_cycles);
			this.yAxis_ce.add(y_energy);
			this.skipsPairs_in.add(skipPairs);
			return true;
		} else {

			System.out.println("-------------STABLE MATCHING FAILED----------");
			return false;
		}

		// System.out.println();

	}

	public void getAverageChargingAndDischarging() {
		int countC = 0;
		int charge = 0;
		int discharge = 0;
		int countD = 0;

		for (int i = 0; i < this.allNodes.size(); i++) {
			for (int j = 0; j < this.allNodes.get(i).chargingPatterns.size(); j++) {
				if (this.allNodes.get(i).chargingPatterns.get(j).totalCharge > 0) {
					charge = charge + this.allNodes.get(i).chargingPatterns.get(j).totalCharge;
					countC++;
				}
				if (this.allNodes.get(i).chargingPatterns.get(j).totalDischarge > 0) {
					discharge = discharge + this.allNodes.get(i).chargingPatterns.get(j).totalDischarge;
					countD++;
				}

			}
		}

		double cAvg = (double) charge / (double) countC;
		double dAvg = (double) discharge / (double) countD;

		System.out.println("Total chargings " + cAvg);
		System.out.println("Total Discharging " + dAvg);

		System.exit(0);
	}

	public void getAverageChargingAndDischargingDuration() {
		int countC = 0;
		long chargeDuration = 0;
		int countD = 0;
		long dischargeDuration = 0;

		for (int i = 0; i < this.allNodes.size(); i++) {
			for (int j = 0; j < this.allNodes.get(i).chargingPatterns.size(); j++) {
				if (this.allNodes.get(i).chargingPatterns.get(j).totalCharge > 0) {
					// charge = charge +
					// this.allNodes.get(i).chargingPatterns.get(j).totalCharge;
					chargeDuration = chargeDuration + this.allNodes.get(i).chargingPatterns.get(j).cte
							- this.allNodes.get(i).chargingPatterns.get(j).cts;
					countC++;
				}
				if (this.allNodes.get(i).chargingPatterns.get(j).totalDischarge > 0) {
					dischargeDuration = dischargeDuration + this.allNodes.get(i).chargingPatterns.get(j).dte
							- this.allNodes.get(i).chargingPatterns.get(j).dts;
					countD++;
				}

			}
		}

		double cAvg = (double) chargeDuration / (double) countC;
		double dAvg = (double) dischargeDuration / (double) countD;

		System.out.println("Total chargings " + cAvg);
		System.out.println("Total Discharging " + dAvg);

		System.exit(0);
	}
	
	public void findOptimalPairs(ArrayList<MatchedPair> matchedPairs)
	{
		//Find pairs whose total cooperative skip is atleast >2 than total selfish
		List<Nodes> selectedNodes = new ArrayList<>();
		
		for(int i=0; i<matchedPairs.size();i++)
		{
			int totalSelfish = matchedPairs.get(i).resultA.sourceNode.selfishRes.totalSkips + matchedPairs.get(i).resultB.sourceNode.selfishRes.totalSkips;
			int totalCooperative = matchedPairs.get(i).resultA.totalSourceSkips + matchedPairs.get(i).resultB.totalSourceSkips;
			if(totalCooperative > totalSelfish + 2)
			{
				selectedNodes.add(matchedPairs.get(i).resultA.sourceNode);
				selectedNodes.add(matchedPairs.get(i).resultB.sourceNode);
			}
			
		}
		
		//Print Information
		System.out.println("Total Selected Nodes : " + selectedNodes.size());
		for(int i=0;i<selectedNodes.size();i++)
		{
			System.out.println(selectedNodes.get(i) + ",");
		}
		
		System.exit(0);
		
	}

}
