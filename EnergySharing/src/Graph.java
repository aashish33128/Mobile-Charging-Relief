/**
 * @author Aashish Dhungana
 * 
 * Gets different required results for plotting the graphs in the paper
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Graph {

	// Time gap in x-Axis.
	int timeGap = 2;
	long startTime;
	long endTime;
	ArrayList<MatchedPair> matchedPairs = new ArrayList<>();
	List<Nodes> allNodes = new ArrayList<>();
	Result[][] result = null;

	public Graph(long startTime, long endTime, ArrayList<MatchedPair> matchedPairs, List<Nodes> allNodes,
			Result[][] res) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.matchedPairs = matchedPairs;
		
		this.allNodes = allNodes;
		this.result = res;
		// checkIfNull(this.result);

	}

	public void checkIfNull(Result[][] res) {
		for (int i = 0; i < allNodes.size(); i++) {
			for (int j = 0; j < allNodes.size() && j != i; j++) {
				if (res[i][j] == null) {
					System.out.println("Null here ???? " + i + " , " + j);
					System.exit(0);
				}
			}
		}
	}

	public ArrayList<Double> getxAxis() {
		return Utilities.getTimeAxis(startTime, endTime);
	}

	public ArrayList<Double> getxAxisPerDay(int numDays) {
		ArrayList<Double> xAxis = new ArrayList<>();
		for (int i = 1; i <= numDays; i++) {
			xAxis.add(Double.valueOf((i * 24 * 60) + startTime));
		}
		
		return xAxis;
	}
	

	public int getNumberOfChargingCycles(ArrayList<ChargingPattern> cp, double endTime) {
		int totalChargingCycles = 0;
		for (int i = 0; i < cp.size(); i++) {
			if (cp.get(i).dte <= endTime) {
				if (cp.get(i).totalCharge > 0)
					totalChargingCycles = totalChargingCycles + 1;
			} else if (cp.get(i).dte >= endTime) {
				break;
			}
		}

		System.out.println("Total charging cycles current : " + totalChargingCycles);
		return totalChargingCycles;

	}

	public int getNumberOfSkips(ArrayList<ChargingPattern> cp, double endTime, ArrayList<Integer> skips) {

		int totalSkips = 0;
		for (int i = 0; i < cp.size(); i++) {
			if (cp.get(i).dte <= endTime) {
				if (skips != null) {

					// System.out.println("SIZE " + cp.size() + " , " +
					// skips.size());
					if (skips.get(i) == 1 && cp.get(i).totalCharge > 0)
						totalSkips = totalSkips + 1;

					else if (cp.get(i).dte >= endTime) {
						break;
					}
				}
			}
		}

		return totalSkips;

	}

	public int getNumberOfSkipsPerDay(ArrayList<ChargingPattern> cp, double startTime, double endTime,
			ArrayList<Integer> skips) {

		int totalSkips = 0;
		for (int i = 0; i < cp.size(); i++) {
			if (cp.get(i).cts >= startTime && cp.get(i).dte <= endTime) {
				if (skips != null) {

					// System.out.println("SIZE " + cp.size() + " , " +
					// skips.size());
					if (skips.get(i) == 1 && cp.get(i).totalCharge > 0)
						totalSkips = totalSkips + 1;

					else if (cp.get(i).dte >= endTime) {
						break;
					}
				}
			}
		}

		return totalSkips;

	}

	public int getNumberOfChargingCycles(ArrayList<ChargingPattern> cp, double endTime, ArrayList<Integer> skips) {
		int totalChargingCycles = 0;
		//
		for (int i = 0; i < cp.size(); i++) {
			if (cp.get(i).dte <= endTime) {
				if (skips != null) {

					// System.out.println("SIZE " + cp.size() + " , " +
					// skips.size());
					if (skips.get(i) == 0 && cp.get(i).totalCharge > 0)
						totalChargingCycles = totalChargingCycles + 1;
				} else {
					if (cp.get(i).totalCharge > 0)
						totalChargingCycles = totalChargingCycles + 1;
				}
			} else if (cp.get(i).dte >= endTime) {
				break;
			}
		}
		return totalChargingCycles;

	}

	public int getNumberOfChargingCyclesPerDay(ArrayList<ChargingPattern> cp, double startTime, double endTime,
			ArrayList<Integer> skips) {
		int totalChargingCycles = 0;
		//
		for (int i = 0; i < cp.size(); i++) {
			if (cp.get(i).cts >= startTime && cp.get(i).dte <= endTime) {
				if (skips != null) {

					// System.out.println("SIZE " + cp.size() + " , " +
					// skips.size());
					if (skips.get(i) == 0 && cp.get(i).totalCharge > 0)
						totalChargingCycles = totalChargingCycles + 1;
				} else {
					if (cp.get(i).totalCharge > 0)
						totalChargingCycles = totalChargingCycles + 1;
				}
			} else if (cp.get(i).dte >= endTime) {
				break;
			}
		}
		return totalChargingCycles;

	}

	public double getTotalReceivedEnergy(ArrayList<ChargingPattern> cp, double endTime,
			ArrayList<Integer> originalEnergySource, ArrayList<Integer> originalSkips, String label)

	{
		double totalEnergy = 0;
		if (label.compareTo("current") == 0) {
			for (int i = 0; i < cp.size(); i++) {
				if (cp.get(i).dte <= endTime) {
					totalEnergy = totalEnergy + cp.get(i).totalCharge;
				}

			}
		} else if (label.compareTo("selfish") == 0) {
			for (int i = 0; i < cp.size(); i++) {
				if (cp.get(i).dte <= endTime) {
					if (originalSkips.get(i) == 0) {
						totalEnergy = totalEnergy + cp.get(i).totalCharge;
					}
				}
			}
		} else if (label.compareTo("cooperative") == 0) {
			for (int i = 0; i < cp.size(); i++) {
				if (cp.get(i).dte <= endTime) {

					if (originalSkips.get(i) == 0) {
						totalEnergy = totalEnergy + cp.get(i).totalCharge + originalEnergySource.get(i);
					} else if (originalSkips.get(i) == 1) {
						totalEnergy = totalEnergy + originalEnergySource.get(i);
					}
					// if(skips.get(i) == 0 && cp.get(i).totalCharge >
					// 0)totalChargingCycles = totalChargingCycles + 1;

				} else if (cp.get(i).dte >= endTime) {
					break;
				}
			}
		}

		return totalEnergy;

	}

	public double getTotalExchangedEnergy(ArrayList<ChargingPattern> cp, double endTime,
			ArrayList<Integer> originalEnergySource, ArrayList<Integer> originalSkips, String label)

	{
		double totalEnergy = 0;

		for (int i = 0; i < cp.size(); i++) {
			if (cp.get(i).dte <= endTime) {

				totalEnergy = totalEnergy + originalEnergySource.get(i);
				// if(skips.get(i) == 0 && cp.get(i).totalCharge >
				// 0)totalChargingCycles = totalChargingCycles + 1;

			} else if (cp.get(i).dte >= endTime) {
				break;
			}
		}

		return totalEnergy;

	}

	public ArrayList<Double> getAverageChargingCycles(ArrayList<Double> xAxis, String label) {
		ArrayList<Double> yAxis = new ArrayList<>();

		if (label.compareTo("current") == 0) {
			for (int i = 0; i < xAxis.size(); i++) {
				double sum = 0;
				for (int j = 0; j < this.allNodes.size(); j++) {
					sum = sum + getNumberOfChargingCycles(this.allNodes.get(j).chargingPatterns, xAxis.get(i));
				}

				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));
			}
		} else if (label.compareTo("selfish") == 0) {
			// Make for selfish

			for (int i = 0; i < xAxis.size(); i++) {
				double sum = 0;
				for (int j = 0; j < this.allNodes.size(); j++) {
					sum = sum + getNumberOfChargingCycles(this.allNodes.get(j).chargingPatterns, xAxis.get(i),
							this.allNodes.get(j).selfishRes.originalSkips);
				}
				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));
			}
		} else if (label.compareTo("cooperative") == 0) {

			// Case for cooperative. From stable matching
			for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;

				for (int i = 0; i < matchedPairs.size(); i++) {
					// System.out.println("At i " + i);

					if (matchedPairs.get(i) != null) {

						if (this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB] != null) {
							// System.out.println(" ******** " +
							// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB]);
							Nodes a = this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].sourceNode;
							Nodes b = this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].sourceNode;
							sum = sum
									+ getNumberOfChargingCycles(a.chargingPatterns, xAxis.get(j),
											this.result[matchedPairs.get(i).indexA][matchedPairs
													.get(i).indexB].originalSkips_Source)
									+ getNumberOfChargingCycles(b.chargingPatterns, xAxis.get(j),
											this.result[matchedPairs.get(i).indexB][matchedPairs
													.get(i).indexA].originalSkips_Source);
						}
					} else {
						System.out.println("Null Matched Pairs found");
						System.exit(0);
					}

				}
				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));

			}
		}

		return yAxis;
	}

	public ArrayList<Double> getAverageSavingsInChargingCycles(ArrayList<Double> xAxis, String label) {
		ArrayList<Double> yAxis = new ArrayList<>();

		if (label.compareTo("selfish") == 0) {
			// Make for selfish

			for (int i = 0; i < xAxis.size(); i++) {
				double sum = 0;
				for (int j = 0; j < this.allNodes.size(); j++) {
					int num = getNumberOfChargingCycles(this.allNodes.get(j).chargingPatterns, xAxis.get(i),
							this.allNodes.get(j).selfishRes.originalSkips);
					double savings = (((double) this.allNodes.get(j).totalCycles - (double) num)
							/ (double) this.allNodes.get(j).totalCycles) * 100;
					sum = sum + savings;
				}
				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));
			}
		} else if (label.compareTo("cooperative") == 0) {

			// Case for cooperative. From stable matching
			
			
		
			for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;

				for (int i = 0; i < matchedPairs.size(); i++) {
					// System.out.println("At i " + i);

					if (matchedPairs.get(i) != null) {

						if (this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB] != null) {
							// System.out.println(" ******** " +
							// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB]);
							Nodes a = this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].sourceNode;
							Nodes b = this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].sourceNode;
							int num1 = getNumberOfSkips(a.chargingPatterns, xAxis.get(j),
									this.result[matchedPairs.get(i).indexA][matchedPairs
											.get(i).indexB].originalSkips_Source);
							int num2 = getNumberOfSkips(b.chargingPatterns, xAxis.get(j),
									this.result[matchedPairs.get(i).indexB][matchedPairs
											.get(i).indexA].originalSkips_Source);

							int num1Current = getNumberOfChargingCycles(a.chargingPatterns, xAxis.get(j), null);
							int num2Current = getNumberOfChargingCycles(b.chargingPatterns, xAxis.get(j), null);
							double sum1 = ((double) num1 / (double) num1Current) * 100;
							double sum2 = (((double) num2) / (double) num2Current) * 100;

							if (num1Current == 0)
								sum1 = 0;
							if (num2Current == 0)
								sum2 = 0;
							sum = sum + sum1 + sum2;
							
							
						}
					} else {
						System.out.println("Null Matched Pairs found");
						System.exit(0);
					}

				}
				
				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));

			}
		}

		return yAxis;
	}

	public ArrayList<Double> getAverageSavingsInChargingCyclesPerDay(ArrayList<Double> xAxis, String label) {
		ArrayList<Double> yAxis = new ArrayList<>();

		if (label.compareTo("selfish") == 0) {
			// Make for selfish
			double startTime = 0;
			for (int i = 0; i < xAxis.size(); i++) {
				double sum = 0;
				for (int j = 0; j < this.allNodes.size(); j++) {
					int num = getNumberOfChargingCyclesPerDay(this.allNodes.get(j).chargingPatterns, startTime,
							xAxis.get(i), this.allNodes.get(j).selfishRes.originalSkips);
					double savings = (((double) this.allNodes.get(j).totalCycles - (double) num)
							/ (double) this.allNodes.get(j).totalCycles) * 100;
					sum = sum + savings;
					
				}
				
				startTime = xAxis.get(i);
				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));
			}
		} else if (label.compareTo("cooperative") == 0) {

			double startTime = 0;
			// Case for cooperative. From stable matching
			for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;

				for (int i = 0; i < matchedPairs.size(); i++) {
					// System.out.println("At i " + i);

					if (matchedPairs.get(i) != null) {

						if (this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB] != null) {
							// System.out.println(" ******** " +
							// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB]);
							Nodes a = this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].sourceNode;
							Nodes b = this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].sourceNode;
							int num1 = getNumberOfSkipsPerDay(a.chargingPatterns,startTime, xAxis.get(j),
									this.result[matchedPairs.get(i).indexA][matchedPairs
											.get(i).indexB].originalSkips_Source);
							int num2 = getNumberOfSkipsPerDay(b.chargingPatterns, startTime, xAxis.get(j),
									this.result[matchedPairs.get(i).indexB][matchedPairs
											.get(i).indexA].originalSkips_Source);

							int num1Current = getNumberOfChargingCyclesPerDay(a.chargingPatterns, startTime, xAxis.get(j), null);
							int num2Current = getNumberOfChargingCyclesPerDay(b.chargingPatterns, startTime, xAxis.get(j), null);
							double sum1 = ((double) num1 / (double) num1Current) * 100;
							double sum2 = (((double) num2) / (double) num2Current) * 100;

							if (num1Current == 0)
								sum1 = 0;
							if (num2Current == 0)
								sum2 = 0;
							sum = sum + sum1 + sum2;

							/*
							 * if(j==xAxis.size()-1) {
							 * 
							 * //Printing debugging results for selfish and TS =
							 * 0 System.out.println(
							 * "----Total charging cycles : " + a.totalCycles +
							 * " original skips size self " +
							 * a.selfishRes.originalSkips.size() +
							 * " , original skips size coop " +
							 * this.result[matchedPairs.get(i).indexA][
							 * matchedPairs.get(i).indexB].originalSkips_Source.
							 * size() );
							 * 
							 * System.out.println("Skip sequence for Self \n");
							 * 
							 * for(int k = 0;
							 * k<a.selfishRes.originalSkips.size();k++) {
							 * System.out.print(a.selfishRes.originalSkips.get(
							 * k) + " , "); }
							 * 
							 * System.out.println("Skip sequence for Coop \n");
							 * 
							 * for(int k = 0;
							 * k<this.result[matchedPairs.get(i).indexA][
							 * matchedPairs.get(i).indexB].originalSkips_Source.
							 * size();k++) {
							 * System.out.print(this.result[matchedPairs.get(i).
							 * indexA][matchedPairs.get(i).indexB].
							 * originalSkips_Source.get(k) + " , "); }
							 * 
							 * System.out.println();
							 * 
							 * 
							 * 
							 * System.out.println("Skips from formula : " +
							 * (num1Current - num1) + " , " +
							 * a.selfishRes.totalSkips); System.exit(0); }
							 */

						}
					} else {
						System.out.println("Null Matched Pairs found");
						System.exit(0);
					}

				}
				startTime = xAxis.get(j);
				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));

			}
		}

		return yAxis;
	}
	
	public ArrayList<Double> getAverageSavingsInChargingCyclesDay(ArrayList<Double> xAxis, String label) {
		ArrayList<Double> yAxis = new ArrayList<>();

		if (label.compareTo("selfish") == 0) {
			// Make for selfish
	
			for (int i = 0; i < xAxis.size(); i++) {
				double sum = 0;
				for (int j = 0; j < this.allNodes.size(); j++) {
					int num = getNumberOfChargingCycles(this.allNodes.get(j).chargingPatterns,
							xAxis.get(i), this.allNodes.get(j).selfishRes.originalSkips);
					double savings = (((double) this.allNodes.get(j).totalCycles - (double) num)
							/ (double) this.allNodes.get(j).totalCycles) * 100;
					sum = sum + savings;
					
				}
				
			
				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));
			}
		} else if (label.compareTo("cooperative") == 0) {

		
			// Case for cooperative. From stable matching
			for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;

				for (int i = 0; i < matchedPairs.size(); i++) {
					// System.out.println("At i " + i);

					if (matchedPairs.get(i) != null) {

						if (this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB] != null) {
							// System.out.println(" ******** " +
							// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB]);
							Nodes a = this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].sourceNode;
							Nodes b = this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].sourceNode;
							int num1 = getNumberOfSkips(a.chargingPatterns, xAxis.get(j),
									this.result[matchedPairs.get(i).indexA][matchedPairs
											.get(i).indexB].originalSkips_Source);
							int num2 = getNumberOfSkips(b.chargingPatterns, xAxis.get(j),
									this.result[matchedPairs.get(i).indexB][matchedPairs
											.get(i).indexA].originalSkips_Source);

							int num1Current = getNumberOfChargingCycles(a.chargingPatterns, xAxis.get(j), null);
							int num2Current = getNumberOfChargingCycles(b.chargingPatterns, xAxis.get(j), null);
							double sum1 = ((double) num1 / (double) num1Current) * 100;
							double sum2 = (((double) num2) / (double) num2Current) * 100;

							if (num1Current == 0)
								sum1 = 0;
							if (num2Current == 0)
								sum2 = 0;
							sum = sum + sum1 + sum2;

							/*
							 * if(j==xAxis.size()-1) {
							 * 
							 * //Printing debugging results for selfish and TS =
							 * 0 System.out.println(
							 * "----Total charging cycles : " + a.totalCycles +
							 * " original skips size self " +
							 * a.selfishRes.originalSkips.size() +
							 * " , original skips size coop " +
							 * this.result[matchedPairs.get(i).indexA][
							 * matchedPairs.get(i).indexB].originalSkips_Source.
							 * size() );
							 * 
							 * System.out.println("Skip sequence for Self \n");
							 * 
							 * for(int k = 0;
							 * k<a.selfishRes.originalSkips.size();k++) {
							 * System.out.print(a.selfishRes.originalSkips.get(
							 * k) + " , "); }
							 * 
							 * System.out.println("Skip sequence for Coop \n");
							 * 
							 * for(int k = 0;
							 * k<this.result[matchedPairs.get(i).indexA][
							 * matchedPairs.get(i).indexB].originalSkips_Source.
							 * size();k++) {
							 * System.out.print(this.result[matchedPairs.get(i).
							 * indexA][matchedPairs.get(i).indexB].
							 * originalSkips_Source.get(k) + " , "); }
							 * 
							 * System.out.println();
							 * 
							 * 
							 * 
							 * System.out.println("Skips from formula : " +
							 * (num1Current - num1) + " , " +
							 * a.selfishRes.totalSkips); System.exit(0); }
							 */

						}
					} else {
						System.out.println("Null Matched Pairs found");
						System.exit(0);
					}

				}
				
				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));

			}
		}

		return yAxis;
	}


	public ArrayList<Double> getEnergyExchanged(ArrayList<Double> xAxis) {
		ArrayList<Double> yAxis = new ArrayList<>();
		for (int j = 0; j < xAxis.size(); j++) {
			double sum = 0;

			// System.out.println(" ******** " +
			// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB]);

			// sum = sum + getNumberOfChargingCycles(a.chargingPatterns,
			// xAxis.get(j),
			// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].originalSkips_Source)+getNumberOfChargingCycles(b.chargingPatterns,
			// xAxis.get(j),this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].originalSkips_Source);
			for (int i = 0; i < matchedPairs.size(); i++) {
				if (this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB] != null) {
					Nodes a = this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].sourceNode;
					Nodes b = this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].sourceNode;
					sum = sum
							+ getTotalExchangedEnergy(a.chargingPatterns, xAxis.get(j),
									this.result[matchedPairs.get(i).indexA][matchedPairs
											.get(i).indexB].originalEnergySource,
							this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].originalSkips_Source,
							"cooperative")
							+ getTotalExchangedEnergy(b.chargingPatterns, xAxis.get(j),
									this.result[matchedPairs.get(i).indexB][matchedPairs
											.get(i).indexA].originalEnergySource,
									this.result[matchedPairs.get(i).indexB][matchedPairs
											.get(i).indexA].originalSkips_Source,
									"cooperative");
					// totalEnergy = totalEnergy + a.totalEnergy +
					// b.totalEnergy;
				}

			}
			yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));

		}

		return yAxis;
	}

	public ArrayList<Double> getAverageReceivedEnergy(ArrayList<Double> xAxis, String label) {

		ArrayList<Double> yAxis = new ArrayList<>();

		if (label.compareTo("current") == 0) {
			for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;
				double totalEnergy = 0;
				for (int i = 0; i < this.allNodes.size(); i++) {
					sum = sum + getTotalReceivedEnergy(this.allNodes.get(i).chargingPatterns, xAxis.get(j), null, null,
							"current");
					totalEnergy = totalEnergy + this.allNodes.get(i).totalEnergy;

				}

				yAxis.add((sum / totalEnergy) * 100);
			}

		}

		else if (label.compareTo("selfish") == 0) {
			for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;
				double totalEnergy = 0;
				for (int i = 0; i < this.allNodes.size(); i++) {
					sum = sum + getTotalReceivedEnergy(this.allNodes.get(i).chargingPatterns, xAxis.get(j), null,
							this.allNodes.get(i).selfishRes.originalSkips, "selfish");
					totalEnergy = totalEnergy + this.allNodes.get(i).totalEnergy;
				}

				yAxis.add((sum / totalEnergy) * 100);
			}
		}

		else if (label.compareTo("cooperative") == 0) {

			// Case for cooperative. From stable matching
			for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;
				double totalEnergy = 0;

				// System.out.println(" ******** " +
				// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB]);

				// sum = sum + getNumberOfChargingCycles(a.chargingPatterns,
				// xAxis.get(j),
				// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].originalSkips_Source)+getNumberOfChargingCycles(b.chargingPatterns,
				// xAxis.get(j),this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].originalSkips_Source);
				for (int i = 0; i < matchedPairs.size(); i++) {
					if (this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB] != null) {
						Nodes a = this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].sourceNode;
						Nodes b = this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].sourceNode;
						sum = sum
								+ getTotalReceivedEnergy(a.chargingPatterns, xAxis.get(j),
										this.result[matchedPairs.get(i).indexA][matchedPairs
												.get(i).indexB].originalEnergySource,
								this.result[matchedPairs.get(i).indexA][matchedPairs
										.get(i).indexB].originalSkips_Source, "cooperative")
								+ getTotalReceivedEnergy(b.chargingPatterns, xAxis.get(j),
										this.result[matchedPairs.get(i).indexB][matchedPairs
												.get(i).indexA].originalEnergySource,
										this.result[matchedPairs.get(i).indexB][matchedPairs
												.get(i).indexA].originalSkips_Source,
										"cooperative");
						totalEnergy = totalEnergy + a.totalEnergy + b.totalEnergy;
					}

				}
				yAxis.add(Double.valueOf((sum / totalEnergy) * 100));

			}
		}

		return yAxis;
	}

	public ArrayList<Double> getAverageSavedEnergy(ArrayList<Double> xAxis, String label) {

		ArrayList<Double> yAxis = new ArrayList<>();
		if (label.compareTo("selfish") == 0) {
			for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;
				double totalEnergy = 0;
				for (int i = 0; i < this.allNodes.size(); i++) {
					double num = getTotalReceivedEnergy(this.allNodes.get(i).chargingPatterns, xAxis.get(j), null,
							this.allNodes.get(i).selfishRes.originalSkips, "selfish");
					sum = sum + ((double) (this.allNodes.get(i).totalEnergy - num)
							/ (double) this.allNodes.get(i).totalEnergy) * 100;
					// sum = sum +
					// getTotalReceivedEnergy(this.allNodes.get(i).chargingPatterns,
					// xAxis.get(j), null,
					// this.allNodes.get(i).selfishRes.originalSkips,
					// "selfish");
					totalEnergy = totalEnergy + this.allNodes.get(i).totalEnergy;
				}

				yAxis.add((sum / (double) this.allNodes.size()));
			}
		}

		else if (label.compareTo("cooperative") == 0) {

			// Case for cooperative. From stable matching
			for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;
				// double totalEnergy = 0;

				// System.out.println(" ******** " +
				// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB]);

				// sum = sum + getNumberOfChargingCycles(a.chargingPatterns,
				// xAxis.get(j),
				// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].originalSkips_Source)+getNumberOfChargingCycles(b.chargingPatterns,
				// xAxis.get(j),this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].originalSkips_Source);
				for (int i = 0; i < matchedPairs.size(); i++) {
					if (this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB] != null) {
						Nodes a = this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].sourceNode;
						Nodes b = this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].sourceNode;

						double numA = getTotalReceivedEnergy(a.chargingPatterns, xAxis.get(j),
								this.result[matchedPairs.get(i).indexA][matchedPairs
										.get(i).indexB].originalEnergySource,
								this.result[matchedPairs.get(i).indexA][matchedPairs
										.get(i).indexB].originalSkips_Source,
								"cooperative");
						double numB = getTotalReceivedEnergy(b.chargingPatterns, xAxis.get(j),
								this.result[matchedPairs.get(i).indexB][matchedPairs
										.get(i).indexA].originalEnergySource,
								this.result[matchedPairs.get(i).indexB][matchedPairs
										.get(i).indexA].originalSkips_Source,
								"cooperative");
						double numAcurrent = getTotalReceivedEnergy(a.chargingPatterns, xAxis.get(j), null, null,
								"current");
						double numBcurrent = getTotalReceivedEnergy(b.chargingPatterns, xAxis.get(j), null, null,
								"current");
						double sum1 = ((((numAcurrent - numA) / numAcurrent) * 100));
						double sum2 = (((numBcurrent - numB) / (double) numBcurrent) * 100);

						if (numAcurrent == 0)
							sum1 = 0;
						if (numBcurrent == 0)
							sum2 = 0;
						sum = sum + sum1 + sum2;
						// sum = sum +
						// getTotalReceivedEnergy(a.chargingPatterns,
						// xAxis.get(j),
						// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].originalEnergySource,
						// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].originalSkips_Source,"cooperative")+getTotalReceivedEnergy(b.chargingPatterns,
						// xAxis.get(j),this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].originalEnergySource,this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].originalSkips_Source,"cooperative");
						// totalEnergy = totalEnergy + a.totalEnergy +
						// b.totalEnergy;
					}

				}
				yAxis.add(Double.valueOf((sum / (double) this.allNodes.size())));

			}
		}

		return yAxis;
	}

	public void graph1() throws IOException {
		// Average number of charging cycles by all users current.
		ArrayList<Double> xAxis = new ArrayList<>();
		xAxis = getxAxis();
		ArrayList<Double> yAxis = new ArrayList<>();
		yAxis = getAverageChargingCycles(xAxis, "current");
		GraphPair pair = new GraphPair(xAxis, yAxis);
		pair.plot("averageCyclesCurrent");
	}

	public void graph2() throws IOException {
		// Average number of charging cycles selfish
		ArrayList<Double> xAxis = new ArrayList<>();
		xAxis = getxAxis();
		ArrayList<Double> yAxis = new ArrayList<>();
		yAxis = getAverageChargingCycles(xAxis, "selfish");
		GraphPair pair = new GraphPair(xAxis, yAxis);
		pair.plot("averageChargingCyclesSelfish");
	}

	public void graph3() throws IOException {

		// Average number of charging cycles by all users cooperative.
		ArrayList<Double> xAxis = new ArrayList<>();
		xAxis = getxAxis();
		ArrayList<Double> yAxis = new ArrayList<>();
		yAxis = getAverageChargingCycles(xAxis, "cooperative");
		GraphPair pair = new GraphPair(xAxis, yAxis);
		pair.plot("averageChargingCyclesCooperative");
	}

	public void graph4() throws IOException {

		// Average energy (%) wise, only for cooperative
		ArrayList<Double> xAxis = new ArrayList<>();
		xAxis = getxAxis();
		ArrayList<Double> yAxis = new ArrayList<>();
		yAxis = getAverageReceivedEnergy(xAxis, "current");
		GraphPair pair = new GraphPair(xAxis, yAxis);
		pair.plot("averageEnergyUsedCurrent");
	}

	public void graph5() throws IOException {

		// Average energy (%) wise, only for cooperative
		ArrayList<Double> xAxis = new ArrayList<>();
		xAxis = getxAxis();
		ArrayList<Double> yAxis = new ArrayList<>();
		yAxis = getAverageReceivedEnergy(xAxis, "selfish");
		GraphPair pair = new GraphPair(xAxis, yAxis);
		pair.plot("averageEnergyUsedselfish");
	}

	public void graph6() throws IOException {

		// Average energy (%) wise, only for cooperative
		ArrayList<Double> xAxis = new ArrayList<>();
		xAxis = getxAxis();
		ArrayList<Double> yAxis = new ArrayList<>();
		yAxis = getAverageReceivedEnergy(xAxis, "cooperative");
		GraphPair pair = new GraphPair(xAxis, yAxis);
		pair.plot("averageEnergyUsedcooperative");
	}

	/**
	 * Method needs to be independantly called. Only generates one column of
	 * data per call. tE -> transfer efficiency.
	 */
	public ArrayList<Double> graph7() {
		// Average savings (decrease) in num of charging cycles vs Transfer
		// efficiency.
		ArrayList<Double> yAxis = new ArrayList<>();
		ArrayList<Double> xAxis = new ArrayList<>();

		// Selfish
		xAxis.add((double) 6000000);
		yAxis = getAverageSavingsInChargingCycles(xAxis, "selfish");

		return yAxis;

	}

	/**
	 * Method needs to be independantly called. Only generates one column of
	 * data per call. tE -> transfer efficiency.
	 */
	public ArrayList<Double> graph8() {
		// Average savings (decrease) in savings in energy vs Transfer
		// efficiency.
		ArrayList<Double> yAxis = new ArrayList<>();
		ArrayList<Double> xAxis = new ArrayList<>();

		// selfish
		xAxis.add((double) 6000000);
		yAxis = getAverageSavedEnergy(xAxis, "selfish");

		return yAxis;

	}

	/**
	 * Method needs to be independantly called. Only generates one column of
	 * data per call. tE -> transfer efficiency.
	 */
	public ArrayList<Double> graph9() {
		// Average savings (decrease) in average charging vs Transfer
		// efficiency.
		ArrayList<Double> yAxis = new ArrayList<>();
		ArrayList<Double> xAxis = new ArrayList<>();

		// Cooperative
		xAxis.add((double) 6000000);
		yAxis = getAverageSavingsInChargingCycles(xAxis, "cooperative");

		return yAxis;

	}

	/**
	 * Method needs to be independantly called. Only generates one column of
	 * data per call. tE -> transfer efficiency.
	 */
	public ArrayList<Double> graph10() {
		// Average savings (decrease) in total energy used vs Transfer
		// efficiency.
		ArrayList<Double> yAxis = new ArrayList<>();
		ArrayList<Double> xAxis = new ArrayList<>();

		// Cooperative
		xAxis.add((double) 6000000);
		yAxis = getAverageSavedEnergy(xAxis, "cooperative");

		return yAxis;

	}

	/**
	 * Method needs to be independantly called. Only generates one column of
	 * data per call. tS -> transfer Speed.
	 */
	public ArrayList<Double> graph11() {
		// Average savings (decrease) in average Charging Cycles vs Transfer
		// speed.
		ArrayList<Double> yAxis = new ArrayList<>();
		ArrayList<Double> xAxis = new ArrayList<>();

		// selfish
		xAxis.add((double) 6000000);
		yAxis = getAverageSavingsInChargingCycles(xAxis, "selfish");

		return yAxis;

	}

	/**
	 * Method needs to be independantly called. Only generates one column of
	 * data per call. tS -> transfer speed.
	 */
	public ArrayList<Double> graph12() {
		// Average savings (decrease) in total energy used vs Transfer Speed.
		ArrayList<Double> yAxis = new ArrayList<>();
		ArrayList<Double> xAxis = new ArrayList<>();

		// selfish
		xAxis.add((double) 6000000);
		yAxis = getAverageSavedEnergy(xAxis, "selfish");

		return yAxis;

	}

	
	public ArrayList<Double> getSavingsIndividualPairs(ArrayList<Double> xAxis, String label) {
		ArrayList<Double> yAxis = new ArrayList<>();

		if (label.compareTo("selfish") == 0) {
			// Make for selfish

			
				
				for (int j = 0; j < this.allNodes.size(); j++) {
					double sum = 0;
					
					int num1 = getNumberOfSkips(this.allNodes.get(j).chargingPatterns, xAxis.get(0),this.allNodes.get(j).selfishRes.originalSkips);
					int num1Current = getNumberOfChargingCycles(this.allNodes.get(j).chargingPatterns, xAxis.get(0), null);
					
					 sum = ((double) num1 / (double) num1Current) * 100;
				
					
					yAxis.add(Double.valueOf((sum)));
				}
				
			
		} else if (label.compareTo("cooperative") == 0) {

			// Case for cooperative. From stable matching
			
			
			//for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;

				for (int i = 0; i < matchedPairs.size(); i++) {
					// System.out.println("At i " + i);

					if (matchedPairs.get(i) != null) {

						if (this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB] != null) {
							// System.out.println(" ******** " +
							// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB]);
							Nodes a = this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB].sourceNode;
							Nodes b = this.result[matchedPairs.get(i).indexB][matchedPairs.get(i).indexA].sourceNode;
							int num1 = getNumberOfSkips(a.chargingPatterns, xAxis.get(0),
									this.result[matchedPairs.get(i).indexA][matchedPairs
											.get(i).indexB].originalSkips_Source);
							int num2 = getNumberOfSkips(b.chargingPatterns, xAxis.get(0),
									this.result[matchedPairs.get(i).indexB][matchedPairs
											.get(i).indexA].originalSkips_Source);

							int num1Current = getNumberOfChargingCycles(a.chargingPatterns, xAxis.get(0), null);
							int num2Current = getNumberOfChargingCycles(b.chargingPatterns, xAxis.get(0), null);
							double sum1 = ((double) num1 / (double) num1Current) * 100;
							double sum2 = (((double) num2) / (double) num2Current) * 100;

							if (num1Current == 0)
								sum1 = 0;
							if (num2Current == 0)
								sum2 = 0;
							sum =  (sum1 + sum2)/2;
							
							

							yAxis.add(sum);

						}
					} else {
						System.out.println("Null Matched Pairs found");
						System.exit(0);
					}

				//}
				
				

			}
		}
		else if (label.compareTo("all") == 0) {

			// Case for cooperative. From stable matching
			
			
			//for (int j = 0; j < xAxis.size(); j++) {
				double sum = 0;

				for (int i = 0; i < this.allNodes.size(); i++) {
					
					for(int j=i+1; j<this.allNodes.size();j++)
					{
					// System.out.println("At i " + i);

					//if (matchedPairs.get(i) != null) {
						

						if (this.result[i][j] != null) {
							// System.out.println(" ******** " +
							// this.result[matchedPairs.get(i).indexA][matchedPairs.get(i).indexB]);
							Nodes a = this.result[i][j].sourceNode;
							Nodes b = this.result[j][i].sourceNode;
							int num1 = getNumberOfSkips(a.chargingPatterns, xAxis.get(0),
									this.result[i][j].originalSkips_Source);
							int num2 = getNumberOfSkips(b.chargingPatterns, xAxis.get(0),
									this.result[j][i].originalSkips_Source);

							int num1Current = getNumberOfChargingCycles(a.chargingPatterns, xAxis.get(0), null);
							int num2Current = getNumberOfChargingCycles(b.chargingPatterns, xAxis.get(0), null);
							double sum1 = ((double) num1 / (double) num1Current) * 100;
							double sum2 = (((double) num2) / (double) num2Current) * 100;

							if (num1Current == 0)
								sum1 = 0;
							if (num2Current == 0)
								sum2 = 0;
							sum =  (sum1 + sum2)/2;
							
							

							yAxis.add(sum);
						
						}
					else {
						System.out.println("Null Matched Pairs found");
						System.exit(0);
					}
					}
					}
				//}
				
				

			
		}

		return yAxis;
	}

	
	public ArrayList<Double> getInSkips(long endTime)
	{
		ArrayList<Double> skips = new ArrayList<>();
		ArrayList<Double> xAxis = new ArrayList<>();
		xAxis.add((double) endTime);
		skips = getSavingsIndividualPairs(xAxis, "all");
		return skips;
	}
	/**
	 * Method needs to be independantly called. Only generates one column of
	 * data per call. tS -> transfer efficiency.
	 */
	public ArrayList<Double> graph13(long endTime) {
		// Average savings (decrease) in average charging cycles vs Transfer
		// speed.
		ArrayList<Double> yAxis = new ArrayList<>();
		ArrayList<Double> xAxis = new ArrayList<>();

		// Cooperative
		xAxis.add((double) endTime);
		yAxis = getAverageSavingsInChargingCycles(xAxis, "cooperative");

		return yAxis;

	}

	/**
	 * Method needs to be independantly called. Only generates one column of
	 * data per call. tS-> transfer efficiency.
	 */
	public ArrayList<Double> graph14(long endTime) {
		// Average savings (decrease) in total energy used vs Transfer speed.
		ArrayList<Double> yAxis = new ArrayList<>();
		ArrayList<Double> xAxis = new ArrayList<>();

		// Cooperative
		xAxis.add((double) endTime);
		yAxis = getAverageSavedEnergy(xAxis, "cooperative");

		return yAxis;

	}

	/**
	 * Time wise energy exchanged
	 * 
	 * @throws IOException
	 */
	public void graph15() throws IOException {
		ArrayList<Double> xAxis = getxAxis();
		ArrayList<Double> yAxis = new ArrayList<>();
		yAxis = getEnergyExchanged(xAxis);
		GraphPair pair = new GraphPair(xAxis, yAxis);
		pair.plot("averageEnergyExhanged.csv");

	}

	public ArrayList<Double> getSavingsEnergyPerTime(String mode) {
		ArrayList<Double> xAxis = new ArrayList<>();
		ArrayList<Double> yAxis = new ArrayList<>();
		xAxis = getxAxis();
		yAxis = getAverageSavedEnergy(xAxis, mode);
		return yAxis;
	}

	public ArrayList<Double> getSavingsCyclesPerTime(String mode) {
		ArrayList<Double> xAxis = new ArrayList<>();
		ArrayList<Double> yAxis = new ArrayList<>();
		xAxis = getxAxis();
		yAxis = getAverageSavingsInChargingCycles(xAxis, mode);
		return yAxis;
	}

	public ArrayList<Double> getSavingsCyclesPerDay(String mode, int numDays) {
		ArrayList<Double> xAxis = new ArrayList<>();
		ArrayList<Double> yAxis = new ArrayList<>();
		xAxis = getxAxisPerDay(numDays);
		yAxis = getAverageSavingsInChargingCyclesPerDay(xAxis, mode);
		return yAxis;
	}
	
	public ArrayList<Double> getSavingsCyclesDay(String mode, int numDays) {
		ArrayList<Double> xAxis = new ArrayList<>();
		ArrayList<Double> yAxis = new ArrayList<>();
		xAxis = getxAxisPerDay(numDays);
		yAxis = getAverageSavingsInChargingCyclesDay(xAxis, mode);
		return yAxis;
	}


	public ArrayList<Double> getEnergyReceivedPerTime(String mode) {
		ArrayList<Double> xAxis = new ArrayList<>();
		ArrayList<Double> yAxis = new ArrayList<>();
		xAxis = getxAxis();
		yAxis = getEnergyExchanged(xAxis);
		return yAxis;
	}

}
