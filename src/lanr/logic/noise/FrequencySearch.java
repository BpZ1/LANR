package lanr.logic.noise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lanr.logic.model.Noise;

/**
 * Base class for the analysis of frequency.
 * The frequencies are analyzed separately and combined at the end.
 * 
 * @author Nicolas Bruch
 *
 */
public abstract class FrequencySearch extends NoiseSearch {

	/**
	 * List containing all frequencies that are represented by the bins.
	 */
	protected final List<Double> frequencies = new LinkedList<Double>();
	/**
	 * dBFS values that meet the requirements of background noise.
	 */
	protected final List<List<Double>> frequencyDbValues = new LinkedList<List<Double>>();
	/**
	 * Number of currently skipped windows.
	 */
	protected final int[] skipCounter;	
	/**
	 * Lower limit of the desired frequency range.
	 */
	protected final double lowerFreqBound;
	/**
	 * Upper limit of the desired frequency range.
	 */
	protected final double upperFreqBound;
	/**
	 * dBFS value over which the signal has to be to be recognized.
	 */
	protected final double threshold;
	/**
	 * Duration the signal has to be in samples.
	 */
	protected final int duration;
	/**
	 * Actually found noise.
	 */
	protected List<Noise> foundNoise = new LinkedList<Noise>();
	
	/**
	 * Number of windows that can be under the threshold.
	 */
	private final int maxSkip;
	private final Map<Integer, Noise> currentNoises = new HashMap<Integer, Noise>();
	private long locationCounter = 0;
	
	/**
	 * @param sampleRate - Sample rate of the signal.
	 * @param windowSize - Size of the window.
	 * @param replayGain - Replay gain value for volume normalization.
	 * @param mirrored - If the analyzed data is half of the window size.
	 * @param duration - Duration the frequency has to be over the threshold in seconds.
	 * @param lowerFreqBound - Lower limit of the desired frequency range.
	 * @param upperFreqBound - Upper limit of the desired frequency range.
	 * @param threshold - dBFS value over which the signal has to be.
	 * @param maxSkip - Number of frames that can be skipped (not be over the threshold).
	 */
	public FrequencySearch(int sampleRate, int windowSize, double replayGain, boolean mirrored,
			double duration, double lowerFreqBound, double upperFreqBound, double threshold, 
			int maxSkip) {
		
		super(sampleRate, windowSize, replayGain);
		this.lowerFreqBound = lowerFreqBound;
		this.upperFreqBound = upperFreqBound;
		this.threshold = threshold;
		this.maxSkip = maxSkip;
		this.duration = (int) (sampleRate * duration);
		int size = windowSize;
		if(mirrored) size = (size / 1) + 1;
		for (int i = 0; i < size; i++) {
			frequencies.add(calculateFrequency(i));
			frequencyDbValues.add(new LinkedList<Double>());
		}
		skipCounter = new int[size];
	}
	
	/**
	 * Will search for values that are over the defined threshold and inside
	 * the defined frequency range. If the value for a frequency fulfills the criteria
	 * defined for the defined duration, it is recognized as noise. 
	 * @param values - Frequency magnitudes in dBFS.
	 */
	protected final void getNoise(double[] values) {
		for(int i = 0; i < values.length; i++) {
			double frequency = frequencies.get(i);
			double value = values[i];
			//Ignore all values that are outside the defined frequency range
			if(frequency >= lowerFreqBound && frequency <= upperFreqBound) {
				if(value > threshold) {
					frequencyDbValues.get(i).add(value);
						//If the noise has the needed size
					if(currentNoises.containsKey(i)) {
						currentNoises.get(i).setLength(currentNoises.get(i).getLength() + windowSize);
					}else {			
						Noise noise = createNoise(locationCounter, windowSize);
						currentNoises.put(i, noise);
					}					
				}else {
					if(currentNoises.containsKey(i)) {
						skipCounter[i]++;
						if(skipCounter[i] > maxSkip) {						
							Noise noise = currentNoises.get(i);
							if(noise.getLength() >= duration) {
								foundNoise.add(noise);							
							}
							currentNoises.remove(i);
							frequencyDbValues.get(i).clear();
							skipCounter[i] = 0;
						}
					}					
				}
			}
		}
		locationCounter += windowSize;
	}
	
	@Override
	public void compact() {
		for(Map.Entry<Integer, Noise> entry : currentNoises.entrySet()) {
			if(entry.getValue().getLength() >= duration) {
				foundNoise.add(entry.getValue());
			}
		}
		foundNoise = combineNoises(foundNoise, sampleRate * 3);
	}
	
	/**
	 * Will be used by the getNoise() method to create the noise
	 * found.
	 * @param location - Location of the noise.
	 * @param length - Length of the noise.
	 * @return 
	 */
	protected abstract Noise createNoise(long location, long length);
	
	/**
	 * Calculates the frequency of a given index in a window.
	 * i * fs / N
	 * @param index
	 * @param sampleCount
	 * @return
	 */
	protected final double calculateFrequency(int index) {
		double freq = index * (double) sampleRate / (double) windowSize;
		return freq;
	}

}
