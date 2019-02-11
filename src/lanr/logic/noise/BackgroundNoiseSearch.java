package lanr.logic.noise;

import javolution.util.FastTable;
import lanr.logic.model.FixedList;
import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

/**
 * 
 * Noise search algorithm that calculates the average dBFS values
 * of the last x frames in a given frequency range.
 * 
 * @author Nicolas Bruch
 *
 */
public class BackgroundNoiseSearch extends FrequencySearch {

	private static int volumeThreshold = -40;
	private static double frequencyThreshold = 1000;
	/**
	 * Number of frames over which the signal is analysed
	 */
	private int analysisFrames;
	/**
	 * Duration over which signal is averaged in seconds.
	 */
	private double duration = 0.5;
	
	/**
	 * Values of previous frames.
	 */
	private FastTable<FixedList<Double>> values;
	/**
	 * Current frame index.
	 */
	private int frameIndex = 0;
	
	private FastTable<Noise> foundNoise = new FastTable<Noise>();
	
	public BackgroundNoiseSearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain, mirrored);
		
		analysisFrames = (int) (duration * sampleRate / windowSize);
		values = new FastTable<FixedList<Double>>();
		
		for(int i = 0; i < windowSize; i++) {
			values.add(new FixedList<Double>(analysisFrames));
		}
	}

	@Override
	public void search(double[] bins) {
		
		boolean noiseInFrame = false;
		for(int i = 0; i < bins.length; i++) {
			FixedList<Double> frequencyValues = values.get(i);
			frequencyValues.add(bins[i]);
			if(frequencyValues.size() == analysisFrames 
					&& frequencies.get(i) > frequencyThreshold) {
				double average = 0;
				for(double d : frequencyValues.getAll()) {
					average += d;
				}
				average /= frequencyValues.size();	
				if(average > volumeThreshold) {
					noiseInFrame = true;					
				}
			}
		}
		if(noiseInFrame) {
			foundNoise.add(new Noise(NoiseType.Background, (
					frameIndex - analysisFrames) * windowSize,
					analysisFrames));
		}
		frameIndex++;
	}

	@Override
	public FastTable<Noise> getNoise() {
		return foundNoise;
	}

	@Override
	public void compact() {
		foundNoise = combineNoises(foundNoise, sampleRate * 3);
	}

}
