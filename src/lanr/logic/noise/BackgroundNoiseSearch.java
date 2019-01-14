package lanr.logic.noise;

import java.util.LinkedList;
import java.util.List;

import lanr.logic.FrequencyAnalyzer;
import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

/**
 * Search for background noise.<br>
 * The search does the following:<br>
 * Save all values as long as a window contains a dBFS value over the given
 * threshold.
 * 
 * @author Nicolas Bruch
 *
 */
public class BackgroundNoiseSearch extends FrequencyAnalyzer {

	private static double noiseThreshold = 100;
	/**
	 * Minimum dBFS value.
	 */
	private final static double DECIBEL_BOUND = -40;
	/**
	 * Lower frequency bound.
	 */
	private final static double FREQUENCY_BOUND = 400;
	
	private final int duration = sampleRate / 4;

	private List<Double> frequencies = new LinkedList<Double>();

	/**
	 * Location on which the current noise was found.
	 */
	private long foundLocation = 0;
	private long locationCounter = 0;
	private List<Noise> foundNoise = new LinkedList<Noise>();
	/**
	 * dBFS values that meet the requirements of background noise.
	 */
	private List<Double> frequencyDbValues = new LinkedList<Double>();

	public BackgroundNoiseSearch(int sampleRate, int windowSize) {
		super(sampleRate, windowSize);
		// Calculate the frequencies of the input bins
		for (int i = 0; i < (windowSize / 2) + 1; i++) {
			frequencies.add(calculateFrequency(i));
		}
	}
	
	@Override
	public void search(double[] frequencySamples) {
		boolean windowContainsNoise = false;
		for (int i = 0; i < frequencySamples.length; i++) {
			// Check if there are frequencies that are above the threshold
			if (frequencySamples[i] > DECIBEL_BOUND && frequencies.get(i) > FREQUENCY_BOUND) {
				// Save the beginning location if this is the start of the noise
				if (foundLocation == 0) {
					foundLocation = locationCounter;
				}
				frequencyDbValues.add(frequencySamples[i]);
				windowContainsNoise = true;
			}
		}
		// If no noise was found in this window, add the previously found noise
		if (!windowContainsNoise && !frequencyDbValues.isEmpty()) {
			double noiseLevel = average(frequencyDbValues);
			System.out.println("Number: " + frequencyDbValues.size() + " Value: " + noiseLevel);
			frequencyDbValues.clear();
			// Only add the noise if it is above a certain level
			if (noiseLevel > noiseThreshold) {
				long length = locationCounter - foundLocation + windowSize;
				if(length >= duration) {
					foundNoise.add(new Noise(NoiseType.Background, foundLocation, length, noiseLevel));					
				}
			}
			foundLocation = 0;
		}
		locationCounter += windowSize;
	}

	/**
	 * Calculates an average value for the given values.<br>
	 * The value is calculated like this:<br>
	 * Add the values to the positive lower decibel bound
	 * to convert them to positive values.<br>
	 * Take them to the power of 3 to increase the gap
	 * between big and small numbers.<br>
	 * Multiply them by 0.25 to get them smaller.<br>
	 * Sum all values and divide through the number of values.
	 * @param values - dBFS values.
	 * @return Specially calculated average value.
	 */
	private double average(List<Double> values) {
		double sum = 0.0;
		for (double d : values) {
			double value = Math.pow(40 + d, 3) * 0.25;
			sum += value;
		}
		return sum / values.size();
	}

	@Override
	public List<Noise> getNoise() {
		return foundNoise;
	}

	@Override
	public void compact() {
		double noiseLevel = average(frequencyDbValues);
		frequencyDbValues.clear();
		if (noiseLevel > noiseThreshold) {
			foundNoise.add(new Noise(NoiseType.Background, foundLocation, locationCounter - foundLocation, noiseLevel));
		}
		foundNoise = combineNoises(foundNoise, 0, sampleRate * 3);
	}

}
