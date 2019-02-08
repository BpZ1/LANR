package lanr.logic.noise;

import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

/**
 * Searches for humming in the low frequencies.
 * 
 * @author Nicolas Bruch
 *
 */
public class HummingSearch extends FrequencyDurationSearch {
	
	/**
	 * Severity per second of noise
	 */
	private static double severityWeight = 10;
	private double severityRelativeWeight;
	/**
	 * Threshold for the decibel value of the humming.
	 */
	private static final double DECIBEL_BOUND = -50;
	/**
	 * We are only interested in low frequency humming under 400 Hz.
	 */
	private static final double FREQUENCY_BOUND_VALUE = 400;
	/**
	 * Number of windows that can be interrupted and still count
	 */
	private static int maxSkip = 4000;
	/**
	 * Number of seconds for which the humming must persist.
	 */
	private static double duration = 3;

	public HummingSearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain, mirrored, duration,
				Double.NEGATIVE_INFINITY, FREQUENCY_BOUND_VALUE, DECIBEL_BOUND, maxSkip / windowSize);
		severityRelativeWeight = severityWeight / sampleRate;
	}

	@Override
	protected Noise createNoise(long location, long length) {
		Noise noise = new Noise(NoiseType.Hum, location, length, severityRelativeWeight);
		return noise;
	}

	@Override
	protected double calculateSeverity(double dBFSValue) {
		double positiveThreshold = DECIBEL_BOUND * -1;	
		return positiveThreshold + dBFSValue;
	}
	
	public static double getSeverityWeight() {
		return severityWeight;
	}

	public static void setSeverityWeight(double severityWeight) {
		HummingSearch.severityWeight = severityWeight;
	}
}
