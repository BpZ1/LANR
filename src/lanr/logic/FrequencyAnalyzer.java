package lanr.logic;

public abstract class FrequencyAnalyzer {

	protected final int sampleRate;
	protected final int windowSize;
	
	public FrequencyAnalyzer(int sampleRate, int windowSize) {
		this.sampleRate = sampleRate;
		this.windowSize = windowSize;
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
