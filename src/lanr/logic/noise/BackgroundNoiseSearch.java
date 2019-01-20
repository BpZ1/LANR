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

	private static final int maxSkip = 1;
	/**
	 * Minimum dBFS value.
	 */
	private final static double DECIBEL_BOUND = -30;

	/**
	 * Lower frequency bound.
	 */
	private final static double FREQUENCY_BOUND = 400;

	private List<Noise> foundNoise = new LinkedList<Noise>();

	public BackgroundNoiseSearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain, mirrored, 0.25,
				FREQUENCY_BOUND, Double.POSITIVE_INFINITY, DECIBEL_BOUND, maxSkip);
	}
	
	@Override
	public void search(double[] frequencySamples) {
		getNoise(frequencySamples);
	}
	
	@Override
	protected Noise createNoise(long location, long length) {
		Noise noise = new Noise(NoiseType.Background, location, length, lowerFreqBound);
		return noise;
	}
	

	/**
	 * Calculates an average value for the given values.<br>
	 * The value is calculated like this:<br>
	 * Add the values to the positive lower decibel bound
	 * to convert them to positive values.<br>
	 * Take them to the power of 3 to increase the gap
	 * between big and small numbers.<br>
	 * Sum all values and divide through the number of values.
	 * @param values - dBFS values.
	 * @return Specially calculated average value.
	 */
	private double average(List<Double> values) {
		if(values.isEmpty()) {
			return 0.0;
		}
		double sum = 0.0;
		for (double d : values) {
			double value = Math.pow(d, 2);
			sum += value;
		}
		sum /= values.size();
		return Math.sqrt(sum);
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

	
}
