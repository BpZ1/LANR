package lanr.logic.noise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lanr.logic.FrequencyAnalyzer;
import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

public class HummingSearch extends FrequencyAnalyzer {
	
	/**
	 * Severity per second of noise
	 */
	private static final double SEVERITY_WEIGHT = 1;
	private static final double TOLERANCE = 1;
	/**
	 * Threshold for the decibel value of the humming.
	 */
	private static final double DECIBEL_BOUND_VALUE = -40;
	/**
	 * We are only interested in low frequency humming under 400 Hz.
	 */
	private static final double FREQUENCY_BOUND_VALUE = 400;
	/**
	 * Number of windows that can be interrupted and still count
	 */
	private int toleranceLevel = (int) (TOLERANCE * 4000 / windowSize);	
	/**
	 * Number of seconds for which the humming must persist.
	 */
	private static int duration = 3;
	private int threshold = sampleRate * duration;
	/**
	 * Value how much one window counts in severity.
	 */
	double severityValue = SEVERITY_WEIGHT * (1 / windowSize);

	public HummingSearch(int sampleRate, int windowSize) {
		super(sampleRate, windowSize);
		//Calculate the frequencies of the input bins
		for(int i = 0; i < windowSize / 2; i++) {
			frequencies.add(calculateFrequency(i));
		}
	}
	private int windowCount = 0;
	private long locationCounter = 0;
	private List<Noise> noise = new LinkedList<Noise>();

	private List<Double> frequencies = new LinkedList<Double>();
	private Map<Integer, PotentialHumming> frequencyMap = new HashMap<Integer, PotentialHumming>();
	private List<Integer> toBeRemoved = new LinkedList<Integer>();

	@Override
	public void search(double[] samples) {
		//Check all frequencies for high decibel values
		for (int i = 0; i < samples.length; i++) {
			//If found add it to the frequency map or update the window in which it was found
			if (samples[i] > DECIBEL_BOUND_VALUE && frequencies.get(i) < FREQUENCY_BOUND_VALUE) {
				PotentialHumming pn = frequencyMap.get(i);
				if (pn != null) {
					pn.setLastWindowIndex(windowCount);
					pn.getNoise().setLength(pn.getNoise().getLength() + windowSize);
					pn.incrementCounter();
				} else {
					frequencyMap.put(i, new PotentialHumming(new Noise(NoiseType.Hum,
							locationCounter, windowSize, severityValue * windowSize)));
				}
			}
		}
		//Check which frequencies skipped windows and could be removed
		for (Entry<Integer, PotentialHumming> entry : frequencyMap.entrySet()) {
			PotentialHumming pn = entry.getValue();
			//Check if the frequency was found this window
			if((windowCount - pn.getLastWindowIndex()) > 0) {
				pn.incrementSkipCounter();
			}
			//Check if they over stepped the tolerance level
			if(pn.getSkipCounter() > toleranceLevel) {
				//If they are over the threshold they are confirmed
				if(pn.getCounter() * windowSize > threshold) {
					this.noise.add(pn.getNoise());
				}
				toBeRemoved.add(entry.getKey());
			}
		}
		//Remove all frequencies that went over the threshold
		for(Integer i : toBeRemoved) {
			frequencyMap.remove(i);
		}
		toBeRemoved.clear();
		windowCount++;
		locationCounter += windowSize;
	}

	double freqs = 0;
	int counter = 0;

	@Override
	public List<Noise> getNoise() {
		return noise;
	}

	@Override
	public void compact() {
		//Add the last noises if they were long enough
		for (Entry<Integer, PotentialHumming> entry : frequencyMap.entrySet()) {
			PotentialHumming pn = entry.getValue();
			if(pn.getCounter() * windowSize > threshold) {
				noise.add(pn.getNoise());
			}
		}
		//Combine noises if possible		
		if(!noise.isEmpty()) {
			this.noise = combineNoises(noise, SEVERITY_WEIGHT/sampleRate, sampleRate);
		}	
	}

}
