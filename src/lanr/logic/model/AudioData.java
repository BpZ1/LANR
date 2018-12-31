package lanr.logic.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Paths;
import java.util.List;

/**
 *  Represents the audio data from a given file.
 * 
 * @author Nicolas Bruch
 * 
 */
public class AudioData {

	public final static String DATA_ANALYSIS_STARTED = "start";
	public final static String DATA_ANALYZED_PROPERTY = "analyzed";
	private final PropertyChangeSupport state = new PropertyChangeSupport(this);

	private String name;

	private final String path;

	/**
	 * Represents the amount and intensity of the found audio noises.
	 */
	private double severity;
	private final List<AudioStream> audioStreams;
	private boolean isAnalyzed;

	public AudioData(String path, List<AudioStream> audioStreams) {
		this.audioStreams = audioStreams;
		this.path = path;
		this.name = Paths.get(path).getFileName().toString();
	}

	/**
	 * Calculates the severity of all found {@link Noise} types found in all
	 * {@link AudioStream}s.
	 */
	public void calculateSeverity() {
		severity = 0;
		for(AudioStream channel : audioStreams) {
			for (Noise noise : channel.getFoundNoise()) {
				severity += noise.getSeverity();
			}
		}	
	}

	public void addChangeListener(PropertyChangeListener listener) {
		this.state.addPropertyChangeListener(listener);
	}

	public int getSampleRate() {
		if (audioStreams.size() > 0) {
			return audioStreams.get(0).getSampleRate();
		} else {
			return 0;
		}
	}

	public int getBitDepth() {
		if (audioStreams.size() > 0) {
			return audioStreams.get(0).getBitDepth();
		} else {
			return 0;
		}
	}

	public List<AudioStream> getStreams() {
		return audioStreams;
	}

	public AudioStream getAudioChannel(int index) {
		return audioStreams.get(index);
	}

	public String getPath() {
		return path;
	}

	/**
	 * Notifys all listeners that the data is now being analyzed.
	 */
	public void startAnalysis() {
		state.firePropertyChange(DATA_ANALYSIS_STARTED, null, null);
	}

	public void setAnalyzed(boolean value) {
		this.isAnalyzed = value;
		if (value) {
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
