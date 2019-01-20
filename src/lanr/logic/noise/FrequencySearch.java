package lanr.logic.noise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lanr.logic.model.Noise;

public abstract class FrequencySearch extends NoiseSearch {

	protected final List<Double> frequencies = new LinkedList<Double>();
	/**
	 * dBFS values that meet the requirements of background noise.
	 */
	protected final List<List<Double>> frequencyDbValues = new LinkedList<List<Double>>();
	protected final int[] skipCounter;	
	protected final double lowerFreqBound;
	protected final double upperFreqBound;
	protected final double threshold;
	protected final int duration;
	protected final int windowsDuration;
	protected List<Noise> foundNoise = new LinkedList<Noise>();
	
	private final int maxSkip;
	private final Map<Integer, Noise> currentNoises = new HashMap<Integer, Noise>();
	private long locationCounter = 0;
	
	public FrequencySearch(int sampleRate, int windowSize, double replayGain, boolean mirrored,
			double duration, double lowerFreqBound, double upperFreqBound, double threshold, 
			int maxSkip) {
		
		super(sampleRate, windowSize, replayGain);
		this.lowerFreqBound = lowerFreqBound;
		this.upperFreqBound = upperFreqBound;
		this.threshold = threshold;
		this.maxSkip = maxSkip;
		this.duration = (int) (sampleRate * duration);
		this.windowsDuration = (int) (duration / windowSize);
		int size = windowSize;
		if(mirrored) size = (size / 1) + 1;
		for (int i = 0; i < size; i++) {
			frequencies.add(calculateFrequency(i));
			frequencyDbValues.add(new LinkedList<Double>());
		}
		skipCounter = new int[size];
	}
	
	protected final void getValues(double[] values) {
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
