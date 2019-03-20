/*
 * LANR (Lecture Audio Noise Recognition) is a software that strives to automate
 * the reviewing process of lecture recordings at the WIAI faculty of the University of Bamberg.
 *
 * Copyright (C) 2019 Nicolas Bruch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package lanr.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lanr.logic.AudioLogic;
import lanr.logic.LogWriter;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;

/**
 * @author Nicolas Bruch
 *
 */
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
	private final AudioLogic reader;
	
	private MainModel() {
		reader = new AudioLogic(
				(int) Settings.getInstance()
				.getPropertyValue(Settings.THREAD_COUNT_PROPERTY_NAME),
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
	
	private boolean isSupportedFile(String path) {
		String p = path.toLowerCase();
		if(p.endsWith(".mp3")) {
			return true;
		}
		if(p.endsWith(".mp4")) {
			return true;
		}
		if(p.endsWith(".avi")) {
			return true;
		}
		return false;
	}

	/**
	 * Reads the data of a given file and saves it.
	 * 
	 * @param path - Path to the audio file
	 * @throws LANRException
	 */
	public void addAudioData(File file) throws LANRException {	
		if(file == null) {
			throw new IllegalArgumentException("The given file mustn't be null");
		}
		if(file.isFile()) {
			reader.getFileContainer(new String[] {file.getAbsolutePath()});			
		}else if(file.isDirectory()) {			
			List<String> files = new LinkedList<String>();
			for(String p : file.list()) {
				File contained = new File(file.getAbsolutePath() + "\\" + p);
				if(contained.isFile()) {
					if(isSupportedFile(p)) {
						files.add(contained.getAbsolutePath());
					}
				}else {
					addAudioData(contained);
				}
			}
			String[] fArr = new String[files.size()];
			fArr = files.toArray(fArr);
			reader.getFileContainer(fArr);
		}
	}
	
	/**
	 * Starts the decoding and analyzing of the given audio data in a separate thread.
	 * @param data - Data to be decoded and analyzed.
	 */
	public void analyzeAudio(AudioData data) {
		reader.analyze(data);
	}
	
	public void createLogFile(AudioData data) throws LANRException {
		if(!data.isAnalyzed()) {
			throw new LANRException("Can't create a log for files that are not analysed!");
		}
		LogWriter.writeLogFile(data);
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
					case AudioLogic.MEMORY_USAGE_PROPERTY:
						state.firePropertyChange(MEMORY_USAGE_PROPERTY, null, (double)evt.getNewValue());
						break;
					case AudioLogic.PROGRESS_PROPERTY:
						state.firePropertyChange(PROGRESS_UPDATE_PROPERTY, null, evt.getNewValue());
						break;
					case AudioLogic.WORK_ENDED_PROPERTY:
						if(evt.getNewValue() != null) {
							AudioData[] data = (AudioData[]) evt.getNewValue();
							for(AudioData audio : data) {
								audioData.add(audio);								
							}
							state.firePropertyChange(AUDIO_ADDED_PROPERTY, null, evt.getNewValue());
						}					
						break;
					case AudioLogic.ERROR_PROPERTY:
						if(evt.getNewValue() instanceof LANRException) {
							LANRException e = (LANRException) evt.getNewValue();
							state.firePropertyChange(ERROR_PROPERTY, null, e);
						}else {
							Exception e = (Exception) evt.getNewValue();
							throw new RuntimeException(e);
						}
						break;				
					case AudioLogic.WORK_STARTED_PROPERTY:
						state.firePropertyChange(IS_WORKING_PROPERTY, null, null);
						break;
					case AudioLogic.ALL_TASKS_COMPLETE:
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
