import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class MeetingTimeConfigurationManager {
	public static LinkedHashMap<Integer, String> mithourlymeetings = new LinkedHashMap<>();
	public static LinkedHashMap<Integer, String> cambridgehourlymeetings = new LinkedHashMap<>();
	public static LinkedHashMap<Integer, String> hagglehourlymeetings = new LinkedHashMap<>();

	public static LinkedHashMap<Integer, String> mitnummeetings = new LinkedHashMap<>();
	public static LinkedHashMap<Integer, String> cambridgenummeetings = new LinkedHashMap<>();
	public static LinkedHashMap<Integer, String> hagglenummeetings = new LinkedHashMap<>();

	public static LinkedHashMap<Integer, String> mitcontactduration = new LinkedHashMap<>();
	public static LinkedHashMap<Integer, String> cambridgecontactduration = new LinkedHashMap<>();
	public static LinkedHashMap<Integer, String> hagglecontactduration = new LinkedHashMap<>();

	public MeetingTimeConfigurationManager() {
		loadData();
	}

	
	/**
	 * These data are hardcoded. You can change these parameters to include data files.
	 */
	public void loadData() {
		String hourlymeetingsfile = "D:/DTN Research/Energy Sharing Journal version/Data/hourlydistribution.csv";
		String nummeetingsfile = "D:/DTN Research/Energy Sharing Journal version/Data/meetingperday.csv";
		String contactdurationfile = "D:/DTN Research/Energy Sharing Journal version/Data/contactduration.csv";

		// load hourly meetings data
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(hourlymeetingsfile)));
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] column = line.split(",");
				int key = Integer.parseInt(column[0]);
				String mitData = column[1].replace("%", "");
				String cambridgeData = column[2].replace("%", "");
				String haggleData = column[3].replace("%", "");
				mithourlymeetings.put(key, mitData);
				cambridgehourlymeetings.put(key, cambridgeData);
				hagglehourlymeetings.put(key, haggleData);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// load num of meetings data
		BufferedReader reader1 = null;
		try {
			reader1 = new BufferedReader(new FileReader(new File(nummeetingsfile)));
			String line = reader1.readLine();
			while ((line = reader1.readLine()) != null) {
				String[] column = line.split(",");
				int key = Integer.parseInt(column[0]);
				String mitData = column[1].replace("%", "");
				String cambridgeData = column[2].replace("%", "");
				String haggleData = column[3].replace("%", "");
				mitnummeetings.put(key, mitData);
				cambridgenummeetings.put(key, cambridgeData);
				hagglenummeetings.put(key, haggleData);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// load contact duration data
		BufferedReader reader2 = null;
		try {
			reader2 = new BufferedReader(new FileReader(new File(contactdurationfile)));
			String line = reader2.readLine();
			while ((line = reader2.readLine()) != null) {
				String[] column = line.split(",");
				int key = Integer.parseInt(column[0]);
				String mitData = column[1].replace("%", "");
				String cambridgeData = column[1].replace("%", "");
				String haggleData = column[2].replace("%", "");
				mitcontactduration.put(key, mitData);
				cambridgecontactduration.put(key, cambridgeData);
				hagglecontactduration.put(key, haggleData);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * flag - 0 =MIT, 1= Cambridge, 2 = Haggle
	 */

	public void setMeetingParameters(int flag, MeetingTime meet,int startTime, int numDays) {
		// a random number for num of meetings for a day
		//2 iterations for two days. handled by num of minutes to add.
		int minutesToAdd = startTime;
	//	int startMin = minutesToAdd;
		for(int i=0 ; i<numDays;i++)
		{
			if(i!=0)
			{
				minutesToAdd = minutesToAdd + 1440;
			}
		Random rand = new Random(); 
		float min = 0.00f;
		float max = 1.00f;
		double value = min + rand.nextDouble() * (max - min) * 100;
		int hr = 0;
		
		if(flag==0)
		{
		hr = (int)(getNumofMeetings(value, flag)/3);
		}
		else if(flag==1)
		{
			hr = (int)(getNumofMeetings(value, flag)/2);
		}
		else
		{
			hr = (int)(getNumofMeetings(value, flag));
		}
	//	System.out.println("Total number of meetings : " + hr);
		if (hr != 0) {
			int[] hrs = getMeetingHrs(flag, hr);
			if (hrs.length > 0) {
				int[] contactDuration = getContactDurations(flag, hrs);
				/**
				 * minutesToAdd = minute equivalent tot hrs for the given dataset
				 */
				meet.setHours(hrs, minutesToAdd);
				meet.setContactDuration(contactDuration,i);

			}

		}
		//displayMeetingParameters(hr);
		}
	}
	
	

	public int[] getContactDurations(int flag, int[] hrsList) {
		Random rand = new Random();
		LinkedHashMap<Integer, String> tempContactDuration = new LinkedHashMap<>();
		float min = 0.00f;
		float max = 1.00f;
		int[] contacts = new int[hrsList.length];
		List<Integer> selectedDurations = new ArrayList<>();
		if (flag == 0) {
			tempContactDuration = mitcontactduration;
		} else if (flag == 1) {
			tempContactDuration = cambridgecontactduration;
		} else {
			tempContactDuration = hagglecontactduration;
		}

		int numgenerated = 0;
		while (numgenerated < hrsList.length) {
			double value = min + rand.nextDouble() * (max - min) * 100;
			int hour = getHourInformation(value, tempContactDuration);
			if (numgenerated == 0) {
				contacts[numgenerated] = hour;
				selectedDurations.add(hour);
				numgenerated++;
			} else {
				//if (!selectedDurations.contains(hour))
				{
					selectedDurations.add(hour);
					contacts[numgenerated] = hour;
					numgenerated++;
				}
			}

		}
		return contacts;
	}

	public int[] getMeetingHrs(int flag, int count) {
		Random rand = new Random();
		LinkedHashMap<Integer, String> temphourlyMeetings = new LinkedHashMap<>();
		float min = 0.00f;
		float max = 1.00f;
		int[] hrsDay = new int[count];
		List<Integer> selectedHours = new ArrayList<>();
		if (flag == 0) {
			temphourlyMeetings = mithourlymeetings;
		} else if (flag == 1) {
			temphourlyMeetings = cambridgehourlymeetings;
		} else {
			temphourlyMeetings = hagglehourlymeetings;
		}

		int numHrsgenerated = 0;
		while (numHrsgenerated < count) {
			double value = min + rand.nextDouble() * (max - min) * 100;
			int hour = getHourInformation(value, temphourlyMeetings);
			if (numHrsgenerated == 0) {
				hrsDay[numHrsgenerated] = hour;
				selectedHours.add(hour);
				numHrsgenerated++;
			} else {
				if (!selectedHours.contains(hour)) {
					selectedHours.add(hour);
					hrsDay[numHrsgenerated] = hour;
					numHrsgenerated++;
				}
			}

		}
		return hrsDay;
	}

	public int getNumofMeetings(double value, int flag) {
		LinkedHashMap<Integer, String> tempnumMeetings = new LinkedHashMap<>();
		if (flag == 0) {
			tempnumMeetings = mitnummeetings;
		} else if (flag == 1) {
			tempnumMeetings = cambridgenummeetings;
		} else {
			tempnumMeetings = hagglenummeetings;
		}
		return getHourInformation(value, tempnumMeetings);
	}

	public int getHourInformation(double value, LinkedHashMap<Integer, String> valueSet) {

		// System.out.println("Value is " + value);
		int predictedHour = -1;
		float maxProb = -1;
		for (int hrs : valueSet.keySet()) {
			float prob = Float.parseFloat(valueSet.get(hrs));
			if (prob <= value) {

				if (maxProb == -1) {
					maxProb = prob;
					predictedHour = hrs;
				} else {
					if (prob > maxProb) {
						maxProb = prob;
						predictedHour = hrs;
					}
				}

				// }
			}
		}
		// System.out.println("predicted " + predictedHour);
		return predictedHour;

	}


}
