package lanr.logic.frequency.windowfunctions;

/**
 * Implementation of the Hamming Window function.
 * 
 * @author Nicolas Bruch
 *
 */
public class HammingWindow extends WindowFunctionImpl {

	private final double[] scalars;

	public HammingWindow(int size) {
		scalars = new double[size];
		for (int n = 0; n < size; n++) {
			scalars[n] = 0.54 + 0.46 * Math.cos(((2.0 * PI) / (size - 1)) * n);
		}

	}
	
	@Override
	public void apply(double[] samples) {
		if (samples.length != scalars.length) {
			throw new IllegalArgumentException(
					"Invalid length. Was " + samples.length + " but should be " + scalars.length);
		}
		for (int i = 0; i < samples.length; i++) {
			samples[i] *= scalars[i];
		}	
	}

}
