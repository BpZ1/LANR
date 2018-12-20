package lanr.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lanr.logic.AudioAnalyzer;
import lanr.logic.FileReader;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;
import lanr.logic.model.LANRFileException;

public class MainModel extends Model {

	public static final String AUDIO_ADDED_PROPERTY = "addAudio";
	public static final String AUDIO_REMOVED_PROPERTY = "removeAudio";
	public static final String PROGRESS_UPDATE_PROPERTY = "prorgessUpdate";
	public static final String ERROR_PROPERTY = "error";

	private Thread fileReaderThread;

	private List<AudioData> audioData = new ArrayList<AudioData>();
	private AudioAnalyzer analyzer = new AudioAnalyzer();

	public void analyze() {
		analyzer.anazlyze();
	}

	/**
	 * Adds an audio file to the model and fires the property change.
	 * 
	 * @param path - Path to the audio file
	 * @throws LANRException
	 */
	public void addAudioData(String path) throws LANRException {
		Runnable algorithmRunnable = () -> {
			FileReader reader = new FileReader(path, getFileReaderEventHandler());
			AudioData data;
			try {
				data = reader.readFile(null);
				if (data != null) {
					audioData.add(data);
					state.firePropertyChange(AUDIO_ADDED_PROPERTY, null, data);
				}
			} catch (InterruptedException | IOException | LANRFileException e) {
				state.firePropertyChange(ERROR_PROPERTY, null, new LANRException(e));
			}
		};
		fileReaderThread = new Thread(algorithmRunnable);
		fileReaderThread.start();
	}

	public void removeAudioData(AudioData data) {
		audioData.remove(data);
		state.firePropertyChange(AUDIO_REMOVED_PROPERTY, null, data);
	}

	public List<AudioData> getAudioData() {
		return this.audioData;
	}

	private PropertyChangeListener getFileReaderEventHandler() {
		PropertyChangeListener eventHandler = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				switch(evt.getPropertyName()) {
					case FileReader.LOADING_STARTED_PROPERTY:
						state.firePropertyChange(PROGRESS_UPDATE_PROPERTY, null, true);
						break;
					case FileReader.LOADING_ENDED_PROPERTY:
						state.firePropertyChange(PROGRESS_UPDATE_PROPERTY, null, false);
						break;
					default:
						state.firePropertyChange(PROGRESS_UPDATE_PROPERTY, null, evt.getNewValue());
						break;
				}
				
			}

		};
		return eventHandler;
	}
	
	public void shutdown() {
		if(this.fileReaderThread != null) {
			FileReader.interrupted = true;
		}
	}

}
