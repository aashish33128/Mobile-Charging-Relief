/**
 * @author: Aashish Dhungana
 * Algorithm for selfish skips
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SelfishAlgorithm {
	
	
	int c = 100;	
	int[][] D ;
	int[][] track;
	Nodes node;
	int initialCharge = 0;
	int numOfPoints = 0;
	double lossRate_Beta = 0;
	long startTime, endTime;
	ArrayList<ChargingPattern> cps = new ArrayList<>();
	
	
	public ArrayList<Long> times = new ArrayList<>();
	public ArrayList<Long> setDecisionTimes()
	{
		ArrayList<ChargingPattern> cpTemp = new ArrayList<>();
		cpTemp = this.node.chargingPatterns;
		times.add(startTime);
		for(int i=0;i<cpTemp.size();i++)
		{
			if(cpTemp.get(i).dte <= endTime)
			{
				
				//System.out.println("ADDING SADSADSAD");
			times.add(cpTemp.get(i).dte);
			}
		}
		
		times.add(endTime);
		// Remove duplicates
				Set<Long> hs = new HashSet<>();
				hs.addAll(times);
				times.clear();
				times.addAll(hs);
				Collections.sort(times);
				System.out.println("asdasdas " + times.size());
				return times;
		
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
			decisionPoints = setDecisionTimes();
			//System.out.println("SIZSDSDS " + decisionPoints.size());
			/*System.out.println("Num of decision points " + decisionPoints.size());
			System.out.println("Decision Points are " );*/
			
			/*for(int i=0; i<decisionPoints.size();i++)
			{
				System.out.print(decisionPoints.get(i) + ", ");
			}*/

			for (int i = 0; i < decisionPoints.size()-1; i++) {
				long initPoint = decisionPoints.get(i);
				long secondPoint = decisionPoints.get(i+1);

				ChargingPattern patternA = getChargingPattern(node, initPoint, secondPoint);
				//ChargingPattern patternB = getChargingPattern(nodeB, initPoint, secondPoint);
				if(patternA!=null)
				{
				this.cps.add(patternA);
				
				}
				
				
				
			
			}
			
			//Add dummy block
			ChargingPattern finalBlock_A = new ChargingPattern(cps.get(cps.size()-1));
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
			cps.add(finalBlock_A);
			
		
			//verifyDecisionPoints();
			
			
		}
	
	public SelfishAlgorithm(Nodes source,double lossRate_Beta,long startTime,long endTime)
	{
		this.node = source;
		this.startTime = startTime;
		this.endTime = endTime;
		createDecisionBlocks();
		
	
		this.lossRate_Beta = lossRate_Beta;
		this.initialCharge = cps.get(0).cls;
		this.numOfPoints = this.cps.size();
		this.D = new int[numOfPoints][101];
		this.track = new int[numOfPoints][101];
	}
	
	
	public void runAlgorithm()
	{
		init();
		run();
		trackSkip(this.D,this.track);
	}
	
	/*public static void main(String[] args)
	{
		SelfishAlgorithm object = new SelfishAlgorithm();
		object.init();
		object.run();
		object.trackSkip(object.D, object.track);
	}*/
	
	
	/**
	 * Initialize the D Matrix
	 */
	public void init()
	{
		for(int i=0; i<numOfPoints; i++)
		{
			for(int j=0; j<=c; j++)
			{
				
					D[i][j] = 500;
					track[i][j] = 500;
				
			}
		}
		
		D[0][initialCharge] = 0;
		
	}
	

	public void run()
	{
		for(int i=0; i<numOfPoints-1;i++)
		{
			for(int j=0; j<=c;j++)
			{
				
				if(D[i][j]!=500)
				{
				int residualChargeSkip = j - this.cps.get(i).totalDischarge - (int)(j*lossRate_Beta);
				//System.out.println("Residual Energy From Friend at i = " + i + " and charge = " + j + " = " + residualChargeFromFriend);
				
				int residualChargeFromWall= Math.min(100, j +this.cps.get(i).totalCharge ) - this.cps.get(i).totalDischarge;
				//System.out.println("Residual Energy From Wall at i = " + i + " and charge = " + j + " = " + residualChargeFromWall);
				
				if(residualChargeSkip >= 0 )
				{
					int originalValue = D[i+1][residualChargeSkip];
					/*System.out.println("Original Value " + originalValue);
					System.out.println("Value to be Updated : " + D[i][j]);*/
					
					if(D[i][j] <  originalValue)
					{
						D[i+1][residualChargeSkip] = D[i][j];
						track[i+1][residualChargeSkip] = j;						
						
					}
					
					
				}
				
				if(residualChargeFromWall >= 0 )
				{
					int originalValue = D[i+1][residualChargeFromWall];
					/*System.out.println("Original Value " + originalValue);
					System.out.println("Value to be Updated : " + D[i][j] + 1);*/
					
					if(D[i][j] + 1 <  originalValue)
					{
						D[i+1][residualChargeFromWall] = D[i][j] + 1;
						track[i+1][residualChargeFromWall] = j;
						
					}
					
					
				}
				}
			}
		}
		
		System.out.println("Algorithm Finished. Printing Matrices");
		
		System.out.println("Printing Updated charging Pattern");
	
	}
	
	public void trackSkip(int[][] D, int[][] t)
	{
		
	
		int value = 500;
		int index = -1;
		int[] skip = new int[numOfPoints];
		for(int a = 0; a<=c; a++)
		{
			
			if(D[numOfPoints-1][a] < value)
			{
				value = D[numOfPoints-1][a];
				index = a;
			}
			
			
		}
		
		//System.out.println("Index 6 " + index);
		index = t[numOfPoints-1][index];
		
		//System.out.print("Index 5 " + index);
		//int numberOfCharges = value;
		skip[numOfPoints-1] = 1;

		for(int i=numOfPoints-2;i>=0;i--)
		{
			
			//System.out.println("Time, Charge and Value : " + (i) + "," + index +  "&&" + D[i][index]);
			// numberOfCharges = D[i][index];
			if(value==D[i][index])
			{
				skip[i]=1;
			}
			else if(value - D[i][index] == 1)
			{
				skip[i] = 0;
			}
			else
			{
				System.out.println("Error");
				System.exit(1);
			}
			value = D[i][index];
			index = t[i][index];
			/*
			if(i!=0)
			{
				System.out.println("Index " + (i-1) + " : " + index );
			}*/
			
		}
		
		System.out.println("Selfish Algorithm completed");
		SelfishResult res = new SelfishResult(this.node, skip);
		res.setOriginalSkips(cps, skip);
		this.node.selfishRes = res;
		
		
		System.out.println("--------------SKIP SEQUENCE for node " + this.node.index + " and size " + this.node.chargingPatterns.size()+" -------");
		for(int i = 0; i<res.originalSkips.size();i++)
		{
			System.out.print(res.originalSkips.get(i) + ",");
		}
		
	}
	


}
