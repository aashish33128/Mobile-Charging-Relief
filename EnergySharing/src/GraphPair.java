import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GraphPair {

	ArrayList<Double> xAxis = new ArrayList<>();
	ArrayList<Double> yAxis = new ArrayList<>();

	public GraphPair(ArrayList<Double> xAxis, ArrayList<Double> yAxis) {
		this.xAxis = xAxis;
		this.yAxis = yAxis;

		
	}

	public void plot(String filename) throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter(filename + ".csv"));
		// content.write("Time,Value");
		write.write("Time,Value");
		write.newLine();
		StringBuilder content = null;
		for (int i = 0; i < xAxis.size(); i++) {
			content = new StringBuilder();
			content.append(String.valueOf(xAxis.get(i)) + ", " + String.valueOf(yAxis.get(i)));
			write.write(content.toString());
			write.newLine();

		}

		write.close();

	}

	public void plotPartial(String filename, String content, String header) throws IOException {
		BufferedWriter write = new BufferedWriter(new FileWriter(filename + ".csv"));
		// content.write("Time,Value");
		write.write(header+ " ,Value");
		write.newLine();
		write.write(content.toString());
		write.newLine();
		write.close();

	}

}
