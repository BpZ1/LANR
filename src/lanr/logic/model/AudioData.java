package lanr.logic.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Nicolas Bruch
 * 
 *         Represents the audio data from a given file.
 * 
 */
public class AudioData {

	public final static String DATA_ANALYZED_PROPERTY = "analyzed";
	private final PropertyChangeSupport state = new PropertyChangeSupport(this);
	
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
		this.path = path;
		this.name = Paths.get(path).getFileName().toString();
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
	
	public void addChangeListener(PropertyChangeListener listener) {
		this.state.addPropertyChangeListener(listener);
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

	public List<AudioChannel> getChannel() {
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
		if(value) {
			calculateSeverity();
			state.firePropertyChange(DATA_ANALYZED_PROPERTY, null, null);
		}
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
