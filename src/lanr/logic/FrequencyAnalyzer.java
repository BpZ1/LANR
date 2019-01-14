package lanr.logic;

import lanr.logic.noise.NoiseSearch;

public abstract class FrequencyAnalyzer extends NoiseSearch {
	
	public FrequencyAnalyzer(int sampleRate, int windowSize, double replayGain) {
		super(sampleRate, windowSize, replayGain);
	}

	/**
	 * Calculates the frequency of a given index in a window.
	 * i * fs / N
	 * @param index
	 * @param sampleCount
	 * @return
	 */
	protected double calculateFrequency(int index) {
		double freq = index * (double) sampleRate / (double) windowSize;
		return freq;
	}
}
