package lanr.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import lanr.logic.AudioAnalyzer;
import lanr.logic.FileReader;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;
import lanr.logic.model.LANRFileException;

public class MainModel extends Model {

	private static MainModel instance;
	
	private MainModel() {};
	
	public static MainModel instance() {
		if(instance == null) {
			instance = new MainModel();
		}
		return instance;
	}
	
	/**
	 * Counter for the number of running threads
	 */
	private static int processCounter = 0;
	private static ReentrantLock counterLock = new ReentrantLock(true);
	
	public static final String AUDIO_ADDED_PROPERTY = "addAudio";
	public static final String AUDIO_REMOVED_PROPERTY = "removeAudio";
	public static final String PROGRESS_UPDATE_PROPERTY = "progressUpdate";
	public static final String ERROR_PROPERTY = "error";

	private ExecutorService executors = Executors.newFixedThreadPool(10);

	private List<AudioData> audioData = new ArrayList<AudioData>();
	private AudioAnalyzer analyzer = new AudioAnalyzer();

	public void analyze() {
		analyzer.anazlyze();
	}
	
	public boolean isBussy() {
		if(processCounter == 0) {
			return false;
		}
		return true;
	}
	
	private static void incrementCounter(){
		counterLock.lock();
        try{
            processCounter++;
        }finally{
        	counterLock.unlock();
        }
     }
	
	private static void decrementCounter(){
		counterLock.lock();
        try{
            processCounter--;
        }finally{
        	counterLock.unlock();
        }
     }

	/**
	 * Reads the data of a given file and saves it.
	 * 
	 * @param path - Path to the audio file
	 * @throws LANRException
	 */
	public void addAudioData(String path) throws LANRException {	
		Runnable algorithmRunnable = () -> {
			try {
				AudioData data = FileReader.getFile(path, getFileEventHandler());
				if (data != null) {
					audioData.add(data);
					state.firePropertyChange(AUDIO_ADDED_PROPERTY, null, data);
					decrementCounter();
				}
			} catch (InterruptedException | IOException | LANRFileException e) {
				state.firePropertyChange(ERROR_PROPERTY, null, new LANRException(e));
			}
		};
		incrementCounter();
		executors.execute(algorithmRunnable);
	}
	
	/**
	 * Starts the decoding and analyzing of the given audio data in a separate thread.
	 * @param data - Data to be decoded and analyzed.
	 */
	public void analyzeAudio(AudioData data) {
		FileReader.interrupted = false;
		Runnable algorithmRunnable = () -> {
			try {
				FileReader.readFile(data, getFileEventHandler());
				decrementCounter();
			} catch (InterruptedException | IOException e) {
				state.firePropertyChange(ERROR_PROPERTY, null, new LANRException(e));
			}
		};
		incrementCounter();
		executors.execute(algorithmRunnable);
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
	private PropertyChangeListener getFileEventHandler() {
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
				}			
			}
		};
		return eventHandler;
	}
	
	/**
	 * Ends all running threads used for analyzing data.
	 */
	public void shutdown() {
		FileReader.interrupted = true;
		executors.shutdown();
	}

}
