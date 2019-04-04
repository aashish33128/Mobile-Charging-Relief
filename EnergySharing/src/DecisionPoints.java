/**
 * @author Aashish Dhungana
 * 
 * Class that creates decision blocks from charging and discharging patterns
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DecisionPoints {
	public Nodes nodeA;
	public Nodes nodeB;
	public long startTime;
	public long endTime;
	public ArrayList<ChargingPattern> cpA = new ArrayList<>();
	public ArrayList<ChargingPattern> cpB = new ArrayList<>();

	public DecisionPoints(Nodes nodeA, Nodes nodeB, long startTime, long endTime) {
		this.nodeA = nodeA;
		this.nodeB = nodeB;
		this.startTime = startTime;
		this.endTime = endTime;
		/*
		 * this.cpA = this.nodeA.chargingPatterns; this.cpB =
		 * this.nodeB.chargingPatterns;
		 */
		createDecisionBlocks();
	}

	public ChargingPattern getChargingPattern(Nodes cp, long initPoint, long secondPoint) {
		ArrayList<ChargingPattern> temp = cp.chargingPatterns;
		ChargingPattern decisionBlock = new ChargingPattern();
		int cls = 0, cle = 0, dls = 0, dle = 0, totalCharge = 0, totalDischarge = 0 , patternType = -1;
		long cts = 0, cte = 0, dts = 0, dte = 0;
		boolean hasFirstHalf = false, hasSecondHalf = false;
		int count = 0;
		int i=0;
		
		
		for ( i = 0; i < cp.chargingPatterns.size(); i++) {

			//0 : Full charging block
			//1: Full charging with half/no discharging
			//2: Partial Charging (First Half)
			//3 : Partial Charging (Second Half)
			//4: Full discharging
			//5:half discharging (1st part)
			//6: half discharging (2nd part)
			//7 : Half discharging (middle portion)
			// Case 1 : Has same decision block as charging cycles
			if (temp.get(i).cts == initPoint && temp.get(i).dte == secondPoint) {
				
			//	System.out.println("Case 1 " + initPoint);
				cls = temp.get(i).cls;
				cle = temp.get(i).cle;
				dls = temp.get(i).dls;
				dle = temp.get(i).dle;
				hasFirstHalf = false;
				hasSecondHalf = false;
				cts = temp.get(i).cts;
				cte = temp.get(i).cte;
				dts = temp.get(i).dts;
				dte = temp.get(i).dte;
				totalCharge = temp.get(i).totalCharge;
				totalDischarge = temp.get(i).totalDischarge;
				patternType = 0;
				count++;
				break;

			}

			// Case 2 : Decision point cuts at the start and the charging end
			// point
			else if (temp.get(i).cts == initPoint && temp.get(i).cte == secondPoint) {
				//System.out.println("Case 2 " + initPoint);
				cls = temp.get(i).cls;
				cle = temp.get(i).cle;
				dls = cle;
				dle = cle;
				hasFirstHalf = false;
				hasSecondHalf = false;
				cts = initPoint;
				cte = secondPoint;
				dts = secondPoint;
				dte = secondPoint;
				totalCharge = temp.get(i).totalCharge;
				totalDischarge = 0;
				patternType = 1;
				count++;

				break;

			}

			// Case 3 : Decision point cuts at the start of the charging and in between the
			// discharging
			// time
			else if (temp.get(i).cts == initPoint && secondPoint > temp.get(i).dts
					&& secondPoint < temp.get(i).dte) {
				//System.out.println("Case 3 " + initPoint);

				cls = temp.get(i).cls;
				cle = temp.get(i).cle;
				dls = cle;
				double dischargeRate = getDischargingRate(temp.get(i).dts, temp.get(i).dte, temp.get(i).totalDischarge);
				dle = dls - (int) (dischargeRate * (secondPoint - temp.get(i).dts) + 0.5);

				hasFirstHalf = false;
				hasSecondHalf = false;
				cts = initPoint;
				cte = temp.get(i).cte;
				dts = cte;
				dte = secondPoint;
				totalCharge = temp.get(i).totalCharge;
				totalDischarge = dls - dle;
				patternType = 1;
				count++;
				break;

			}

			// Case 4 : cuts at the start of charging and in between charging
			else if (temp.get(i).cts == initPoint&& secondPoint > temp.get(i).cts
					&& secondPoint<temp.get(i).cte) {
			//	System.out.println("Case 4 " + initPoint);

				cls = temp.get(i).cls;

				double chargeRate = getChargingRate(temp.get(i).cts, temp.get(i).cte, temp.get(i).totalCharge);
				cle = cls + (int) (chargeRate * (secondPoint - temp.get(i).cts) + 0.5);
				dls = cle;
				dle = cle;

				hasFirstHalf = true;
				hasSecondHalf = false;
				cts = initPoint;
				cte = secondPoint;
				dts = cte;
				dte = cte;
				totalCharge = cle - cls;
				totalDischarge = 0;
				patternType = 2;
				count++;
				break;
			}

			// Case 5 : cuts at the end of charging and end of discharging.
			else if (temp.get(i).cte == initPoint && secondPoint == temp.get(i).dte) {
				//System.out.println("Case 5 " + initPoint);
				cls = temp.get(i).cle;
				cle = cls;
				dls = cle;
				dle = temp.get(i).dle;
				cts = initPoint;
				cte = initPoint;
				dts = initPoint;
				dte = secondPoint;
				hasFirstHalf = false;
				hasSecondHalf = false;
				totalCharge = 0;
				totalDischarge = temp.get(i).totalDischarge;
				patternType = 4;
				count++;
				break;
			}

			// Case 6 : cuts at between discharging(both lines)
			else if (initPoint > temp.get(i).cte && initPoint < temp.get(i).dte
					&& secondPoint > temp.get(i).cte && secondPoint < temp.get(i).dte) {
				
				//if(initPoint)
			//	System.out.println("Case 6 " + initPoint);
				double dischargeRate = getDischargingRate(temp.get(i).dts, temp.get(i).dte, temp.get(i).totalDischarge);
				
				dls = temp.get(i).dls - (int)(dischargeRate * (initPoint - temp.get(i).dts) +0.5);
				dle = temp.get(i).dls - (int)(dischargeRate * (secondPoint - temp.get(i).dts) + 0.5);
				cls = dls;
				cle = dls;
				cts = initPoint;
				cte = initPoint;
				dts = initPoint;
				dte = secondPoint;
				hasFirstHalf = false;
				hasSecondHalf = false;
				totalCharge = 0;
				totalDischarge = dls - dle;
				patternType = 7;
				count++;
				break;

			}
			
			//Case 7 : cuts both in between charging line
			else if (initPoint > temp.get(i).cts && initPoint < temp.get(i).cte
					&& secondPoint> temp.get(i).cts && secondPoint < temp.get(i).cte) {
				//System.out.println("-------------Case 7 " + initPoint);
				double chargeRate = getChargingRate(temp.get(i).cts, temp.get(i).cte, temp.get(i).totalCharge);
				
			
				cls = temp.get(i).cls + (int)(chargeRate * (initPoint - temp.get(i).cts) + 0.5);
				cle = temp.get(i).cls + (int)(chargeRate * (secondPoint - temp.get(i).cts) + 0.5);
				
			//	System.out.println("Parsed : " + (int)(chargeRate * (initPoint - temp.get(i).cts) + 0.5) + " , " + (int)(chargeRate * (secondPoint - temp.get(i).cts) + 0.5)  + " original " +  (chargeRate * (initPoint - temp.get(i).cts) + 0.5) + " , " + (chargeRate * (secondPoint - temp.get(i).cts) + 0.5));
				dls = cle;
				dle = cle;
				cts = initPoint;
				cte = secondPoint;
				dts = secondPoint;
				dte = secondPoint;
				hasFirstHalf = true;
				hasSecondHalf = false;
				totalCharge = cle-cls;
				totalDischarge = 0;
				patternType = 2;
				count++;
				break;
			}
			
			//Case 8 : cuts at the end of charging cycle and in between discharging line.
			else if (initPoint == temp.get(i).cte 
					&& secondPoint > temp.get(i).cte && secondPoint < temp.get(i).dte) {
			//	System.out.println("Case 8 " + initPoint);
				cls = temp.get(i).cle;
				cle = cls;
				dls = cle;
				double dischargeRate = getDischargingRate(temp.get(i).dts, temp.get(i).dte, temp.get(i).totalDischarge);
				dle = dls - (int) (dischargeRate * (secondPoint - temp.get(i).dts) +0.5);
				cts = initPoint;
				cte = initPoint;
				dts = initPoint;
				dte = secondPoint;
				hasFirstHalf = false;
				hasSecondHalf = false;
				totalCharge = 0;
				totalDischarge = dls - dle;	
				patternType = 5;
				count++;
				break;

				
				
			}
			
			//Case 9 : cuts in between charging and between discharging lines
			else if (initPoint > temp.get(i).cts && initPoint < temp.get(i).cte
					&& secondPoint > temp.get(i).dts && secondPoint < temp.get(i).dte) {
				//System.out.println("Case 9 " + initPoint);

				double chargeRate = getChargingRate(temp.get(i).cts, temp.get(i).cte, temp.get(i).totalCharge );
				cls = temp.get(i).cls + (int)(chargeRate * (initPoint - temp.get(i).cts) + 0.5);
				cle =  temp.get(i).cle;
				dls = temp.get(i).dls;
				double dischargeRate = getDischargingRate(temp.get(i).dts, temp.get(i).dte, temp.get(i).totalDischarge);
				dle = dls - (int) (dischargeRate * (secondPoint - temp.get(i).dts) + 0.5);
				cts = initPoint;
				cte = temp.get(i).cte;
				dts = temp.get(i).dts;
				dte = secondPoint;
				hasFirstHalf = false;
				hasSecondHalf = true;
				totalCharge = cle-cls;
				totalDischarge = dls - dle;	
				patternType = 3;
				count++;
				break;
				
			}
			
			//Case 10 : cuts in between charging and end of charging
			else if (initPoint > temp.get(i).cts && initPoint < temp.get(i).cte
					&& secondPoint == temp.get(i).cte ) {
			//	System.out.println("Case 10 "+ initPoint);
				double chargeRate = getChargingRate(temp.get(i).cts, temp.get(i).cte, temp.get(i).totalCharge);
				//System.out.println("Charging Rate " + chargeRate + " , " + i + ", init , second = " + initPoint + ", " + secondPoint + " , cts and cte = " + temp.get(i).cts + ", " + temp.get(i).cte + " Total charge " + temp.get(i).totalCharge);
				cls = temp.get(i).cls + (int)(chargeRate * (initPoint - temp.get(i).cts) + 0.5);
				cle =  temp.get(i).cle;
				dls = temp.get(i).dls;
				dle = dls;
				cts = initPoint;
				cte = secondPoint;
				dts = secondPoint;
				dte = secondPoint;
				hasFirstHalf = false;
				hasSecondHalf = true;
				totalCharge = cle-cls;
				totalDischarge = 0;		
				count++;
				patternType = 3;
				break;
				
			}
			
			//Case 11: cuts in between charging and end of discharging.
			else if (initPoint > temp.get(i).cts && initPoint < temp.get(i).cte
					&& secondPoint == temp.get(i).dte ) {
			//	System.out.println("Case 11 " + initPoint);
				double chargeRate = getChargingRate(temp.get(i).cts, temp.get(i).cte, temp.get(i).totalCharge);
			//	System.out.println("Charging Rate " + chargeRate + " , " + i + ", init , second = " + initPoint + ", " + secondPoint + " , cts and cte = " + temp.get(i).cts + ", " + temp.get(i).cte + " Total charge " + temp.get(i).totalCharge);
				cls = temp.get(i).cls + (int)(chargeRate * (initPoint - temp.get(i).cts) + 0.5);
				cle =  temp.get(i).cle;
				dls = temp.get(i).dls;
				dle = temp.get(i).dle;
				cts = initPoint;
				cte = temp.get(i).cte;
				dts = cte;
				dte = temp.get(i).dte;
				hasFirstHalf = false;
				hasSecondHalf = true;
				totalCharge = cle-cls;
				totalDischarge = temp.get(i).totalDischarge;	
				patternType = 3;
				count++;
				break;
				
			}
			
			//Case 12: cuts in between discharging and end of discharging
			else if (initPoint > temp.get(i).cte && initPoint < temp.get(i).dte
					&& secondPoint ==temp.get(i).dte) {
			//	System.out.println("Case 12 " + initPoint);
				double dischargeRate = getDischargingRate(temp.get(i).dts, temp.get(i).dte, temp.get(i).totalDischarge);
				dls = temp.get(i).dls - (int)(dischargeRate * (initPoint - temp.get(i).dts) + 0.5);
				dle = temp.get(i).dle;
				cls = dls;
				cle = dls;
				cts = initPoint;
				cte = initPoint;
				dts = initPoint;
				dte = secondPoint;
				hasFirstHalf = false;
				hasSecondHalf = false;
				totalCharge = 0;
				totalDischarge = dls - dle;
				patternType = 6;
				
				count++;
				break;
				

			}
			
			/*else
			{
				if(temp.get(i).dte > this.startTime && temp.get(i).cts < this.endTime)
				{
					System.out.println("StartTime " + this.startTime + " End time " + this.endTime);
					System.out.println("Why is this not covered ?? " + temp.get(i).cts + ", " + temp.get(i).cte + ", "  + temp.get(i).dts + ", " + temp.get(i).dte  + " Cutting Points : " + initPoint + " , " + secondPoint);
					System.out.println("Why is this not covered ?? " + temp.get(i).cls + ", " + temp.get(i).cle + ", "  + temp.get(i).dls + ", " + temp.get(i).dle );
					
				}
			}
			*/
			
			
				
			}
			//

		if(count > 0)
		{
			
		decisionBlock = new ChargingPattern(cls, cle, dls, dle);
		decisionBlock.cts = cts;
		decisionBlock.cte = cte;
		decisionBlock.dts = dts;
		decisionBlock.dte = dte;
		decisionBlock.patternType = patternType;
		
		decisionBlock.totalCharge = totalCharge;
		decisionBlock.totalDischarge = totalDischarge;
		decisionBlock.hasFirstHalf = hasFirstHalf;
		decisionBlock.hasSecondHalf = hasSecondHalf;
		count = 0;
		return decisionBlock;
		}
			

		
		
		return null;
	}

	public double getDischargingRate(long dts, long dte, int totalDischarge) {

		long totalTime = dte - dts;
		//System.out.println("Total " + totalTime + ", " + totalDischarge);
		double discharge = (double)totalDischarge / (double)totalTime;
		//System.out.println("DisCharging Rate " + discharge);
		return discharge;
	}

	public double getChargingRate(long cts, long cte, int totalCharge) {

		long totalTime = cte - cts;
	//	System.out.println("Total " + totalTime + ", " + totalCharge); 
		double charge = (double)totalCharge / (double)totalTime;
		//System.out.println("Charging Rate " + charge);
		return charge;
	}

	public void createDecisionBlocks() {

	//	System.out.println("-----Creating Decision blocks for " + nodeA.nodeId + " and " + nodeB.nodeId); 
		ArrayList<Long> decisionPoints = new ArrayList<>();
		decisionPoints = updateDecisionPoints();
		

		for (int i = 0; i < decisionPoints.size()-1; i++) {
			long initPoint = decisionPoints.get(i);
			long secondPoint = decisionPoints.get(i+1);

			ChargingPattern patternA = getChargingPattern(nodeA, initPoint, secondPoint);
			ChargingPattern patternB = getChargingPattern(nodeB, initPoint, secondPoint);
			if(patternA!=null && patternB!=null)
			{
			cpA.add(patternA);
			cpB.add(patternB);
			}
			/*else
			{
				System.out.println("NUll is coming for " + patternB.cts);
			}*/
			
			
		
		}
		
		//Add dummy block
		ChargingPattern finalBlock_A = new ChargingPattern(cpA.get(cpA.size()-1));
		finalBlock_A.cls= 0;
		finalBlock_A.cle=0;
		finalBlock_A.dls=0;
		finalBlock_A.dle= 0;
		finalBlock_A.hasFirstHalf = false;
		finalBlock_A.hasSecondHalf = false;
		finalBlock_A.cts=finalBlock_A.dte;
		finalBlock_A.cte=finalBlock_A.dte;
		finalBlock_A.dts=finalBlock_A.dte;
		//finalBlock_A.dte=finalBlock_A.dte;
		cpA.add(finalBlock_A);
		
		ChargingPattern finalBlock_B = new ChargingPattern(cpB.get(cpB.size()-1));
		finalBlock_B.cls=0;
		finalBlock_B.cle=0;
		finalBlock_B.dls=0;
		finalBlock_B.dle=0;
		finalBlock_B.hasFirstHalf = false;
		finalBlock_B.hasSecondHalf = false;
		finalBlock_B.cts=finalBlock_B.dte;
		finalBlock_B.cte=finalBlock_B.dte;
		finalBlock_B.dts=finalBlock_B.dte;
	//	finalBlock_B.dte=finalBlock_B.dte;
		cpB.add(finalBlock_B);
		verifyDecisionPoints();
		
		
	}

	private ArrayList<Long> updateDecisionPoints() {
		// Get all the discharge times, sort and remove duplicates. Gives all
		// the points for decision.
		ArrayList<Long> times = new ArrayList<>();
		times.add(startTime);

		for (int i = 0; i < nodeA.chargingPatterns.size(); i++) {

			if (nodeA.chargingPatterns.get(i).dte >= startTime
					&& nodeA.chargingPatterns.get(i).dte <= endTime)
			{
				
				times.add(nodeA.chargingPatterns.get(i).dte);
			}
		}
		for (int i = 0; i < nodeB.chargingPatterns.size(); i++) {

			if (nodeB.chargingPatterns.get(i).dte >= startTime
					&& nodeB.chargingPatterns.get(i).dte <= endTime)
			{	
			
				 
				times.add(nodeB.chargingPatterns.get(i).dte);
			}
		}
		
		
		times.add(endTime);
		

		// Remove duplicates
		Set<Long> hs = new HashSet<>();
		hs.addAll(times);
		times.clear();
		times.addAll(hs);
		Collections.sort(times);
		return times;
	}
	
	public void verifyDecisionPoints()
	{
		for(int i=0;i<this.cpA.size()-1;i++)
		{
			if(this.cpA.get(i).dte!=this.cpA.get(i+1).cts)
			{
				System.out.println("ERRRORRRRRRRRRR for dp " + i + " dte :? " + this.cpA.get(i).dte + " and " + this.cpA.get(i+1).cts);
				System.exit(1);
			}
		}
	}

}
