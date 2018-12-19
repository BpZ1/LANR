package lanr.logic.model;

import java.nio.file.Paths;
import java.util.List;

/**
 * @author Nicolas Bruch
 * 
 *         Represents the audio data from a given file.
 * 
 */
public class AudioData {

	private String name;
	
	private final String path;
	
	/**
	 * Represents the amount and intensity of the found audio noises. 
	 */
	private double severity;
	private final List<AudioChannel> audioChannels;
	private boolean isAnalyzed;

	public AudioData(String path, List<AudioChannel> audioChannels) {
		this.audioChannels = audioChannels;
		//Set the channel indices
		int counter = 1;
		for(AudioChannel channel : audioChannels) {
			channel.setParent(this);
			channel.setIndex(counter);
			counter++;
		}
		this.path = path;
		this.name = Paths.get(path).getFileName().toString();
		
		//DEBUG:
		for(AudioChannel c : audioChannels) {
			c.addNoise(new Noise(NoiseType.Clipping, 200, 10000, 0.5));
			c.addNoise(new Noise(NoiseType.Hum, 5000, 20000, 0.92));
		}
	}

	/**
	 * Calculates the severity of all found {@link Noise} types
	 * found in all {@link AudioChannel}s.
	 */
	public void calculateSeverity() {
		severity = 0;
		for(AudioChannel channel : audioChannels) {
			for (Noise noise : channel.getFoundNoise()) {
				severity += noise.getSeverity();
			}
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

	public void setAnalyzed(boolean value) {
		this.isAnalyzed = value;
	}

	public double getSeverity() {
		return severity;
	}
	
	public boolean isAnalyzed() {
		return isAnalyzed;
	}

	public String getName() {
		return name;
	}
}
