package lanr.logic.frequency.windowfunctions;

public class VonHannWindow extends WindowFunction {	
	
	private final double[] scalars;
	
	public VonHannWindow(int windowSize) {
		scalars = new double[windowSize];
		for (int i = 0; i < windowSize; i++) {
			scalars[i] = 0.5 * (1.0 - Math.cos((2.0 * PI * i) / (double)(windowSize - 1)));
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
