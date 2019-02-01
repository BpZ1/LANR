package lanr.logic.frequency.windowfunctions;

/**
 * Based on a version from
 * https://code.google.com/archive/p/spectro-edit/
 */
public class VorbisWindow extends WindowFunctionImpl {

	private final double[] scalars;

	public VorbisWindow(int size) {
		scalars = new double[size];
		for (int i = 0; i < size; i++) {
			double xx = Math.sin((PI / (2.0 * size)) * (2.0 * i));
			scalars[i] = Math.sin((PI / 2.0) * (xx * xx));
		}

	}

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
