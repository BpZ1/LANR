package lanr.logic.model;

/**
 * @author Nicolas Bruch
 * 
 *         Type of noise found in an audio stream.
 *
 */
public enum NoiseType {
	/**
	 * Occurrence of disturbing frequency.
	 */
	Hum("Occurrence of disturbing frequency"),
	/**
	 * Samples exceed the bit depth of the audio stream.
	 */
	Clipping("Samples exceed the bit depth of the audio stream"),
	/**
	 * Longer silent area in the audio stream.
	 */
	Silence("Longer silent area in the audio stream"),
	/**
	 * Difference in volume in the audio stream.
	 */
	Volume("Difference in volume in the audio stream"),
	/**
	 * Other noises.
	 */
	Other("Difference in volume in the audio stream");
	
	private final String definition;
	
	private NoiseType(String definition) {
		this.definition = definition;
	}
	
	public String getDefinition() {
		return this.definition;
	}
}
