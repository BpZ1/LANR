package lanr.logic.noise;

import javolution.util.FastTable;
import lanr.logic.Utils;
import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;


/**
 * @author Nicolas Bruch
 *
 */
public class VolumeSearch extends NoiseSearch {

	private static double lowerSilentThreshold = -40;
	private static double upperSilentThreshold = -30;
	
	private static double lowerLoudThreshold = -10;
	private static double upperLoudThreshold = 0;
	/**
	 * Maximum number of samples that can be over the threshold 
	 * in a silence window.
	 */
	private final int maxSkip = sampleRate / 500;
	/**
	 * Number of seconds a silence has to last before it is
	 * recognized as such.
	 */
	private static int minimalDuration = 1;
		
	private long sampleCounter = 0;
	/**
	 * Counts the samples
	 */
	private long counter = 0;
	/**
	 * Number of times a value over the threshold is ignored.
	 */
	private int skipCounter = 0;
	
	private FastTable<Noise> foundNoise = new FastTable<Noise>();
	private Noise currentNoise;
	
	public VolumeSearch(int sampleRate, int windowSize, double replayGain) {
		super(sampleRate, windowSize, replayGain);
	}

	@Override
	public void search(double[] samples) {
		long minimalDurationSamples = minimalDuration * sampleRate;
		for(double s : samples) {
			sampleCounter++;
			//Check if the sample is under the threshold
			double dbValue = Utils.sampleToDBFS(s) + replayGain;
			if((dbValue > lowerSilentThreshold && dbValue < upperSilentThreshold) ||
					(dbValue > lowerLoudThreshold && dbValue < upperLoudThreshold)) {
				counter++;
				//Check if the sample
				if(counter >= minimalDurationSamples) {
					if(currentNoise != null) {
						currentNoise.setLength(currentNoise.getLength() + 1);
					}else {
						currentNoise = new Noise(NoiseType.Volume,
								sampleCounter - minimalDurationSamples,
								minimalDurationSamples);
					}
				}				
			}else {
				/*
				 * If a sample over the threshold was found increment the skip counter
				 * If the skip counter is over the allowed number of skips
				 * start a new search by resetting the counter. If a noise was already
				 * created it will be added to the list of found noises.
				 */
				skipCounter++;
				if(skipCounter > maxSkip) {
					counter = 0;
					skipCounter = 0;
					if(currentNoise != null) {
						foundNoise.add(currentNoise);
						currentNoise = null;
					}
				}
			}
		}
	}

	@Override
	public FastTable<Noise> getNoise() {
		return foundNoise;
	}

	@Override
	public void compact() {
		if(currentNoise != null) {
			foundNoise.add(currentNoise);
		}
		//Noises that are up to 3 seconds apart will still be counted as one.
		foundNoise = combineNoises(foundNoise, sampleRate * 3);		
	}

}
