import java.util.ArrayList;

public class SelfishResult {
	
	Nodes sourceNode;
	int[] skipSequence;
	ArrayList<Integer> originalSkips = new ArrayList<>();
	int totalSkips = 0;
	
	public SelfishResult(Nodes node, int[] skipSequence)
	{
		this.sourceNode = node;
		this.skipSequence = skipSequence;
	}
	
	
	
	public ArrayList<Integer> setOriginalSkips(ArrayList<ChargingPattern> decisionPoints, int[] skipSequence) {
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
			int currentSkip = skipSequence[i];
			
			
			System.out.println("pattern type " + c.patternType + " , " + currentSkip);

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

		/*if (debugMode) {
			System.out.println("Size is " + nodeA.totalCycles);
			System.out.println("Size is " + nodeB.totalCycles);
			System.out.println("Original Skips is : ");
			for (int i = 0; i < originalSkips.size(); i++) {
				System.out.println(originalSkips.get(i) + " , ");
			}
		}*/
		//setNumSkips();
		
		this.originalSkips = originalSkips;
		setNumSkips();
		
		return originalSkips;
	}

	
	public void setNumSkips()
	{
		for(int i=0;i<this.originalSkips.size();i++)
		{
			this.totalSkips = this.totalSkips + this.originalSkips.get(i);
			
		}
		
		System.out.println("Total size " + this.totalSkips);
	}

}
