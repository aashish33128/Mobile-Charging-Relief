import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class MeetingTime {

	ArrayList<Integer> hours = new ArrayList<>();
	LinkedHashMap<Integer, Integer> duration = new LinkedHashMap<>();

	/**
	 * 
	 * @param hours
	 * @param minutesToAdd
	 *            Since we are starting from 24th hour of the first day, we have
	 *            to add 1440 for each day to represnt the next day
	 */
	public void setHours(int[] hours, int minutesToAdd) {
		// System.out.println("Size of hours " + hours.length);
		Arrays.sort(hours);
		for (int i : hours) {
			/*
			 * System.out.println(i + " , " + minutesToAdd);
			 * System.out.println(i*60 + minutesToAdd);
			 */
			this.hours.add(i * 60 + minutesToAdd);
		}
	}

	public void setContactDuration(int[] contacts, int day) {
		int count = 0;
		/**
		 * 
		 */
		if (day != 0) {
			count = hours.size() - contacts.length;
		}

		// System.out.println("Count is " + count);
		for (int i : contacts) {
			this.duration.put(this.hours.get(count), i);
			count++;
		}
	}
}
