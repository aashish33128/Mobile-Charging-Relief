
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class SkipAlgorithm {
	Nodes nodeA, nodeB;
	ArrayList<ChargingPattern> decisionPointsChargesA = new ArrayList<>();
	ArrayList<ChargingPattern> decisionPointsChargesB = new ArrayList<>();
	int n = 0;
	int c = 100;
	int[][][] D = null;
	int[][][] D_Skip = null;
	int[][][] D_Charge = null;
	String[][][] track_charge = null;
	String[][][] track_skip = null;
	String track_main[][][] = null;
	int initialCharge_0 = 0;
	int initialCharge_1 = 0; // Set to 0 for single User case

	int meetingParameter_Alpha = 1;
	Result[][] result;
	boolean debugMode = false;
	public MeetingTime meet;
	long startTime = 0;
	long endTime = 0;
	// Debug method

	/**
	 * Controllable parameters.
	 */
	double lossRate_Beta = 0.0; // Loss incurred when skipping the current
								// Charge
	double tE = 1; // transfer Efficiency; transfer efficiency 1 translates to
					// receiver receives all energy sent by sender.
	double sharingRate = 0.5; // shares sharingRate energy in 1 time unit.

	public SkipAlgorithm() {

	}

	public void update(int time, int aCharge, int bCharge, int[][][] source, int[][][] dest, int incr, int residualA,
			int residualB, String[][][] trackM, int currentUser, int skipDecision, String sourceLabel) {
		
		//System.out.println("Time " + time + ", " + aCharge + ", " + bCharge);
		int currentValue = source[time][aCharge][bCharge];		
		
		if (currentValue != 500) {
			
		//	System.out.println("Time " + time);
			if(residualA >= 0 && residualB >=0)
			{
			int originalValue = dest[time + 1][residualA][residualB];
			
			
			if ((currentValue + incr) < originalValue) {
				//System.out.println("Getting Inside " + (currentValue + incr));
				dest[time + 1][residualA][residualB] = (currentValue + incr);
				
				//System.out.println("UPDATED TO " + dest[time + 1][residualA][residualB]);
				trackM[time + 1][residualA][residualB] = aCharge + "-" + bCharge + "-" + 0 + "-" + currentUser + "-"
						+ skipDecision + "-" + sourceLabel;
			}
			}
		}
	}

	public void updateEnergySharable(int time, int aCharge, int bCharge, int[][][] source, int[][][] dest, int incr,
			String[][][] trackM, int currentUser, int skipDecision, String sourceLabel, int energy, int wallCharge_A,
			int wallCharge_B, int discharge_A, int discharge_B) {
		
		
	//	System.out.println(" incr" + incr);
		if (energy > 0) {

			int currentValue = source[time][aCharge][bCharge];
			if (currentValue != 500) {
				for (int i = 0; i < energy; i++) {
				/*	int residualChargeA_shared = Math.min(100, aCharge + wallCharge_A - i) - discharge_A;
					int residualChargeB_received = (int) Math.round(Math.min(100, bCharge + wallCharge_B + (i * tE)))
							- discharge_B;

					int residualChargeA_received = (int) Math.round(Math.min(100, aCharge + wallCharge_A + (i * tE)))
							- discharge_A;
					int residualChargeB_shared = Math.min(100, bCharge + wallCharge_B - i) - discharge_B;*/
					
					
					int residualChargeA_shared = Math.min(100, aCharge + wallCharge_A) - i - discharge_A;
					int residualChargeB_received = (int) Math.round (Math.min(100, bCharge + wallCharge_B) + (i * tE))
							- discharge_B;

					int residualChargeA_received = (int) Math.round(Math.min(100, aCharge + wallCharge_A) + (i * tE))
							- discharge_A;
					int residualChargeB_shared =  Math.min(100,bCharge + wallCharge_B) - i - discharge_B;
					
					
					if(residualChargeA_shared > 100 || residualChargeB_received > 100 || residualChargeA_received > 100 || residualChargeB_shared > 100 )
					{
						break;
					}

					if (residualChargeA_shared >= 0 && residualChargeB_received >= 0) {

						int originalValue = dest[time + 1][residualChargeA_shared][residualChargeB_received];
						if ((currentValue + incr) <= originalValue) {
							dest[time + 1][residualChargeA_shared][residualChargeB_received] = (currentValue + incr);
							trackM[time + 1][residualChargeA_shared][residualChargeB_received] = aCharge + "-" + bCharge
									+ "-" + (int)(i ) + "_" + 0 + "-" + currentUser + "-" + skipDecision + "-" + sourceLabel;
						}
					}

					if (residualChargeA_received >= 0 && residualChargeB_shared >= 0) {

						int originalValue = dest[time + 1][residualChargeA_received][residualChargeB_shared];
						if ((currentValue + incr) <= originalValue) {
							dest[time + 1][residualChargeA_received][residualChargeB_shared] = (currentValue + incr);
							trackM[time + 1][residualChargeA_received][residualChargeB_shared] = aCharge + "-" + bCharge
									+ "-" + (int)(i) + "_" + 1 + "-" + currentUser + "-" + skipDecision + "-" + sourceLabel;
						}
					}
				}
			}

		}
	}

	public SkipAlgorithm(Nodes nodeA, Nodes nodeB, int totalNum, ArrayList<ChargingPattern> cpA,
			ArrayList<ChargingPattern> cpB, Result[][] result, MeetingTime meetInfo, long startTime, long endTime,
			double transferEfficiency, double transferSpeed) {

		this.nodeA = nodeA;
		this.nodeB = nodeB;
		this.result = result;
		this.n = totalNum;
		this.decisionPointsChargesA = cpA;
		this.decisionPointsChargesB = cpB;
		this.meet = meetInfo;
		this.startTime = startTime;
		this.endTime = endTime;
		this.tE = transferEfficiency;
		this.sharingRate = transferSpeed;

		// Last decision Points should not contain first Half
		if (n > 1) {
			cpA.get(n - 2).hasFirstHalf = false;
			if (cpA.get(n - 3).hasFirstHalf) {
				cpA.get(n - 2).hasSecondHalf = true;
			} else {
				cpA.get(n - 2).hasSecondHalf = false;
			}
			cpB.get(n - 2).hasFirstHalf = false;

			if (cpB.get(n - 3).hasFirstHalf) {
				cpB.get(n - 2).hasSecondHalf = true;
			} else {
				cpB.get(n - 2).hasSecondHalf = false;
			}

		}

		// First Decision point should not contain second half. Could occur
		// while filtering with time.
		cpA.get(0).hasSecondHalf = false;
		cpB.get(0).hasSecondHalf = false;

		// Need to force last charging cycle not to have any partial cycles.

		this.initialCharge_0 = this.decisionPointsChargesA.get(0).cls;
		this.initialCharge_1 = this.decisionPointsChargesB.get(0).cls;
	}

	public Result[][] getResult() {
		return this.result;
	}

	public void run() throws IOException {
		init();
		runDP_Updated_new();
		System.out.println("Algorithm Completed without errors");
		trackSkip_Updated(D, D_Skip, D_Charge, track_charge, track_skip, track_main, this.tE);
	}

	/**
	 * Initialize the D Matrix
	 */
	public void init() {

		D = new int[n][101][101];
		D_Skip = new int[n][101][101];
		D_Charge = new int[n][101][101];
		track_skip = new String[n][101][101];
		track_charge = new String[n][101][101];
		track_main = new String[n][101][101];

		if (debugMode)
			System.out.println("Initialize");

		for (int k = 0; k < n; k++) {
			for (int i = 0; i <= c; i++) {

				for (int j = 0; j <= c; j++) {

					D_Skip[k][i][j] = 500;
					D_Charge[k][i][j] = 500;
					D[k][i][j] = 500;
					track_skip[k][i][j] = "500";
					track_charge[k][i][j] = "500";
					track_main[k][i][j] = "500";
					// }
				}
			}

		}

		D[0][initialCharge_0][initialCharge_1] = 0;

	}

	public int getEnergySharable(int time, int askip, int bskip) {

		// case 0,0
		long userAStart = 0, userAEnd = 0, userBStart = 0, userBEnd = 0;
		if (askip == 0 && bskip == 0) {
			
			userAStart = this.decisionPointsChargesA.get(time).dts;
			userAEnd = this.decisionPointsChargesA.get(time).dte;
			userBStart = this.decisionPointsChargesB.get(time).dts;
			userBEnd = this.decisionPointsChargesB.get(time).dte;
			//return 50;
		}
		// case 0,1

		if (askip == 0 && bskip == 1) {
			userAStart = this.decisionPointsChargesA.get(time).dts;
			userAEnd = this.decisionPointsChargesA.get(time).dte;
			userBStart = this.decisionPointsChargesB.get(time).cts;
			userBEnd = this.decisionPointsChargesB.get(time).dte;
			//return 50;
		}
		// case 1,0

		if (askip == 1 && bskip == 0) {
			userAStart = this.decisionPointsChargesA.get(time).cts;
			userAEnd = this.decisionPointsChargesA.get(time).dte;
			userBStart = this.decisionPointsChargesB.get(time).dts;
			userBEnd = this.decisionPointsChargesB.get(time).dte;
			//return 50;
		}
		// case 1,1

		if (askip == 1 && bskip == 1) {
			userAStart = this.decisionPointsChargesA.get(time).cts;
			userAEnd = this.decisionPointsChargesA.get(time).dte;
			userBStart = this.decisionPointsChargesB.get(time).cts;
			userBEnd = this.decisionPointsChargesB.get(time).dte;
		//	return 50;
		}

		// System.out.println("-----------------------" + "FOR TIME : " + time +
		// " -----------------");
		ArrayList<Integer> hrs = meet.hours;
		int totalEnergy = 0;
		for (int i = 0; i < hrs.size(); i++) {

			
			  int ms = hrs.get(i);
			  int me = hrs.get(i) +
			  meet.duration.get(hrs.get(i));
			 

		/*	int ms = Math.round(userAStart);
			int me = Math.round(userAEnd);*/
			// if(ms>=userAStart)
			int energyPerMeeting = getOverlappingTimes(ms, me, userAStart, userAEnd, userBStart, userBEnd);

			// System.out.println("Overlapping duration " + energyPerMeeting);

			if (energyPerMeeting > 0) {
				// System.out.println("Meets at " + hrs.get(i));
				int energy = Math.min(100, (int) (energyPerMeeting * sharingRate));
				totalEnergy = totalEnergy + energy;
			}

			/*
			 * if(totalEnergy > 0 && askip == 1 && bskip ==0) {
			 * System.out.println(ms + "," + me + "," + userAStart + ","
			 * +userAEnd + "," + userBStart + "," + userBEnd + " diff " +
			 * energyPerMeeting + " , total energy shared " + totalEnergy) ; }
			 */

		}

		/*
		 * if(askip==1 && bskip==1) { return 100; }
		 */
			
		return totalEnergy;
			
		//return 100;
	}

	public int getEnergyFromWall(int time, int user) {

		// ChargingPattern pattern = this.decisionPointsCharges.get(time);
		if (user == 0) {
			return this.decisionPointsChargesA.get(time).totalCharge;
		} else {
			return this.decisionPointsChargesB.get(time).totalCharge;
		}
	}

	public int getDischargeAmount(int time, int user) {
		// DP3D_chargingPattern pattern = this.decisionPointsCharges.get(time);
		if (user == 0) {
			return this.decisionPointsChargesA.get(time).totalDischarge;
		} else {
			return this.decisionPointsChargesB.get(time).totalDischarge;
		}
	}

	public void printMatrix() {
		if (debugMode)
			System.out.println("Priniting Matrix");

		String content = "";
		for (int i = 0; i < n; i++) {

			content = "\n";
			// System.out.println("\n");
			for (int a = 0; a <= 100; a++) {
				for (int j = 0; j <= 100; j++) {
					content = content.concat(D[i][a][j] + ",");
				}
			}

			// System.out.println(content);
			outputToFile(content, new File("output.csv"));
		}

	}

	public void outputToFile(String contents, File file) {
		// System.out.println("--Writing to a file----" + file.getName());
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(contents);
			// writer.newLine();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void runDP_Updated_new() {
		int source = -1;
		boolean merged = true;

		if (debugMode)
			System.out.println("---------------N is " + this.n);
		for (int time = 0; time < n - 1; time++) {
			/*
			 * if(time==14) { break; }
			 */
			boolean preA = false;
			boolean preB = false;
			/*
			 * int energySharable_B = getEnergySharable(time, 1); int
			 * energySharable_A = getEnergySharable(time, 0);
			 */
			int wallCharge_A = getEnergyFromWall(time, 0);
			int wallCharge_B = getEnergyFromWall(time, 1);
			int discharge_A = getDischargeAmount(time, 0);
			int discharge_B = getDischargeAmount(time, 1);
			int energySharable_0 = getEnergySharable(time, 0, 0);
			int energySharable_1 = getEnergySharable(time, 0, 1);
			int energySharable_2 = getEnergySharable(time, 1, 0);
			int energySharable_3 = getEnergySharable(time, 1, 1);

			/*
			 * System.out.println("At time " +
			 * this.decisionPointsChargesA.get(time).cts + " - " +
			 * this.decisionPointsChargesA.get(time).dte + " Energy 0 : " +
			 * energySharable_0 + " , Energy 1 " + energySharable_1 +
			 * " , Energy 2  " + energySharable_2 + ", Energy 3 : " +
			 * energySharable_3);
			 */

			boolean aHasFirstHalf = this.decisionPointsChargesA.get(time).hasFirstHalf;
			boolean bHasFirstHalf = this.decisionPointsChargesB.get(time).hasFirstHalf;
			boolean aHasSecondHalf = this.decisionPointsChargesA.get(time).hasSecondHalf;
			boolean bHasSecondHalf = this.decisionPointsChargesB.get(time).hasSecondHalf;

			boolean isFirstHalf = (aHasFirstHalf || bHasFirstHalf) ? true : false;
			boolean isSecondHalf = (aHasSecondHalf || bHasSecondHalf) ? true : false;

			if (merged) {
				source = -1;
			} else {
				source = 0;
			}

			/**
			 * Added code : Jan 29/2018 to solve multiple partial splits
			 */
			if (time > 0) {
				preA = this.decisionPointsChargesA.get(time - 1).hasFirstHalf;
				preB = this.decisionPointsChargesB.get(time - 1).hasFirstHalf;
			}

			// Added code end

			for (int aCharge = 0; aCharge <= 100; aCharge++) {
				for (int bCharge = 0; bCharge <= 100; bCharge++) {

					int residualChargeB_Wall = Math.min(100,bCharge + wallCharge_B) - discharge_B;
					int residualChargeB_Skip = 0;
					if (this.decisionPointsChargesB.get(time).totalCharge > 0) {
						residualChargeB_Skip = bCharge - discharge_B
								- (int) (this.decisionPointsChargesB.get(time).totalCharge * lossRate_Beta);
					} else {
						residualChargeB_Skip = bCharge - discharge_B;
					}
					int residualChargeA_Wall =  Math.min(100,aCharge + wallCharge_A) - discharge_A;
					if(residualChargeA_Wall >100 || residualChargeB_Wall > 100)
					{
						break;
					}
					int residualChargeA_None = 0;

					if (this.decisionPointsChargesA.get(time).totalCharge > 0) {
						residualChargeA_None = aCharge - discharge_A
								- (int) (this.decisionPointsChargesA.get(time).totalCharge * lossRate_Beta);
					} else {
						residualChargeA_None = aCharge - discharge_A;
					}

					if (isFirstHalf) {

						if (source == -1) {

							if (aCharge == 0 && bCharge == 0) {
								if (debugMode)
									System.out.println("Splitting From Main " + time);
							}

							if (bHasFirstHalf) {

								// Split Main into B=0 & A = 0,1

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting Main into B = 0  and " + time);
								}

								// case A = 0, B = 0;
								update(time, aCharge, bCharge, D, D_Charge, 2, residualChargeA_Wall,
										residualChargeB_Wall, track_charge, 2, 0, "main");
								
								
								// case A = 1; B =0;
								update(time, aCharge, bCharge, D, D_Charge, 1, residualChargeA_None,
										residualChargeB_Wall, track_charge, 0, 1, "main");

								// Energy Exchanges
								// case 0,0
								updateEnergySharable(time, aCharge, bCharge, D, D_Charge, 2, track_charge, 2, 0, "main",
										energySharable_0, wallCharge_A, wallCharge_B, discharge_A, discharge_B);

								// case 1, 0
								updateEnergySharable(time, aCharge, bCharge, D, D_Charge, 1, track_charge, 0, 1, "main",
										energySharable_2, 0, wallCharge_B, discharge_A, discharge_B);

								// Split main into B=1 & A= 0, 1

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting Main into B = 1  and " + time);
								}

								// case a = 0, b = 1
								update(time, aCharge, bCharge, D, D_Skip, 1, residualChargeA_Wall, residualChargeB_Skip,
										track_skip, 1, 1, "main");
								// case b=1, a = 1
								update(time, aCharge, bCharge, D, D_Skip, 0, residualChargeA_None, residualChargeB_Skip,
										track_skip, 2, 1, "main");

								// Energy Exchanges
								// case a = 0, b = 1
								updateEnergySharable(time, aCharge, bCharge, D, D_Skip, 1, track_skip, 1, 1, "main",
										energySharable_1, wallCharge_A, 0, discharge_A, discharge_B);
								// case a = 1, b = 1
								updateEnergySharable(time, aCharge, bCharge, D, D_Skip, 0, track_skip, 2, 1, "main",
										energySharable_3, 0, 0, discharge_A, discharge_B);
							}

							// }
							if (aHasFirstHalf) {
								// Split Main into A =0 & B= 0,1

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting main into A = 0  and " + time);
								}

								// Case A = 0 and B = 0;
								update(time, aCharge, bCharge, D, D_Charge, 2, residualChargeA_Wall,
										residualChargeB_Wall, track_charge, 2, 0, "main");
								// case A = 0 and B = 1
								update(time, aCharge, bCharge, D, D_Charge, 1, residualChargeA_Wall,
										residualChargeB_Skip, track_charge, 1, 1, "main");
								// Energy shared
								// case a = 0, b = 0
								updateEnergySharable(time, aCharge, bCharge, D, D_Charge, 2, track_charge, 2, 0, "main",
										energySharable_0, wallCharge_A, wallCharge_B, discharge_A, discharge_B);
								// case a = 0, b = 1
								updateEnergySharable(time, aCharge, bCharge, D, D_Charge, 1, track_charge, 1, 1, "main",
										energySharable_1, wallCharge_A, 0, discharge_A, discharge_B);

								// Split Main into A = 1 && B = 0, 1

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("*****Splitting Main into A = 1  and " + time);
								}
								// case A = 1 and B = 0
								update(time, aCharge, bCharge, D, D_Skip, 1, residualChargeA_None, residualChargeB_Wall,
										track_skip, 0, 1, "main");
								// case A = 1; B =1
								update(time, aCharge, bCharge, D, D_Skip, 0, residualChargeA_None, residualChargeB_Skip,
										track_skip, 2, 1, "main");
								// Energy Exchanges
								// Case a = 1, b = 0
								updateEnergySharable(time, aCharge, bCharge, D, D_Skip, 1, track_skip, 0, 1, "main",
										energySharable_2, 0, wallCharge_B, discharge_A, discharge_B);
								// case a = 1, b = 1
								updateEnergySharable(time, aCharge, bCharge, D, D_Skip, 0, track_skip, 2, 1, "main",
										energySharable_3, 0, 0, discharge_A, discharge_B);

							}

							// }

							// source = 0;

							merged = false;

						} else {

							if (aHasFirstHalf && !preA) {

								// Split B = 1 into A = 0 , and A = 1; // B =
								// 0,1 and A = 1 , B = 0 ,1

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting B = 1 into A = 0  and " + time);
								}

								// int currentValue =
								// D_Skip[time][aCharge][bCharge];

								// Case a =0, b = 1 (skip -> charge)
								update(time, aCharge, bCharge, D_Skip, D_Charge, 1, residualChargeA_Wall,
										residualChargeB_Skip, track_charge, 1, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D_Charge, 1, track_charge, 1, 1,
										"skip", energySharable_1, wallCharge_A, 0, discharge_A, discharge_B);

								// case A = 1 and B = 1

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting B = 1 into A = 1  and " + time);
								}

								// case a = 1 , b = 1 (skip -> skip)
								update(time, aCharge, bCharge, D_Skip, D_Skip, 0, residualChargeA_None,
										residualChargeB_Skip, track_skip, 2, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D_Skip, 0, track_skip, 2, 1,
										"skip", energySharable_3, 0, 0, discharge_A, discharge_B);

								// Split B = 0 into A = 0 and A = 1

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting B = 0 into A = 0 and " + time);
								}

								// case a =0, b = 0
								update(time, aCharge, bCharge, D_Charge, D_Charge, 2, residualChargeA_Wall,
										residualChargeB_Wall, track_charge, 2, 0, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D_Charge, 2, track_charge, 2, 0,
										"charge", energySharable_0, wallCharge_A, wallCharge_B, discharge_A,
										discharge_B);

								// case a = 1 , b = 0

								update(time, aCharge, bCharge, D_Charge, D_Skip, 1, residualChargeA_None,
										residualChargeB_Wall, track_skip, 0, 1, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D_Skip, 1, track_skip, 0, 1,
										"charge", energySharable_2, 0, wallCharge_B, discharge_A,
										discharge_B);

								merged = false;

							} else if (aHasFirstHalf && preA) {

								/**
								 * Added case. When the case is of second split.
								 * Only update the current matrix
								 */

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode) {
										System.out.println("****Case for A ***");
										System.out.println(
												"updating A = 1 ,  B = 0 and 1 for second half from A = 1 at time "
														+ time);
									}
								}
								// Case A = 1; B = 0 and 1

								// For skip :
								// Case A = 1, B = 0;

								// case a = 1, b = 0;
								update(time, aCharge, bCharge, D_Skip, D_Skip, 1, residualChargeA_None,
										residualChargeB_Wall, track_skip, 0, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D_Skip, 1, track_skip, 0, 1,
										"skip", energySharable_2, 0, wallCharge_B, discharge_A, discharge_B);

								// case a = 1, b = 1
								update(time, aCharge, bCharge, D_Skip, D_Skip, 0, residualChargeA_None,
										residualChargeB_Skip, track_skip, 2, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D_Skip, 0, track_skip, 2, 1,
										"skip", energySharable_3, 0, 0, discharge_A, discharge_B);
										// Case A = 1 and B = 1

								// Update A = 0;
								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										// System.out.println("****Case for A
										// ***");
										System.out.println(
												"updating A = 0 ,  B = 0 and 1 for second half from A = 0 at time "
														+ time);
								}

								// case a = 0, b = 1
								update(time, aCharge, bCharge, D_Charge, D_Charge, 1, residualChargeA_Wall,
										residualChargeB_Skip, track_charge, 1, 1, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D_Charge, 1, track_charge, 1, 1,
										"charge", energySharable_1, wallCharge_A, 0, discharge_A,
										discharge_B);

								// Case A = 0 , B = 0

								update(time, aCharge, bCharge, D_Charge, D_Charge, 2, residualChargeA_Wall,
										residualChargeB_Wall, track_charge, 2, 0, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D_Charge, 2, track_charge, 2, 0,
										"charge", energySharable_0, wallCharge_A, wallCharge_B, discharge_A,
										discharge_B);

								merged = false;

							}

							if (bHasFirstHalf && !preB) {

								// Split A = 1 into B = 0 and B = 1

								// System.out.println("Splitting into B= 0 and
								// 1");

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting A = 1 into B = 0  and " + time);
								}

								// case a = 1 , b = 0
								update(time, aCharge, bCharge, D_Skip, D_Charge, 1, residualChargeA_None,
										residualChargeB_Wall, track_charge, 0, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D_Charge, 1, track_charge, 0, 1,
										"skip", energySharable_2, 0, wallCharge_B, discharge_A, discharge_B);

								// Case B = 1 and A = 1

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting A = 1 into B = 1 and " + time);
								}

								// case a =1, b =1
								update(time, aCharge, bCharge, D_Skip, D_Skip, 0, residualChargeA_None,
										residualChargeB_Skip, track_skip, 2, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D_Skip, 0, track_skip, 2, 1,
										"skip", energySharable_3, 0, 0, discharge_A, discharge_B);

								// Split A = 0 into B = 0 and B = 1;

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting A = 0 into B = 0  and " + time);
								}

								// case a = 0 , b = 0
								update(time, aCharge, bCharge, D_Charge, D_Charge, 2, residualChargeA_Wall,
										residualChargeB_Wall, track_charge, 2, 0, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D_Charge, 2, track_charge, 2, 0,
										"charge", energySharable_0, wallCharge_A, wallCharge_B, discharge_A,
										discharge_B);

								// Case B = 1 and A = 0
								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Splitting A = 0 into B = 1  and " + time);
								}

								// case a = 0 , b = 1

								update(time, aCharge, bCharge, D_Charge, D_Skip, 1, residualChargeA_Wall,
										residualChargeB_Skip, track_skip, 1, 1, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D_Skip, 1, track_skip, 1, 1,
										"charge", energySharable_1, wallCharge_A, 0, discharge_A,
										discharge_B);

								merged = false;

							} else if (bHasFirstHalf && preB) {

								/**
								 * Added case. When the case is of second split.
								 * No further splitting , only copy the decision
								 * from current skip and current wall charge
								 * matrix
								 */

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("****Case for B ***");
								}

								// For skip :
								// Case a = 1, b = 0

								update(time, aCharge, bCharge, D_Charge, D_Charge, 1, residualChargeA_None,
										residualChargeB_Wall, track_charge, 0, 1, "charge");
								/*updateEnergySharable(time, aCharge, bCharge, D_Charge, D_Charge, 1, track_charge, 0, 1,
										"charge", energySharable_2, 0, wallCharge_B, discharge_A,
										discharge_B);*/

								// case a = 0, b = 0

								update(time, aCharge, bCharge, D_Charge, D_Charge, 2, residualChargeA_Wall,
										residualChargeB_Wall, track_charge, 2, 0, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D_Charge, 2, track_charge, 2, 0,
										"charge", energySharable_0, wallCharge_A, wallCharge_B, discharge_A,
										discharge_B);
								// case a = 0 , b = 1

								update(time, aCharge, bCharge, D_Skip, D_Skip, 1, residualChargeA_Wall,
										residualChargeB_Skip, track_skip, 1, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D_Skip, 1, track_skip, 1, 1,
										"skip", energySharable_1, wallCharge_A, 0, discharge_A, discharge_B);

								// case a = 1, b = 1

								update(time, aCharge, bCharge, D_Skip, D_Skip, 0, residualChargeA_None,
										residualChargeB_Skip, track_skip, 2, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D_Skip, 0, track_skip, 2, 1,
										"skip", energySharable_3, 0, 0, discharge_A, discharge_B);

								merged = false;
							}

						}

					}

					// -----------------------------------------------------MERGING
					// PART
					// -------------------------------------------------------------//

					else if ((!isFirstHalf && isSecondHalf) || (!isFirstHalf && !isSecondHalf)) {

						if (aCharge == 0 && bCharge == 0) {
							
						}

						if (source == 0) {

							// Merge
							if (bHasSecondHalf) {
								// Merge B = 1 and A = 0 ,1 into Main

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Merging B = 1 and B = 0 into Main and " + time);
								}

								// case a = 0 , b = 1

								update(time, aCharge, bCharge, D_Skip, D, 1, residualChargeA_Wall, residualChargeB_Skip,
										track_main, 1, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D, 1, track_main, 1, 1, "skip",
										energySharable_1, wallCharge_A, 0, discharge_A, discharge_B);

								// case a = 1, b = 1
								update(time, aCharge, bCharge, D_Skip, D, 0, residualChargeA_None, residualChargeB_Skip,
										track_main, 2, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D, 0, track_main, 2, 1, "skip",
										energySharable_3, 0, 0, discharge_A, discharge_B);

								// Merge B=0 and A = 0, 1 into Main

								// case B = 0 and A = 0

								update(time, aCharge, bCharge, D_Charge, D, 2, residualChargeA_Wall,
										residualChargeB_Wall, track_main, 2, 0, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D, 2, track_main, 2, 0, "charge",
										energySharable_0, wallCharge_A, wallCharge_B, discharge_A, discharge_B);

								// case a= 1, b = 0
								update(time, aCharge, bCharge, D_Charge, D, 1, residualChargeA_None,
										residualChargeB_Wall, track_main, 0, 1, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D, 1, track_main, 0, 1, "charge",
										energySharable_2, 0, wallCharge_B, discharge_A, discharge_B);

								// source = -1;

								merged = true;

							}

							if (aHasSecondHalf) {
								// Merge A = 0 and B = 0 , 1 into Main

								if (aCharge == 0 && bCharge == 0) {
									if (debugMode)
										System.out.println("Merging A into Main " + time);
								}

								// Case A = 0 and B = 0
								update(time, aCharge, bCharge, D_Charge, D, 2, residualChargeA_Wall,
										residualChargeB_Wall, track_main, 2, 0, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D, 2, track_main, 2, 0, "charge",
										energySharable_0, wallCharge_A, wallCharge_B, discharge_A, discharge_B);

								// Case A = 0 and B = 1

								update(time, aCharge, bCharge, D_Charge, D, 1, residualChargeA_Wall,
										residualChargeB_Skip, track_main, 1, 1, "charge");
								updateEnergySharable(time, aCharge, bCharge, D_Charge, D, 1, track_main, 1, 1, "charge",
										energySharable_1, wallCharge_A, 0, discharge_A, discharge_B);

								// Merge A = 1 and B = 0, 1 into Main

								// case a = 1, b = 0
								update(time, aCharge, bCharge, D_Skip, D, 1, residualChargeA_None, residualChargeB_Wall,
										track_main, 0, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D, 1, track_main, 0, 1, "skip",
										energySharable_2, 0, wallCharge_B, discharge_A, discharge_B);

								// case a = 1, b = 1
								update(time, aCharge, bCharge, D_Skip, D, 0, residualChargeA_None, residualChargeB_Skip,
										track_main, 2, 1, "skip");
								updateEnergySharable(time, aCharge, bCharge, D_Skip, D, 0, track_main, 2, 1, "skip",
										energySharable_3, 0, 0, discharge_A, discharge_B);

								merged = true;
							}

						} else {

							if (aCharge == 0 && bCharge == 0) {
								if (debugMode)

									System.out.println("Moving from main to main " + time);
							}
							// Move to main again from main

							// Case A = 1; B = 0

							// case a = 1 , b = 0
							update(time, aCharge, bCharge, D, D, 1, residualChargeA_None, residualChargeB_Wall,
									track_main, 0, 1, "main");
							
							updateEnergySharable(time, aCharge, bCharge, D, D, 1, track_main, 0, 1, "main",
									energySharable_2, 0, wallCharge_B, discharge_A, discharge_B);

							// case a = 1, b = 1
							update(time, aCharge, bCharge, D, D, 0, residualChargeA_None, residualChargeB_Skip,
									track_main, 2, 1, "main");
							updateEnergySharable(time, aCharge, bCharge, D, D, 0, track_main, 2, 1, "main",
									energySharable_3, 0, 0, discharge_A, discharge_B);

							// case a = 0 , b = 1
							update(time, aCharge, bCharge, D, D, 1, residualChargeA_Wall, residualChargeB_Skip,
									track_main, 1, 1, "main");
							/*if(residualChargeA_Wall >=0 && residualChargeB_Skip >=0)
							{
							System.out.println("Updated to " + time + " " + D[time][residualChargeA_Wall][residualChargeB_Skip]);
							//System.exit(0);
							}*/
							updateEnergySharable(time, aCharge, bCharge, D, D, 1, track_main, 1, 1, "main",
									energySharable_1, wallCharge_A, 0, discharge_A, discharge_B);

							// case a = 0 , b = 0;

							update(time, aCharge, bCharge, D, D, 2, residualChargeA_Wall, residualChargeB_Wall,
									track_main, 2, 0, "main");
							updateEnergySharable(time, aCharge, bCharge, D, D, 2, track_main, 2, 0, "main",
									energySharable_0, wallCharge_A, wallCharge_B, discharge_A, discharge_B);

							
							merged = true;
						}
					}

				}

			}

		}

	}

	public void trackSkip_Updated(int[][][] D, int[][][] D_Skip, int[][][] D_Charge, String[][][] t_charge,
			String[][][] t_skip, String[][][] t_main, double tE) throws IOException {

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("Tracking verification.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		writer.write(
				"Step, Current Value, Source Matrix, User A Charge, User B Charge, User A skip, User B Skip, Energy Received A, Energy Received B , Decision Block type");
		writer.newLine();
		/*
		 * try {
		 */
		int[] skipA = new int[n];
		int[] skipB = new int[n];

		int[] energyReceived_A = new int[n];
		int[] energyReceived_B = new int[n];
		int D_current[][][] = new int[n][101][101];
		String t_current[][][] = new String[n][101][101];
		String t_prev[][][] = new String[n][101][101];
		String contentA = "";
		String contentB = "";
		
		int value = 500;
		int indexA = -1;
		int indexB = -1;
		for (int a = 0; a <= c; a++) {
			for (int d = 0; d <= c; d++) {

				if (D[n - 1][a][d] < value) {
					value = D[n - 1][a][d];
					indexB = d;
					indexA = a;

				}

			}
		}

		
		String content = "Current Level Index A : " + indexA + " , Current Index B : " + indexB + "With Value " + value
				+ "";
		// writer.write(String.valueOf(n) + ", " + String.valueOf(value) + ", "
		// + "Main, " + );

		if (debugMode)
			System.out.println("Value : " + value + "," + indexA + "," + indexB);

		int currentEnergyLevelA = indexA;
		int currentEnergyLevelB = indexB;

		// System.out.println("Initial Charge : " + initialCharge_0 + ", " +
		// initialCharge_1);

		skipA[n - 1] = 0;
		skipB[n - 1] = 0;
		energyReceived_A[n - 1] = 0;
		energyReceived_B[n - 1] = 0;
		content = content + "\n" + " Energy Received A " + energyReceived_A[n - 1] + " ,  Energy Received B "
				+ energyReceived_B[n - 1];
		String inA = t_main[n - 1][indexA][indexB].split("-")[0];

		// System.out.println("Last n is " + n + "cpA " +
		// this.decisionPointsChargesA.size());
		String inB = t_main[n - 1][indexA][indexB].split("-")[1];
		String matrix = t_main[n - 1][indexA][indexB].split("-")[5];

		/*
		 * writer.write(String.valueOf(n) + ", " + String.valueOf(value) + ", "
		 * + matrix + ", " + String.valueOf(currentEnergyLevelA) + "," +
		 * String.valueOf(currentEnergyLevelB) + "," + String.valueOf(skipA[n])
		 * + "," + String.valueOf(skipB[n]) + "," +
		 * String.valueOf(energyReceived_A[n]) + "," +
		 * String.valueOf(energyReceived_B[n]) + ",Dummy"); writer.newLine();
		 */
		/*
		 * System.out.println("**************Debugging ***************");
		 * System.out.println("Current Value " + value + " source matrix : " +
		 * matrix + "source charging Level A : " + inA +
		 * "source charging Level B : " + inB);
		 */
		if (matrix.compareTo("skip") == 0) {
			D_current = D_Skip;
			t_current = t_skip;
		} else if (matrix.compareTo("charge") == 0) {
			D_current = D_Charge;
			t_current = t_charge;
		} else {
			D_current = D;
			t_current = t_main;
		}

		indexA = Integer.parseInt(inA);
		indexB = Integer.parseInt(inB);
		contentA = "," + indexA;
		contentB = " , " + indexB;
		t_prev = t_main;

		for (int i = n - 2; i >= 0; i--) {

			String energyReceived = t_prev[i + 1][currentEnergyLevelA][currentEnergyLevelB].split("-")[2];
			String currentUser = t_prev[i + 1][currentEnergyLevelA][currentEnergyLevelB].split("-")[3];
			String skipped = t_prev[i + 1][currentEnergyLevelA][currentEnergyLevelB].split("-")[4];
			int curr = Integer.parseInt(currentUser);
			int skip = Integer.parseInt(skipped);

			if (energyReceived.contains("_")) {
				String eReceived = energyReceived.split("_")[0];
				String eReceivedFrom = energyReceived.split("_")[1];

				int eReceivedFrom_i = Integer.parseInt(eReceivedFrom);
				if (eReceivedFrom_i == 0) {
					energyReceived_B[i] = Integer.parseInt(eReceived);
					energyReceived_A[i] = 0;
				} else if (eReceivedFrom_i == 1) {
					energyReceived_A[i] = Integer.parseInt(eReceived);
					energyReceived_B[i] = 0;
				}
			} else {
				energyReceived_A[i] = 0;
				energyReceived_B[i] = 0;
			}

			if (value == D_current[i][indexA][indexB]) {
				if (curr == 2 && skip == 1) {
					skipB[i] = 1;
					skipA[i] = 1;
				} else {
					if (debugMode) {
						System.out.println("Error " + curr + "&& " + skip);
						System.exit(1);
						break;
					}

				}

			} else if (value - D_current[i][indexA][indexB] == 1) {

				if ((curr == 0 && skip == 1)) {
					skipA[i] = 1;
					skipB[i] = 0;
				} else if ((curr == 1 && skip == 1)) {
					skipA[i] = 0;
					skipB[i] = 1;
				} else {
					if (debugMode) {
						System.out.println("ERROR HERE TOO");
						System.exit(1);
						break;
					}
				}

			}

			else if (value - D_current[i][indexA][indexB] == 2) {

				if (curr == 2 && skip == 0) {
					skipA[i] = 0;
					skipB[i] = 0;
				} else {
					if (debugMode) {
						System.out.println("Error Here too");
						System.exit(1);
						break;
					}
				}
			}

			else {

				System.out.println("value : " + value + "and " + D_current[i][indexA][indexB] + " && Difference : "
						+ (value - D_current[i][indexA][indexB]));

				if (debugMode) {
					System.out.println("Error 123 " + curr + "  matrix : " + matrix + " time " + i
							+ D_Skip[i][indexA][indexB] + ", " + indexA + " , " + indexB);
					System.exit(1);
					// break;
				}
				// System.exit(1);
			}

			value = D_current[i][indexA][indexB];
			content = content + "\n" + "Current Level Index A : " + indexA + " , Current Index B : " + indexB
					+ "With Value " + value + "";
			content = content + "\n" + " Energy Received A " + energyReceived_A[i] + " ,  Energy Received B "
					+ energyReceived_B[i];

			contentA = "," + indexA + contentA;
			contentB = " , " + indexB + contentB;
			if (i != 0) {

				String inA_1 = t_current[i][indexA][indexB].split("-")[0];
				String inB_1 = t_current[i][indexA][indexB].split("-")[1];
				matrix = t_current[i][indexA][indexB].split("-")[5];

				if (matrix.compareTo("skip") == 0) {
					D_current = D_Skip;
					t_prev = t_current;
					t_current = t_skip;
				} else if (matrix.compareTo("charge") == 0) {
					D_current = D_Charge;
					t_prev = t_current;
					t_current = t_charge;
				} else {
					if (matrix.compareTo("main") != 0) {
						System.out.println("************We have a error here*********" + matrix);
						System.exit(1);
					}
					D_current = D;
					t_prev = t_current;
					t_current = t_main;
				}

				currentEnergyLevelA = indexA;
				currentEnergyLevelB = indexB;
				indexA = Integer.parseInt(inA_1);
				indexB = Integer.parseInt(inB_1);

				writer.write(String.valueOf(i) + ", " + String.valueOf(value) + ", " + matrix + ", "
						+ String.valueOf(currentEnergyLevelA) + "," + String.valueOf(currentEnergyLevelB) + ","
						+ String.valueOf(skipA[i]) + "," + String.valueOf(skipB[i]) + ","
						+ String.valueOf(energyReceived_A[i]) + "," + String.valueOf(energyReceived_B[i]));
				writer.newLine();

			}

		}

		System.out.println(contentA + "\n"  + contentB); 
		// printMatrix();

		Result resA = new Result(n);
		resA.sourceNode = nodeA;
		/*
		 * if(nodeA==null) { System.out.println(" ???????? "); System.exit(0); }
		 */
		resA.energySequence_A = energyReceived_A;
		resA.skipSequence_A = skipA;
		resA.originalSkips_Source = getOriginalSkips(this.decisionPointsChargesA, skipA);

		resA.originalEnergySource = getOriginalEnergy(this.decisionPointsChargesA, energyReceived_A, tE);
		resA.dpSource = this.decisionPointsChargesA;
		resA.sourceNode.decisionBlocks = decisionPointsChargesA;
		resA.destNode = nodeB;
		resA.meet = this.meet;
		//resA.setValues();

	
		Result resB = new Result(n);
		resB.sourceNode = nodeB;

		resB.energySequence_A = energyReceived_B;
		resB.skipSequence_A = skipB;
		resB.originalSkips_Source = getOriginalSkips(this.decisionPointsChargesB, skipB);
		resB.originalEnergySource = getOriginalEnergy(this.decisionPointsChargesB, energyReceived_B, tE);
		resB.sourceNode.decisionBlocks = decisionPointsChargesB;
		resB.dpSource = this.decisionPointsChargesB;
		resB.destNode = nodeA;
		resB.meet = meet;
		//resB.setValues();
		
		resA.originalSkips_Dest = resB.originalSkips_Source;
		resB.originalSkips_Dest = resA.originalSkips_Source;
		resA.energySequence_B = resB.energySequence_A;
		resB.energySequence_B = resA.energySequence_A;
		resA.setValues();
		resB.setValues();

	
		
		if (resA.originalSkips_Source.size() != resA.sourceNode.chargingPatterns.size()) {
			System.out.println("ERROR ????? " + resA.originalSkips_Source.size() + ", " + resA.sourceNode.chargingPatterns.size());
			for(int i=0;i<resA.sourceNode.chargingPatterns.size();i++)
			{
				System.out.println(resA.sourceNode.chargingPatterns.get(i).cls + "\n "+ resA.sourceNode.chargingPatterns.get(i).cle +  ", \n" + resA.sourceNode.chargingPatterns.get(i).dle  );
			}
			
			System.out.println("Time");
			for(int i=0;i<resA.sourceNode.chargingPatterns.size();i++)
			{
				System.out.println(resA.sourceNode.chargingPatterns.get(i).cts + "\n "+ resA.sourceNode.chargingPatterns.get(i).cte  +  ", \n" + resA.sourceNode.chargingPatterns.get(i).dte  );
			}
			
			System.out.println("For Decision Points");
			for(int i=0;i<resA.sourceNode.decisionBlocks.size();i++)
			{
				System.out.println(resA.sourceNode.decisionBlocks.get(i).cls + ", " + resA.sourceNode.decisionBlocks.get(i).cle + " , " + resA.sourceNode.decisionBlocks.get(i).dls + "," + resA.sourceNode.decisionBlocks.get(i).dle);
			}
			
			
			System.out.println("FOR B");
			for(int i=0;i<resB.sourceNode.chargingPatterns.size();i++)
			{
				System.out.println(resB.sourceNode.chargingPatterns.get(i).cls + "\n "+ resB.sourceNode.chargingPatterns.get(i).cle +  ", \n" + resB.sourceNode.chargingPatterns.get(i).dle  );
			}
			
			System.out.println("Time" + resB.sourceNode.nodeId);
			for(int i=0;i<resB.sourceNode.chargingPatterns.size();i++)
			{
				System.out.println(resB.sourceNode.chargingPatterns.get(i).cts + "\n "+ resB.sourceNode.chargingPatterns.get(i).cte  +  ", \n" + resB.sourceNode.chargingPatterns.get(i).dte  );
			}
			
			System.out.println("For Decision Points");
			for(int i=0;i<resB.sourceNode.decisionBlocks.size();i++)
			{
				System.out.println(resB.sourceNode.decisionBlocks.get(i).cls + ", " + resB.sourceNode.decisionBlocks.get(i).cle + " , " + resB.sourceNode.decisionBlocks.get(i).dls + "," + resB.sourceNode.decisionBlocks.get(i).dle);
			}
			
			System.exit(0);
		}
		
		if (resB.originalSkips_Source.size() != resB.sourceNode.chargingPatterns.size()) {
			System.out.println(resB.sourceNode.index);
			System.out.println("ERROR B????? " + resB.sourceNode.chargingPatterns.size() + " , "
					+ resB.originalSkips_Source.size() + " , " + this.decisionPointsChargesB.size());
			Utilities.printCycles(resB.sourceNode.chargingPatterns, resB.sourceNode.nodeId);
			System.out.println("Energy size " + resB.originalEnergySource.size());
			System.exit(0);
		}

		/*
		 * if(resA == null || resB==null) { System.out.println("Found + " +
		 * this.nodeA.index + " , " + this.nodeB.index); System.exit(0); }
		 */
		this.result[this.nodeA.index][this.nodeB.index] = resA;
		this.result[this.nodeB.index][this.nodeA.index] = resB;

		
		writer.close();
		
	}

	public ArrayList<Integer> getOriginalSkips(ArrayList<ChargingPattern> decisionPoints, int[] skipSequence) {
		// 0 : Full charging block
		// 1: Full charging with half discharging
		// 2: Partial Charging (First Half)
		// 3 : Partial Charging (Second Half)
		// 4: Full discharging
		// 5:half discharging (1st part)
		// 6: half discharging (2nd part)
		// 7: Middle Discharging

		int temp = -1;

		ArrayList<Integer> originalSkips = new ArrayList<Integer>();

		for (int i = 0; i < decisionPoints.size() - 1; i++) {
			ChargingPattern c = decisionPoints.get(i);

			 System.out.println("Type " + c.patternType);
			int currentSkip = skipSequence[i];

			if (c.patternType == 0) {
				originalSkips.add(currentSkip);

			} else if (c.patternType == 1) {
				originalSkips.add(currentSkip);

			} else if (c.patternType == 2) {

				if (i == decisionPoints.size() - 2) {
					// System.out.println("Adding ");
					originalSkips.add(currentSkip);

				} else {
					temp = currentSkip;

				}
			} else if (c.patternType == 3) {
				if (i != 0 && currentSkip != temp) {
					System.out.println("Error in Skip sequence " + temp + " , " + currentSkip);
				} else {
					originalSkips.add(currentSkip);

					temp = -1;
				}
			} else if (c.patternType == 4) {
				if (i == 0) {
					originalSkips.add(0);

				}

			} else if (c.patternType == 5) {
				if (i == 0) {
					originalSkips.add(0);

				}

			} else if (c.patternType == 6) {
				if (i == 0) {
					originalSkips.add(0);

				}

			} else if (c.patternType == 7) {
				if (i == 0) {
					originalSkips.add(0);

				}

			}
		}

		// if(originalSkips.size()!=)
		if (debugMode) {
			System.out.println("Size is " + nodeA.totalCycles);
			System.out.println("Size is " + nodeB.totalCycles);
			System.out.println("Original Skips is : ");
			for (int i = 0; i < originalSkips.size(); i++) {
				System.out.println(originalSkips.get(i) + " , ");
			}
		}
		return originalSkips;
	}

	public ArrayList<Integer> getOriginalEnergy(ArrayList<ChargingPattern> decisionPoints, int[] energySequence,
			double tE) {
		// 0 : Full charging block
		// 1: Full charging with half discharging
		// 2: Partial Charging (First Half)
		// 3 : Partial Charging (Second Half)
		// 4: Full discharging
		// 5:half discharging (1st part)
		// 6: half discharging (2nd part)
		// 7: Middle Discharging

		int tempEnergy = 0;

		ArrayList<Integer> originalEnergy = new ArrayList<>();
		for (int i = 0; i < decisionPoints.size() - 1; i++) {
			ChargingPattern c = decisionPoints.get(i);

			int currentEnergy = energySequence[i];

			if (c.patternType == 0) {

				originalEnergy.add((int) (currentEnergy * tE));
				tempEnergy = 0;
			} else if (c.patternType == 1) {

				originalEnergy.add((int) (currentEnergy * tE));
				tempEnergy = 0;
			} else if (c.patternType == 2) {

				if (i == decisionPoints.size() - 2) {
					// System.out.println("Adding ");

					originalEnergy.add((int) (currentEnergy * tE));
					tempEnergy = 0;
				} else {

					tempEnergy = tempEnergy + energySequence[i];
				}
			} else if (c.patternType == 3) {

				originalEnergy.add((int) (currentEnergy * tE));
				tempEnergy = 0;

			} else if (c.patternType == 4) {
				if (i == 0) {

					originalEnergy.add((int) (currentEnergy * tE));
					tempEnergy = 0;
				} else {
					tempEnergy = tempEnergy + energySequence[i];
				}
			} else if (c.patternType == 5) {
				if (i == 0) {

					originalEnergy.add((int) (currentEnergy * tE));
					tempEnergy = 0;

				} else {
					tempEnergy = tempEnergy + energySequence[i];
				}
			} else if (c.patternType == 6) {
				if (i == 0) {

					originalEnergy.add((int) (currentEnergy * tE));
					tempEnergy = 0;

				} else {
					tempEnergy = tempEnergy + energySequence[i];
				}
			} else if (c.patternType == 7) {
				if (i == 0) {

					originalEnergy.add((int) (currentEnergy * tE));
					tempEnergy = 0;

				} else {
					tempEnergy = tempEnergy + energySequence[i];
				}
			}
		}

		return originalEnergy;
	}

	
	public int getOverlappingTimes(long meetingStart, long meetingEnd, long userAStart, long userAEnd, long userBStart,
			long userBEnd) {

		Interval i1 = new Interval(meetingStart, meetingEnd);
		Interval i2 = new Interval(userAStart, userAEnd);
		Interval i3 = new Interval(userBStart, userBEnd);

		Interval lowestInterval = lowestInterval(i1, i2);

		if (lowestInterval != null) {
			
			lowestInterval = lowestInterval(lowestInterval, i3);

			if (lowestInterval != null) {
				long diff = lowestInterval.getDifference();
				System.out.println("Lowest Interval start " + lowestInterval.start + " , " + lowestInterval.end);
				if (diff == 0)
					return 0;
				else {
					int totalTimeDiff = (int) Math.abs(diff);
					

					return totalTimeDiff;
				}
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public Interval lowestInterval(Interval i1, Interval i2) {

		if (i2.start == i1.start && i2.end == i1.end) {
			return i2;
		} else if (i2.start <= i1.start && i2.end <= i1.end && i2.end >= i1.start) {
			return new Interval(i1.start, i2.end);
		} else if (i2.start >= i1.start && i2.start <= i1.end && i2.end >= i1.end) {
			return new Interval(i2.start, i1.end);
		} else if (i2.start >= i1.start && i2.start < i1.end && i2.end >= i1.start && i2.end <= i1.end) {
			return i2;
		} else if (i2.start <= i1.start && i2.end >= i1.end) {
			return i1;
		}

		else
			return null;

	}

	class Interval {
		long start = 0;
		long end = 0;

		public Interval() {

		}

		public Interval(long start, long end) {
			this.start = start;
			this.end = end;

		}

		public long getDifference() {
			return (this.end - this.start);
		}
	}
	
	public void writeTimeBasedCharge(Nodes nodeA, Nodes nodeB, int[] skipsA, int[] skipsB,int[] energyA, int[] energyB,ArrayList<ChargingPattern> dpA, ArrayList<ChargingPattern> dpB) throws IOException
	{
		
		 BufferedWriter writer = new BufferedWriter(new FileWriter("chargingInfo.csv"));
		 String content = "";
		 String content1  = "";
		 String content2 = "";
		 String content3 = "";
	
		 int initialCharge = nodeA.decisionBlocks.get(0).cls;
		// content1 = content + initialCharge + ",";
		// content = content + nodeA.decisionBlocks.get(0).cts + ",";
		 
		for(int i=0;i<nodeA.decisionBlocks.size()-1;i++)
		{
			ArrayList<ChargingPattern> cp = nodeA.decisionBlocks;
			 content = content + String.valueOf(cp.get(i).cts) + " , " +  String.valueOf(cp.get(i).cte)+","+String.valueOf(cp.get(i).dte) + ","; 
			 content1 = content1 + String.valueOf(cp.get(i).cls) + " , " + String.valueOf(cp.get(i).cle) + "," + String.valueOf(cp.get(i).dle) + ",";
			 int cle = Math.min(100, initialCharge + (cp.get(i).totalCharge)*(1-skipsA[i]) + energyA[i]-energyB[i]);//getEnergy(cp.get(i).cts, cp.get(i).dte, energyA, dpA) - getEnergy(cp.get(i).cts, cp.get(i).dte, energyB, dpB);
			 int dle = cle - (cp.get(i).totalDischarge);
			 
			 content2 = content2 + initialCharge + "," + String.valueOf(cle) + "," + String.valueOf(dle) + ",";
			 content3 = content3 + energyA[i] + "," ;
			 
			 initialCharge = dle;
			 
			 
			 
		}
		writer.write(content);
		writer.newLine();
		writer.write(content1);
		writer.newLine();
		writer.write(content2);
		writer.newLine();
		writer.write(content3);
		writer.newLine();
		
		initialCharge = nodeB.decisionBlocks.get(0).cls;
		
		content = "";
		content1 = "";
		content2 = "";
		
		content3 = "";
		// content1 = content + initialCharge + ",";
		// content = content + nodeB.chargingPatterns.get(0).cts + ",";
		 
		
		for(int i=0;i<nodeB.decisionBlocks.size()-1;i++)
		{
			ArrayList<ChargingPattern> cp = nodeB.decisionBlocks;
			 content = content +  String.valueOf(cp.get(i).cts) + " , " + String.valueOf(cp.get(i).cte)+","+String.valueOf(cp.get(i).dte) + ","; 
			 content1 = content1 +   String.valueOf(cp.get(i).cls) + " , " + String.valueOf(cp.get(i).cle) + "," + String.valueOf(cp.get(i).dle) + ",";
			 int cle = Math.min(100, initialCharge + (cp.get(i).totalCharge) * (1-skipsB[i]) + energyB[i]-energyA[i]);
			 int dte = cle - (cp.get(i).totalDischarge);
			 
			 content2 = content2 + initialCharge + "," + String.valueOf(cle) + "," + String.valueOf(dte) + ",";
			 content3 = content3 + energyB[i] + ",";
			 initialCharge = dte;
			 
			 
			 
		}
		writer.newLine();
		writer.newLine();
		writer.write(content);
		writer.newLine();
		writer.write(content1);
		writer.newLine();
		writer.write(content2);
		writer.newLine();
		writer.write(content3);
		
		writer.close();
		System.exit(0);
		
	}
	
	
	public int getEnergy(long cts, long dte, int[] energyB, ArrayList<ChargingPattern> dpB)
	{
		//cp.get(i).cts, cp.get(i).dte, energyB, dpB
		int energyReceived = 0;
		for(int i=0;i<dpB.size()-1;i++)
		{
			if(dpB.get(i).cts >= cts && dpB.get(i).dte <=dte)
			{
				energyReceived = energyReceived + energyB[i];
			}
		}
		
		return energyReceived;
	}
	

}
