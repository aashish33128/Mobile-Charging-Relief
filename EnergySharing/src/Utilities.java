import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utilities {

	// Returns the date value of the provided string. A date string has two
	// Parts joined by alphabet T.

	public static long getMinutes(String time, Date baseTime) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(baseTime);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		baseTime = cal.getTime();
		long hour = TimeUnit.MILLISECONDS.toMinutes(getDate(time).getTime() - baseTime.getTime());

		return hour;

	}

	public static Date getDate(String dateString) {

		// System.out.println(dateString);

		String date = dateString.split("T")[0];

		String time = dateString.split("T")[1];

		String finalString = "";
		SimpleDateFormat simpleDateFormat = null;

		if (dateString.contains("/")) {
			finalString = date + " " + time;
			simpleDateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		} else {
			finalString = date + " " + time.substring(0, time.indexOf("."));
			;
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}

		Date converteddate = null;
		try {
			converteddate = simpleDateFormat.parse(finalString);

			// System.out.println("date : " +
			// simpleDateFormat.format(converteddate));
		} catch (ParseException ex) {
			System.out.println("Exception " + ex);
		}
		// System.out.println("CurrentDate : " + finalString);
		return converteddate;
	}

	public static boolean checkForLastDischargingPoint(ArrayList<ChargingPattern> cp, long endTime) {
		for (int i = 0; i < cp.size(); i++) {
			if (endTime >= cp.get(i).dts && endTime <= cp.get(i).dte) {
				return true;
			}
		}
		return false;

	}

	public static ArrayList<Nodes> checkForTimeGaps(List<Nodes> allNodes) {
		ArrayList<Nodes> allNodesFiltered = new ArrayList<>();
		for (int j = 0; j < allNodes.size(); j++) {
			// Filter Charging Patterns
			boolean accepted = false;
			ArrayList<ChargingPattern> patterns = new ArrayList<>();
			patterns = allNodes.get(j).chargingPatterns;
			for (int i = 0; i < patterns.size() - 1; i++) {
				if (patterns.get(i).dte - patterns.get(i + 1).cts > 0) {
					System.out.println("Gap Detected " + patterns.get(i).dte + " , " + patterns.get(i + 1).cts);
					accepted = false;
					break;
				} else if (patterns.get(i).cte - patterns.get(i).dts > 0) {
					System.out.println("Gap Detected " + patterns.get(i).cte + " , " + patterns.get(i).cts);
					accepted = false;
					break;
				} else if (patterns.get(i).cte - (patterns.get(i).dts - patterns.get(i).dte) < 0) {
					System.out.println("Error  Detected ----- Over Discharge---");
					accepted = false;
					break;
				}

				// Check if level are different but time is same. Faulty Data.
				// Ignore.
				if (patterns.get(i).dle != patterns.get(i + 1).dle && patterns.get(i).dte == patterns.get(i + 1).dte) {
					System.out.println("Same time range. Ignored");
					accepted = false;
					break;
				}

				// check if the time is sequential. Not increasing and then
				// decreasing. Seen issue in the data
				if (patterns.get(i).cts > patterns.get(i + 1).cts || patterns.get(i).dte > patterns.get(i + 1).dte) {
					System.out.println("Problems in time sequence");
					accepted = false;
					break;
				}

				accepted = true;

			}
			if (accepted) {
				allNodesFiltered.add(allNodes.get(j));
			}
		}
		return allNodesFiltered;
	}

	public static ArrayList<Nodes> returnNodesWithTimeFrame(long startMinutes, long endMinutes, List<Nodes> nodes) {
		/**
		 * Get the charging cycles time in minutes..consider from 12 a.m as 0
		 * for the particular day
		 */

		// System.out.println("SIZE NODES " + nodes.size());
		ArrayList<Nodes> nodesWithTimeFilter = new ArrayList<>();
		int index = 0;
		// System.out.println(startMinutes + " , " + endMinutes);

		for (int i = 0; i < nodes.size(); i++) {
			ArrayList<ChargingPattern> chargeList = nodes.get(i).chargingPatterns;
			int count = 0;

			{

				long startTime = chargeList.get(0).cts;

				long currentEndTime = chargeList.get(chargeList.size() - 1).dte;
				/*
				 * System.out.println("S T " + startTime + " , " +
				 * currentEndTime );
				 */
				if (startTime <= startMinutes && currentEndTime >= endMinutes)// &&
																				// //checkForLastDischargingPoint(chargeList,
																				// endMinutes))
																				// {
				// Need to count the number of charging patterns in between.
				// very small number of charging cycles cause problem in
				// creating decision pointss.
				{
					// System.out.println("----------asdasdasd---------");
					ArrayList<ChargingPattern> includedCycles = new ArrayList<>();
					includedCycles = getChargingCycles(startMinutes, endMinutes, chargeList);
					// System.out.println("included " + includedCycles.size());

					if (includedCycles.size() > 3) {
						nodes.get(i).index = index;

						nodes.get(i).totalCycles = includedCycles.size();
						nodes.get(i).chargingPatterns = includedCycles;
						nodes.get(i).setTotalEnergy();
						nodesWithTimeFilter.add(nodes.get(i));
						index++;
					} else {
						System.out.println(
								"Removed Cycles " + nodes.get(i).index + " , Included Cycles " + includedCycles.size());
					}

					// System.exit(0);
					/*
					 * if(countNumOfChargingCycles(startMinutes, endMinutes,
					 * chargeList) > 3) { count++; }
					 */
					// count++;

				} else {
					System.out.println("Excluded cycles " + nodes.get(i).index + "Start Time " + startTime
							+ " , End Time " + currentEndTime);
				}
			}

		}

		return nodesWithTimeFilter;

	}

	public static boolean isRedundant(ChargingPattern cycle, ArrayList<ChargingPattern> inc) {
		for (int i = 0; i < inc.size(); i++) {
			if (inc.get(i).cts == cycle.cts) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<ChargingPattern> getChargingCycles(long startMinutes, long endMinutes,
			ArrayList<ChargingPattern> cycles) {
		// int count =0;
		ArrayList<ChargingPattern> includedCycles = new ArrayList<>();

		for (int i = 0; i < cycles.size(); i++) {
			if (cycles.get(i).dte >= startMinutes && cycles.get(i).cts <= endMinutes
					&& !isRedundant(cycles.get(i), includedCycles)) {

				includedCycles.add(cycles.get(i));
			}

			if (cycles.get(i).dte >= endMinutes) {
				break;
			}
		}
		return includedCycles;

	}

	public static ArrayList<Double> getTimeAxis(double startTime, double endTime) {
		ArrayList<Double> xAxis = new ArrayList<>();
		int numHrs = (int) (endTime - startTime) / (60);
		for (int i = 0; i <= numHrs; i = i + 2) {
			xAxis.add(Double.valueOf(startTime + (i * 60)));
		}

		// xAxis.add(Double.valueOf(startTime + (10000*60)));

		return xAxis;
	}

	public static double getAverage(ArrayList<ArrayList<Double>> list, int index) {
		double sum = 0;
		for (int i = 0; i < list.size(); i++) {
			ArrayList<Double> temp = new ArrayList<>();
			temp = list.get(i);
			sum = sum + temp.get(index);

		}

		return sum / (double) list.size();
	}

	public static double getAverageTime(ArrayList<ArrayList<Double>> list, int simulationNumber, int index) {
		double sum = 0;

		for (int i = 0; i < list.size(); i++) {
			ArrayList<Double> temp = new ArrayList<>();
			temp = list.get(i);
			sum = sum + temp.get(index);

		}

		return sum / (double) list.size();
	}

	public static void printCycles(ArrayList<ChargingPattern> cycles, String nodeId) {
		System.out.println("----------Charging Cycles for Nodes " + nodeId);
		for (int i = 0; i < cycles.size(); i++) {
			System.out.println(cycles.get(i).cls + " - " + cycles.get(i).cts);
			System.out.println(cycles.get(i).cle + " - " + cycles.get(i).cte);
			System.out.println(cycles.get(i).dle + " - " + cycles.get(i).dte);
		}
	}

}
