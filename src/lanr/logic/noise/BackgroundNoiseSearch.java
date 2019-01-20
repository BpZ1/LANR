package lanr.logic.noise;

import java.util.LinkedList;
import java.util.List;

import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

/**
 * Search for background noise.<br>
 * The search does the following:<br>
 * Background noise is defined as noise in the range of
 * 400 hZ or more over a certain threshold of dBFS.<br>
 * 
 * @author Nicolas Bruch
 *
 */
public class BackgroundNoiseSearch extends FrequencySearch {

	private static double severityWeight = 10;
	private double severityRelativeWeight;
	private static final double duration = 1 / 4;
	private static final int maxSkip = 4;
	/**
	 * Minimum dBFS value.
	 */
	private final static double DECIBEL_BOUND = -50;

	/**
	 * Lower frequency bound.
	 */
	private final static double FREQUENCY_BOUND = 400;

	private List<Noise> foundNoise = new LinkedList<Noise>();

	public BackgroundNoiseSearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain, mirrored, duration,
				FREQUENCY_BOUND, Double.POSITIVE_INFINITY, DECIBEL_BOUND, maxSkip);
		severityRelativeWeight = severityWeight / sampleRate;
	}
	
	@Override
	public void search(double[] frequencySamples) {
		getNoise(frequencySamples);
	}
	
	@Override
	protected Noise createNoise(long location, long length) {
		Noise noise = new Noise(NoiseType.Background, location, length, severityRelativeWeight);
		return noise;
	}	

	@Override
	public List<Noise> getNoise() {
		return foundNoise;
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
		BackgroundNoiseSearch.severityWeight = severityWeight;
	}
}
