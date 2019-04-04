import java.util.ArrayList;

public class Result {

	public int[] skipSequence_A;
	public ArrayList<Integer> originalSkips_Source = new ArrayList<>();
	public ArrayList<Integer> originalSkips_Dest = new ArrayList<>();
	public int[] energySequence_A;
	public int[] energySequence_B;
	public int totalOriginalSkips = 0;
	public int totalReceivedEnergy = 0;
	public int totalReceivedEnergy_Dest = 0;
	public int totalSourceSkips = 0;
	public int totalSourceCycles = 0;
	public int totalsourceEnergy = 0;
	public int totalDestSkips = 0;
	public Nodes sourceNode;
	public Nodes destNode;
	ArrayList<Integer> originalEnergySource = new ArrayList<>();
	ArrayList<ChargingPattern> dpSource = new ArrayList<>();
	MeetingTime meet;


	
	public Result(Result res)
	{
		this.skipSequence_A = res.skipSequence_A;
		this.energySequence_A = res.energySequence_A;
		this.energySequence_B = res.energySequence_B;
		this.originalSkips_Source = res.originalSkips_Source;
		this.originalSkips_Dest = res.originalSkips_Dest;
		this.totalOriginalSkips = res.totalOriginalSkips;
		
		this.totalReceivedEnergy = res.totalReceivedEnergy;
		this.totalSourceSkips = res.totalSourceSkips;
		this.totalSourceCycles = res.totalSourceCycles;
		this.totalsourceEnergy = res.totalsourceEnergy;
		this.sourceNode = res.sourceNode;
		this.destNode = res.destNode;
		this.originalEnergySource = res.originalEnergySource;
		this.totalDestSkips = res.totalDestSkips;
	}
	public Result(int totalSize) {

		skipSequence_A = new int[totalSize];
		
		energySequence_A = new int[totalSize];
		
		// setValues();

	}
	
	public Result()
	{
		
	}

	public double getSkipRatio() {
		int skip_1 = this.totalSourceSkips - this.sourceNode.selfishRes.totalSkips;
		int skip_2 = this.totalDestSkips - this.destNode.selfishRes.totalSkips;

		double skipRatio_1 = (double) skip_1 / (double) this.totalSourceCycles;
		double skipRatio_2 = (double)this.totalDestSkips/(double)this.destNode.totalCycles;
		
		//double skipRatio = (double)(this.totalSourceSkips + this.totalDestSkips)/(double)(this.totalSourceCycles+this.destNode.totalCycles);
		
	//	double skipRatio = (this.totalSourceSkips - this.sourceNode.selfishRes.totalSkips) + (this.totalDestSkips - this.destNode.selfishRes.totalSkips);
		//System.out.println("total source skips :  " + skip_1 +  " , Total source cycles " +  this.totalSourceCycles + ", Skip Ratio " + skipRatio_1);

		return skipRatio_1 + skipRatio_2;
		//return skipRatio/(this.totalSourceCycles+ this.destNode.totalCycles);

	}

	public double getEnergyRatio() {
	/*	int energy_1 = this.totalReceivedEnergy;
		
		double energyRatio_1 = (double) energy_1 / (double) this.totalsourceEnergy;
		//System.out.println("Total received Energy " + energy_1 + " , Total source Energy " + this.totalsourceEnergy + " Energy Ratio " + energyRatio_1);

		return energyRatio_1;*/
		
		int energy_1 = this.totalReceivedEnergy;
		int energy_2 = this.totalReceivedEnergy_Dest;
		
		double energyRatio_1 = energy_1 / (double) this.totalsourceEnergy;
		double energyRatio_2 = energy_2/(double) this.destNode.totalEnergy;

/*		double skipRatio_1 = (double) skip_1 / (double) this.totalSourceCycles;
		double skipRatio_2 = (double)this.totalDestSkips/(double)this.destNode.totalCycles;*/
		
		double energyRatio = (this.totalReceivedEnergy)/(this.totalsourceEnergy);
		//System.out.println("total source skips :  " + skip_1 +  " , Total source cycles " +  this.totalSourceCycles + ", Skip Ratio " + skipRatio_1);

		//return skipRatio_1+skipRatio_2;
		//return skipRatio;
		return energyRatio_1 + energyRatio_2;
	}

	public void setValues() {
		this.totalSourceCycles = this.sourceNode.totalCycles;
		this.totalsourceEnergy = this.sourceNode.totalEnergy;
		setSkips();
		setEnergy();
	}

	public void setSkips() {
		int totalSourceSkips = 0;
		int totalDestSkips = 0;

		for (int i = 0; i < this.originalSkips_Source.size(); i++) {
			//System.out.print(this.originalSkips_Source.get(i) + ",");
			totalSourceSkips += this.originalSkips_Source.get(i);
		}
		for (int i = 0; i < this.originalSkips_Dest.size(); i++) {
			//System.out.print(this.originalSkips_Source.get(i) + ",");
			totalDestSkips += this.originalSkips_Dest.get(i);
		}
		this.totalSourceSkips = totalSourceSkips;
		this.totalDestSkips = totalDestSkips;

	}

	public void setEnergy() {
		int totalEnergy_A = 0;
		int totalEnergy_B = 0;

		for (int i = 0; i < this.energySequence_A.length - 1; i++) {
			
			//System.out.println(this.energySequence_A[i]);
			totalEnergy_A += this.energySequence_A[i];
		}
		
		this.totalReceivedEnergy = totalEnergy_A;
		
for (int i = 0; i < this.energySequence_B.length - 1; i++) {
			
			//System.out.println(this.energySequence_A[i]);
			totalEnergy_B += this.energySequence_B[i];
		}
		//System.out.println("Total Energy " + totalEnergy);
		this.totalReceivedEnergy_Dest = totalEnergy_B;

	}

}
