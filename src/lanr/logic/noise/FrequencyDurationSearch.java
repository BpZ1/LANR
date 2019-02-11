package lanr.logic.noise;

import java.util.Map.Entry;

import javolution.util.FastMap;
import javolution.util.FastTable;
import lanr.logic.model.Noise;

/**
 * Base class for the analysis of frequency.
 * The frequencies are analyzed separately and combined at the end.
 * 
 * @author Nicolas Bruch
 *
 */
public abstract class FrequencyDurationSearch extends FrequencySearch {

	/**
	 * dBFS values that meet the requirements of background noise.
	 */
	protected final FastTable<FastTable<Double>> frequencyDbValues = new FastTable<FastTable<Double>>();
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
	protected FastTable<Noise> foundNoise = new FastTable<Noise>();
	
	/**
	 * Number of windows that can be under the threshold.
	 */
	private final int maxSkip;
	private final FastMap<Integer, Noise> currentNoises = new FastMap<Integer, Noise>();
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
	public FrequencyDurationSearch(int sampleRate, int windowSize, double replayGain, boolean mirrored,
			double duration, double lowerFreqBound, double upperFreqBound, double threshold, 
			int maxSkip) {
		
		super(sampleRate, windowSize, replayGain, mirrored);
		this.lowerFreqBound = lowerFreqBound;
		this.upperFreqBound = upperFreqBound;
		this.threshold = threshold;
		this.maxSkip = maxSkip;
		this.duration = (int) ((double)sampleRate * duration);
		int size = windowSize;
		if(mirrored) size = (size / 2) + 1;
		for (int i = 0; i < size; i++) {
			frequencyDbValues.add(new FastTable<Double>());
		}
		skipCounter = new int[size];
	}
	
	/**
	 * Will search for values that are over the defined threshold and inside
	 * the defined frequency range. If the value for a frequency fulfills the criteria
	 * defined for the defined duration, it is recognized as noise. 
	 * @param values - Frequency magnitudes in dBFS.
	 */
	@Override
	public void search(double[] values) {
		for(int i = 0; i < values.length; i++) {
			double frequency = frequencies.get(i);
			double value = values[i];
			//Ignore all values that are outside the defined frequency range
			if(frequency >= lowerFreqBound && frequency <= upperFreqBound) {
				if(value > threshold) {					
					frequencyDbValues.get(i).add(value);
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
		for(Entry<Integer, Noise> entry : currentNoises.entrySet()) {
			if(entry.getValue().getLength() >= duration) {
				foundNoise.add(entry.getValue());
			}
		}
		foundNoise = combineNoises(foundNoise, sampleRate * 3);
	}
	
	@Override
	public FastTable<Noise> getNoise() {
		return foundNoise;
	}
	
	protected final double toDBFS(double binValue) {
		return 2 * binValue / windowSize;
	}
	
	/**
	 * Used by the getNoise() method to create the noise
	 * found.
	 * @param location - Location of the noise.
	 * @param length - Length of the noise.
	 * @return 
	 */
	protected abstract Noise createNoise(long location, long length);
	
	
	/**
	 * Calculates the severity with the given 
	 * @param dBFSValue
	 * @return
	 */
	protected abstract double calculateSeverity(double dBFSValue);
}
