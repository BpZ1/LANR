package lanr.model;

import java.util.ArrayList;
import java.util.List;

import lanr.logic.AudioAnalyzer;
import lanr.logic.FileReader;
import lanr.logic.model.AudioData;

public class MainModel extends Model {

	public static final String AUDIO_ADDED_PROPERTY = "addAudio";
	public static final String AUDIO_REMOVED_PROPERTY = "removeAudio";
	public static final String PROGRESS_UPDATE_PROPERTY = "prorgessUpdate";
	
	private List<AudioData> audioData = new ArrayList<AudioData>();
	private AudioAnalyzer analyzer = new AudioAnalyzer();
	
	
	public void analyze() {
		analyzer.anazlyze();
	}
	
	/**
	 * Adds an audio file to the model and fires the property change.
	 * @param path - Path to the audio file
	 */
	public void addAudioData(String path) {	
		AudioData data = FileReader.readFile(path);
		audioData.add(data);
		state.firePropertyChange(AUDIO_ADDED_PROPERTY, null, data);
	}
	
	public List<AudioData> getAudioData(){
		return this.audioData;
	}
	
	
}
