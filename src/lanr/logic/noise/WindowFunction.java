package lanr.logic.noise;

import lanr.logic.frequency.FrequencyConversion;

public class WindowFunction {

	private final double[] scalars;

	private static final double PI = Math.PI;

	public WindowFunction(int size, FrequencyConversion conversion) {
		scalars = new double[size];
		for (int i = 0; i < size; i++) {
			double xx = Math.sin((PI / (2.0 * size)) * (2.0 * i));
			scalars[i] = Math.sin((PI / 2.0) * (xx * xx));
		}

	}

	public void apply(double[] data) {
		if (data.length != scalars.length) {
			throw new IllegalArgumentException(
					"Invalid length. Was " + data.length + " but should be " + scalars.length);
		}
		for (int i = 0; i < data.length; i++) {
			data[i] *= scalars[i];
		}
	}
}
