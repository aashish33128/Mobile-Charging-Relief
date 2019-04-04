public class ChargingPattern {

	long cts, cte, dts, dte;
	int cls, cle, dls, dle;
	int totalCharge = 0;
	int totalDischarge = 0;
	int initialPoint = 0;
	boolean hasFirstHalf = true;
	boolean hasSecondHalf = true;
	
	//0 : Full charging block
	//1: Full charging with half discharging
	//2: Partial Charging (First Half)
	//3 : Partial Charging (Second Half)
	//4: Full discharging
	//5:half discharging (1st part)
	//6: half discharging (2nd part)
	int patternType  = -1; // 1 for full charging block : 0 for only discharging full
	boolean valid;

	
	public ChargingPattern(ChargingPattern p)
	{
		this.cls = p.cls;
		this.cle = p.cle;
		this.dls = p.dls;
		this.dle = p.dle;
		this.cts = p.cts;
		this.cte = p.cte;
		this.dts = p.dts;
		this.dte = p.dte;
		setIsValid();
	}
	public ChargingPattern(int cls, int cle, int dls, int dle) {
		super();
		this.cls = cls;
		this.cle = cle;
		this.dls = dls;
		this.dle = dle;
		setIsValid();
	}
	
	public void setIsValid()
	{
		if(patternType == 1)
		{
			this.valid =true;
		}
	}

	public ChargingPattern() {
	}

}
