package deepnets;

import java.util.*;

public class DataPoint {
	private final double[] input;
	private final double[] output;
	
	public DataPoint(double[] input, double[] output) {
		this.input = input;
		this.output = output;
	}

	public double[] getInputs() {
		return input;
	}

	public double[] getOutputs() {
		return output;
	}
	
	public static Collection<DataPoint> createData(double[][] inputs, double[][] outputs) {
		int n = Math.min(inputs.length, outputs.length);
		Collection<DataPoint> result = new ArrayList<DataPoint>();
		for (int i = 0; i < n; i++) {
			result.add(new DataPoint(inputs[i], outputs[i]));
		}
		return result;
	}

	public static DataPoint create(double[] inputActivations, double[] outputTargets) {
		return new DataPoint(inputActivations.clone(), outputTargets.clone());
	}

}
