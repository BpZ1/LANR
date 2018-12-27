package lanr.logic.frequency;

public interface SampleConverter {

	/**
	 * Converts the samples into frequency domain.
	 * 
	 * @param samples
	 * @return Frequency magnitudes were <br>
	 * Frequency:<br>
	 * i * fs / N <br>
	 * N = Number of samples <br>
	 * fs = sample frequency <br>
	 * i = index of result
	 */
	public double[] convert(double[] samples);
}
