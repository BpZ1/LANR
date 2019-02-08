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
	private static final double duration = 1;
	/**
	 * Minimum dBFS value.
	 */
	private final static double DECIBEL_BOUND = -40;

	/**
	 * Lower frequency bound.
	 */
	private final static double FREQUENCY_BOUND = 800;

	public BackgroundNoiseSearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain, mirrored);
		severityRelativeWeight = severityWeight / sampleRate;
		for (int i = 0; i < windowSize; i++) {
			frequencies.add(calculateFrequency(i));
		}
	}

	public static double getSeverityWeight() {
		return severityWeight;
	}

	public static void setSeverityWeight(double severityWeight) {
		BackgroundNoiseSearch.severityWeight = severityWeight;
	}

	@Override
	public void search(double[] samples) {
		for(int i = 0; i < samples.length; i++) {
			if(frequencies.get(i) >= FREQUENCY_BOUND && samples[i] >= DECIBEL_BOUND) {
				System.out.println("Frequency: " + frequencies.get(i) + " Hz ---> " + samples[i]);
			}
		}
		
	}

	@Override
	public List<Noise> getNoise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void compact() {
		// TODO Auto-generated method stub
		
	}
}
