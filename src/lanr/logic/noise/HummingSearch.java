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
	
	private static int threshold = 0;
	private static float length = 3;
	
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

	public HummingSearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain, mirrored, length,
				Double.NEGATIVE_INFINITY, FREQUENCY_BOUND_VALUE,
				DECIBEL_BOUND + threshold, maxSkip / windowSize);
	}

	@Override
	protected Noise createNoise(long location, long length) {
		Noise noise = new Noise(NoiseType.Hum, location, length);
		return noise;
	}

	@Override
	protected double calculateSeverity(double dBFSValue) {
		double positiveThreshold = DECIBEL_BOUND * -1;	
		return positiveThreshold + dBFSValue;
	}
	
	public static void setLength(float value) {
		length = value;
	}
	
	public static void setThreshold(int value) {
		threshold = value;
	}
}
