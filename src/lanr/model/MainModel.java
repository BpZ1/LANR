package lanr.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import lanr.logic.FileReader;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;


public class MainModel extends Model {
	
	public static final String AUDIO_ADDED_PROPERTY = "addAudio";
	public static final String AUDIO_REMOVED_PROPERTY = "removeAudio";
	public static final String MEMORY_USAGE_PROPERTY = "memory";	
	public static final String IS_WORKING_PROPERTY = "working";
	public static final String IS_IDLE_PROPERTY = "idle";
	public static final String ERROR_PROPERTY = "error";
	public static final String PROGRESS_UPDATE_PROPERTY = "progress";
	
	private List<AudioData> audioData = new ArrayList<AudioData>();
	private static MainModel instance;
	private final FileReader reader;
	
	private MainModel() {
		reader = new FileReader(
				Settings.getInstance().getThreadCount(), //Number of threads
				getReaderEventHandler());	//Event handler
	};
	
	public static MainModel instance() {
		if(instance == null) {
			instance = new MainModel();
		}
		return instance;
	}

	public void analyze() {
		for(AudioData data : audioData) {
			reader.analyze(data);
		}
	}	

	/**
	 * Reads the data of a given file and saves it.
	 * 
	 * @param path - Path to the audio file
	 * @throws LANRException
	 */
	public void addAudioData(String path) throws LANRException {	
		reader.getFileContainer(path);
	}
	
	/**
	 * Starts the decoding and analyzing of the given audio data in a separate thread.
	 * @param data - Data to be decoded and analyzed.
	 */
	public void analyzeAudio(AudioData data) {
		reader.analyze(data);
	}

	public void removeAudioData(AudioData data) {
		audioData.remove(data);
		state.firePropertyChange(AUDIO_REMOVED_PROPERTY, null, data);
	}

	public List<AudioData> getAudioData() {
		return this.audioData;
	}

	/**
	 * Event handler for loading start and stop.
	 * @return
	 */
	private PropertyChangeListener getReaderEventHandler() {
		PropertyChangeListener eventHandler = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				switch(evt.getPropertyName()) {
					case FileReader.MEMORY_USAGE_PROPERTY:
						state.firePropertyChange(MEMORY_USAGE_PROPERTY, null, (double)evt.getNewValue());
						break;
					case FileReader.PROGRESS_PROPERTY:
						state.firePropertyChange(PROGRESS_UPDATE_PROPERTY, null, evt.getNewValue());
						break;
					case FileReader.WORK_ENDED_PROPERTY:
						if(evt.getNewValue() != null) {
							AudioData data = (AudioData) evt.getNewValue();
							audioData.add(data);
							state.firePropertyChange(AUDIO_ADDED_PROPERTY, null, evt.getNewValue());
						}					
						break;
					case FileReader.ERROR_PROPERTY:
						LANRException e = (LANRException) evt.getNewValue();
						state.firePropertyChange(ERROR_PROPERTY, null, e);
						break;				
					case FileReader.WORK_STARTED_PROPERTY:
						state.firePropertyChange(IS_WORKING_PROPERTY, null, null);
						break;
					case FileReader.ALL_TASKS_COMPLETE:
						state.firePropertyChange(IS_IDLE_PROPERTY, null, true);
						break;
				}			
			}
		};
		return eventHandler;
	}
	
	/**
	 * @return True if there are running threads.
	 */
	public boolean isBussy() {
		return reader.isBussy();
	}
	
	/**
	 * Ends all running threads used for analyzing data.
	 */
	public void shutdown() {
		reader.shutdown();
	}

}
