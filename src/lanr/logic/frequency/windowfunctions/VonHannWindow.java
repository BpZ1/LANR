package lanr.logic.frequency.windowfunctions;

/**
 * 
 * Implementation of the Von-Hann Window function.
 * 
 * @author Nicolas Bruch
 *
 */
public class VonHannWindow extends WindowFunction {	
	
	private final double[] scalars;
	
	public VonHannWindow(int windowSize) {
		scalars = new double[windowSize];
		for (int n = 0; n < windowSize; n++) {
			scalars[n] = 0.5 * (1.0 - Math.cos((2.0 * PI * n) / (double)(windowSize - 1)));
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
