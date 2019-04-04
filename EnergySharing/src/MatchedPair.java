/**
 * 
 * @author Aashish Dhungana
 *
 */

public class MatchedPair {
	
	int indexA; 
	int indexB;
	Result resultA = new Result() ;
	Result resultB = new Result();
	public MatchedPair(int indexA,int indexB, Result resultA,Result resultB)
	{
		this.indexA = indexA;
		this.indexB = indexB;
		this.resultA = resultA;
		this.resultB = resultB;
	}

}
