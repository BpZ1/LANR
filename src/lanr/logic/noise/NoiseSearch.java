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
	protected List<Noise> combineNoises(List<Noise> noise, long distance) {
		List<Noise> noises = new LinkedList<Noise>(noise);
		int numberOfNoises = 0;
		//Iterate to keep combining noises until the number stays the same.
		do {
			numberOfNoises = noises.size();
			noises = combine(noises, distance);
		}while(numberOfNoises != noises.size());
		return noises;
	}
	
	private final List<Noise> combine(List<Noise> noise, long distance){
		if (noise == null) {
			throw new IllegalArgumentException("The given noise must'nt be null");
		}
		if (noise.isEmpty()) {
			return new LinkedList<Noise>();
		}
		for (int i = 0; i < noise.size(); i++) {
			if(i +1 < noise.size()) {
				Noise current = noise.get(i);
				current.setLength(current.getLength() + distance);
				if(current.add(noise.get(i +1))) {
					noise.remove(i + 1);
					i--;
				}else{
					current.setLength(current.getLength() - distance);
				};
			}
		}
		return noise;
	}
}
