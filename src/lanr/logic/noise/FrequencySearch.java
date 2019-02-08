package lanr.logic.noise;

import java.util.LinkedList;
import java.util.List;

public abstract class FrequencySearch extends NoiseSearch {

	/**
	 * List containing all frequencies that are represented by the bins.
	 */
	protected final List<Double> frequencies = new LinkedList<Double>();
	
	public FrequencySearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain);
		int size = windowSize;
		if(mirrored) size = (size / 2) + 1;
		for (int i = 0; i < size; i++) {
			frequencies.add(calculateFrequency(i));
		}
	}
	
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
