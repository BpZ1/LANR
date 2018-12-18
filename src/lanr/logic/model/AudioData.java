package lanr.logic.model;

import java.util.List;

/**
 * @author Nicolas Bruch
 * 
 *         Represents the audio data from a given file.
 * 
 */
public class AudioData {

	private final String path;
	/**
	 * List of found {@link Noise} types in the different channel of this audio file.
	 */
	private List<Noise> foundNoise;
	/**
	 * Represents the amount and intensity of the found audio noises. 
	 */
	private double severity;
	private final List<AudioChannel> audioChannels;

	public AudioData(String path, List<AudioChannel> audioChannels) {
		this.audioChannels = audioChannels;
		this.path = path;
	}

	private void calculateSeverity() {
		severity = 0;
		for (Noise noise : foundNoise) {
			severity += noise.getSeverity();
		}
	}

	public int getSampleRate() {
		if (audioChannels.size() > 0) {
			return audioChannels.get(0).getSampleRate();
		} else {
			return 0;
		}
	}

	public int getBitDepth() {
		if (audioChannels.size() > 0) {
			return audioChannels.get(0).getBitDepth();
		} else {
			return 0;
		}
	}

	public List<AudioChannel> getAllChannel() {
		return audioChannels;
	}

	public AudioChannel getAudioChannel(int index) {
		return audioChannels.get(index);
	}

	public String getPath() {
		return path;
	}

	public List<Noise> getFoundNoise() {
		return foundNoise;
	}

	public void setFoundNoise(List<Noise> foundNoise) {
		this.foundNoise = foundNoise;
	}

	public void addNoise(Noise noise) {
		this.foundNoise.add(noise);
		calculateSeverity();
	}

	public void removeNoise(Noise noise) {
		this.foundNoise.remove(noise);
		calculateSeverity();
	}

	public double getSeverity() {
		return severity;
	}
}
