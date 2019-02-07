package lanr.controller;

import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;
import lanr.model.MainModel;
import lanr.view.Utils;

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
	
	public void createLog(AudioData data) {
		try {
			model.createLogFile(data);
		} catch (LANRException e) {
			Utils.showErrorDialog("Log creation not possible!",
					"Logs can only be created for analysed files.");
		}
	}
}
