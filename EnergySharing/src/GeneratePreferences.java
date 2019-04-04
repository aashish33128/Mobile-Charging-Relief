/**
 * @author Aashish Dhungana
 * Generates Preference list for stable matching based on skip ratio and energy ratio
 */


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneratePreferences {

	

	public List<Nodes> allNodes = new ArrayList<>();
	Result[][] results = new Result[allNodes.size()][allNodes.size()];
	
	
	public void swap(Result[] res, int i, int j)
	{
		Result temp = res[i];
		Result temp1 = res[j];
		res[j] = temp;
		res[i] = temp1;
		
	}
	public Result[] sort(Result[] res, int a)
	{
		for(int i=0; i<res.length-2;i++)
		{
			for(int j=i; j<res.length-2;j++)
			{
				if(res[i]==null)
				{
					swap(res,i,res.length-1);
				}
				if(res[j]==null)
				{
					swap(res,j,res.length-1);
				}
				else if(res[i+1] == null)
				{
					swap(res,i+1,res.length-1);
				}
				
				double skipRatio_1 = res[i].getSkipRatio();
				double skipRatio_2 = res[j].getSkipRatio();

				double energyRatio_1 = res[i].getEnergyRatio();
				
				double energyRatio_2 = res[j].getEnergyRatio();
				
				if(skipRatio_1 < skipRatio_2)
				{
					swap(res,i,j);
				}
				else if(skipRatio_1 == skipRatio_2)
				{
					if(energyRatio_1 < energyRatio_2)
					{
						swap(res,i,j);
					}
					
				}
			
			}
			
		}
		return res;
	}


	
	public GeneratePreferences(List<Nodes> allNodes, Result[][] results,double tS) {
		this.allNodes = allNodes;
		
		this.results = results;
		//System.out.println("dadasdasd" +this.results[1][61].totalOriginalSkips);
		generatePreferences();
		writePreferencesToFile("preferences" +tS+".csv");
		//this.results = null;
	}

	/**
	 * Uses the comparable interface to sort the results on basis of skip ratio
	 * and energy ratio in case of ties.
	 */
	public void generatePreferences() {

		System.out.println("Generating Preferences");
		for (int i = 0; i < this.allNodes.size(); i++) {
			
			Result[] res = new Result[this.allNodes.size()];
			res = this.results[i];		
			Result[] sortedList = sort(res,i);
			this.allNodes.get(i).setPreferenceList(sortedList);
		}

	}


	
	public void writePreferencesToFile(String filename)
	{
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write("Preferences");
			writer.newLine();
			
			for(int i=0;i<this.allNodes.size();i++)
			{
				StringBuilder content = new StringBuilder();
				for(int j=0; j< this.allNodes.get(i).preferenceList.size();j++)
				{
				content .append(this.allNodes.get(i).preferenceList.get(j).index + " ,");
				}
				writer.write(String.valueOf(content.substring(0,content.length()-1)));
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
