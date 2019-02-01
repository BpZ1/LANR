package lanr.logic.frequency.windowfunctions;

import net.sourceforge.jdistlib.math.Bessel;

public class KaiserWindow extends WindowFunctionImpl {

	private final double[] scalars;
	private final int length;
	private final double alpha = 5.0;
	
	public KaiserWindow(int windowSize) {
		this.length = windowSize;
		this.scalars = new double[length];
		for(int i = 0; i < length; i++) {
			double z = ((2 * i) / length) - 1;
			double numerator = Bessel.i(
					Math.PI * alpha * Math.sqrt(1 - Math.pow(z, 2)),
					0, false);
			double denominator = Bessel.i(Math.PI * alpha, 0, false);
			scalars[i] = numerator / denominator;
		}
	}

	@Override
	public void apply(double[] samples) {
		if (samples.length != scalars.length) {
			throw new IllegalArgumentException(
					"Invalid length. Was " + samples.length + " but should be " + scalars.length);
		}
		for(int i = 0; i < samples.length; i++) {
			samples[i] *= scalars[i];
		}
	}
	
	
}
