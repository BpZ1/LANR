package lanr.logic.model;

/**
 * Type of noise found in an audio stream.
 *         
 * @author Nicolas Bruch
 * 
 */
public enum NoiseType {	
	/**
	 * Occurrence of disturbing frequency.
	 */
	Hum("Continuous low frequency noise (under 400 Hz)."),
	/**
	 * Samples exceed the limit of their recording level.
	 */
	Clipping("Samples exeeding the limit of their recording device."
			+ " Causes distortion in the audio."),
	/**
	 * Longer silent area in the audio stream.
	 */
	Silence("Longer silent area in the audio stream."),
	/**
	 * Difference in volume in the audio stream.
	 */
	Volume("Very loud or quiet parts in the audio stream."),
	/**
	 * Impulsive noise.
	 */
	Background("Unwanted sounds over 400 Hz.");
	
	private final String definition;
	
	private NoiseType(String definition) {
		this.definition = definition;
	}
	
	public String getDefinition() {
		return this.definition;
	}
}
