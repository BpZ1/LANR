package lanr.logic.noise;

import java.util.LinkedList;
import java.util.List;

import lanr.logic.model.Noise;

/**
 * Search method for recognizing and creating {@link Noise} in a given sample
 * array.
 * 
 * @author Nicolas Bruch
 *
 */
public abstract class NoiseSearch {

	/**
	 * Samples rate of the signal to be analyzed.
	 */
	protected final int sampleRate;
	/**
	 * Size of the data arrays that will be given for analysis.
	 */
	protected final int windowSize;
	
	/**
	 * Replay gain represents the overall volume of the file.
	 */
	protected final double replayGain;

	public NoiseSearch(int sampleRate, int windowSize, double replayGain) {
		this.sampleRate = sampleRate;
		this.windowSize = windowSize;
		this.replayGain = replayGain;
	}

	/**
	 * Searches for {@link Noise} in the given samples. The values of the samples
	 * should be between -1 and 1 if the given data is not in frequency domain.
	 * 
	 * @param samples - Signal samples between -1 and 1, or frequency data.
	 */
	public abstract void search(double[] samples);

	/**
	 * Returns the found {@link Noise}. <b>Should only be called after compact() was
	 * executed.</b>
	 * 
	 * @return List of found noises.
	 */
	public abstract List<Noise> getNoise();

	public abstract void compact();

	/**
	 * Combines all overlapping noises to one noise. If the end of the previous and
	 * the beginning of the current noise overlap, they will be combined to a single
	 * noise.
	 * 
	 * @param noise
	 * @param severity - Value that will be multiplied with the length of the noise.
	 *                 The length depends on the sample rate and should therefore be
	 *                 calculated into the value.
	 * @param distance - Extra length that will be added to every noise when
	 *                 combining. This will combine noise even if they are not
	 *                 overlapping if they are the given distance away from each
	 *                 other. Value is in samples.
	 * @return
	 */
	protected List<Noise> combineNoises(List<Noise> noise, double severity, long distance) {
		List<Noise> noises = new LinkedList<Noise>(noise);
		int numberOfNoises = 0;
		//Iterate to keep combining noises until the number stays the same.
		do {
			numberOfNoises = noises.size();
			noises = combine(noises, severity, distance);
		}while(numberOfNoises != noises.size());
		return noises;
	}
	
	private final List<Noise> combine(List<Noise> noise, double severity, long distance){
		if (noise == null) {
			throw new IllegalArgumentException("The given noise must'nt be null");
		}
		if (noise.isEmpty()) {
			return new LinkedList<Noise>();
		}
		List<Noise> noises = new LinkedList<Noise>();
		Noise currentNoise = noise.get(0);
		long endOfNoise = currentNoise.getLocation() + currentNoise.getLength() + distance;
		for (Noise n : noise) {
			long endOfN = n.getLocation() + n.getLength();
			if (n.getLocation() < endOfNoise) {
				// Check if it goes further than the current noise
				if (endOfN > endOfNoise) {
					currentNoise.setLength(endOfN - currentNoise.getLocation());
				}
			} else {
				// Sets the new noise as current if they are not overlapping
				currentNoise.setSeverity((currentNoise.getLength() / sampleRate) * severity);
				noises.add(currentNoise);
				currentNoise = n;
				endOfNoise = endOfN + distance;
			}
		}
		noises.add(currentNoise);
		return noises;
	}
}
