package lanr.controller;

import lanr.logic.model.AudioData;
import lanr.model.MainModel;

/**
 * @author Nicolas Bruch
 *
 */
public class AudioController {

	private MainModel model;
	
	public AudioController(MainModel model) {
		this.model = model;
	}
	
	public void analyze(AudioData data) {
		model.analyzeAudio(data);
	}
}
