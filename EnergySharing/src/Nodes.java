import java.util.ArrayList;

public class Nodes {
	public int index;
	public String nodeId;
	public ArrayList<ChargingPattern> chargingPatterns = new ArrayList<>();
	public ArrayList<Nodes> preferenceList = new ArrayList<>();
	public Nodes pairedNode ;
	ArrayList<ChargingPattern> decisionBlocks = new ArrayList<>();
/*	boolean hasAcceptedSentProposal = false;*/
	Nodes acceptedProposal = null;
	ArrayList<Nodes> proposedSet = new ArrayList<>();
	int currentProposal = 0;
	int totalCycles = 0;
	public int totalEnergy;
	long startTime = 0 ;
	long endTime = 0;
	SelfishResult selfishRes ;
	public Nodes()
	{
		
	}
	public Nodes(long startTime,long endTime)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		//setTotalEnergy();
		//getTotalCycles();
		//getTotalEnergy();
	}

	public int getTotalEnergy()
	{
		return this.totalEnergy;
	}
	public void setTotalEnergy()
	{
		for(int i=0;i<this.chargingPatterns.size();i++)
		{
			this.totalEnergy = this.totalEnergy + this.chargingPatterns.get(i).totalCharge;
		}
	}

	public void setTotalCycles(int totalCycles) {
		this.totalCycles = totalCycles;
	}

	public void setPreferenceList(Result[] orderedResults)
	{
		for(int i=0; i<orderedResults.length-1;i++)
		{
			if(orderedResults[i]!=null)
			{
			this.preferenceList.add(orderedResults[i].destNode);
			}
		}
	}
	
	public Nodes hasMoreToPropose()
	{
		Nodes receiver = null;
		if(currentProposal < this.preferenceList.size()-1)
		{
			 receiver =  this.preferenceList.get(currentProposal);
			currentProposal++;
		}
		
		return receiver;
	}
	
	public boolean proposeTo(Nodes receiver)
	{
		boolean accepted = receiver.checkAcceptance(this);
		if(accepted)
		{
			return true;
		}
		else
		{
			this.preferenceList.remove(receiver);
			return false;
		}
	}
	
	public boolean checkAcceptance(Nodes sender)
	{
		if(this.acceptedProposal!=null)
		{
			int currentRank = this.preferenceList.indexOf(this.acceptedProposal);
			if(this.preferenceList.contains(sender))
			{
				int newRank = this.preferenceList.indexOf(sender);
				if(newRank < currentRank)
				{
					this.acceptedProposal = sender;
					return true;
				}
				else
				{
					this.preferenceList.remove(sender);
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else if(this.preferenceList.contains(sender))
		{
			this.acceptedProposal = sender;
			return true;
		}
		else
		{
			return false;
		}
	}
//	public Result result;

}
