package lanr.logic.noise;

import java.util.LinkedList;
import java.util.List;

import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

/**
 * Searches for parts of the signal that are
 * under a specified threshold for a certain duration.
 * 
 * @author Nicolas Bruch
 *
 */
public class SilenceSearch extends NoiseSearch {

	/**
	 * Threshold of the audio signal under which it is
	 * recognized as silence.
	 */
	private static double threshold = 0.02;
	private static int maxSkip = 100;
	/**
	 * Number of seconds a silence has to last before it is
	 * recognized as such.
	 */
	private static int minimalDuration = 5;
		
	private long sampleCounter = 0;
	/**
	 * Counts the silence samples
	 */
	private long counter = 0;
	/**
	 * Number of times a value over the threshold is ignored.
	 */
	private int skipCounter = 0;
	
	
	private List<Noise> foundNoise = new LinkedList<Noise>();
	private Noise currentNoise;
	
	public SilenceSearch(int sampleRate, int windowSize) {
		super(sampleRate, windowSize);
	}
	
	@Override
	public void search(double[] samples) {
		long minimalDurationSamples = minimalDuration * sampleRate;
		for(double s : samples) {
			sampleCounter++;
			if(Math.abs(s) < threshold) {
				counter++;
				if(counter >= minimalDurationSamples) {
					if(currentNoise != null) {
						currentNoise.setLength(currentNoise.getLength() + 1);
					}else {
						currentNoise = new Noise(NoiseType.Silence, sampleCounter - minimalDurationSamples, minimalDurationSamples, 0);
					}
				}				
			}else {
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
	public List<Noise> getNoise() {
		return foundNoise;
	}

	@Override
	public void compact() {
		if(currentNoise != null) {
			foundNoise.add(currentNoise);
		}
		foundNoise = combineNoises(foundNoise, 1.0/sampleRate, sampleRate * 3);		
	}

}
