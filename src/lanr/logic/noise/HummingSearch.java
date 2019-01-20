package lanr.logic.noise;

import java.util.List;

import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

public class HummingSearch extends FrequencySearch {
	
	/**
	 * Severity per second of noise
	 */
	private static final double SEVERITY_WEIGHT = 1;
	private static final double TOLERANCE = 1;
	/**
	 * Threshold for the decibel value of the humming.
	 */
	private static final double DECIBEL_BOUND_VALUE = -50;
	/**
	 * We are only interested in low frequency humming under 400 Hz.
	 */
	private static final double FREQUENCY_BOUND_VALUE = 400;
	/**
	 * Number of windows that can be interrupted and still count
	 */
	private static int maxSkip = 4;
	/**
	 * Number of seconds for which the humming must persist.
	 */
	private static double duration = 3;
	/**
	 * Value how much one window counts in severity.
	 */
	double severityValue = SEVERITY_WEIGHT * (1 / windowSize);

	public HummingSearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain, mirrored, duration,
				Double.NEGATIVE_INFINITY, FREQUENCY_BOUND_VALUE, DECIBEL_BOUND_VALUE, maxSkip);
	}
	
	@Override
	public void search(double[] samples) {
		getValues(samples);
	}

	@Override
	protected Noise createNoise(long location, long length) {
		Noise noise = new Noise(NoiseType.Hum, location, length, 0);
		return noise;
	}

	@Override
	public List<Noise> getNoise() {
		return foundNoise;
	}
}
