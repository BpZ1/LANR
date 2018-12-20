package lanr.controller;

import lanr.logic.model.LANRException;
import lanr.model.MainModel;
import lanr.view.MainView;

/**
 * @author Nicolas Bruch
 *
 *	Main controller.
 */
public class MainViewController {

	private MainModel model;
	private MainView mainView;
	
	public MainViewController(MainModel model) {
		this.model = model;
		mainView = new MainView(model, this);
	}
	
	public void start() {		
		mainView.showAndWait();
	}
	
	public void startAnalyzing() {
		model.analyze();
	}
	
	public void addFile(String path) {
		try {
			model.addAudioData(path);
		} catch (LANRException e) {
			mainView.showErrorDialog("Could not read file!", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		this.model.shutdown();
	}
}
