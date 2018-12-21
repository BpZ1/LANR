package lanr.controller;

import javafx.stage.Modality;
import lanr.logic.model.LANRException;
import lanr.model.MainModel;
import lanr.view.MainView;
import lanr.view.SettingsView;

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
	
	/**
	 * Starts the analyzing process for all files.
	 */
	public void startAnalyzing() {
		model.analyze();
	}
	
	/**
	 * Reads a single file and adds it to the list.
	 * @param path
	 */
	public void addFile(String path) {
		try {
			model.addAudioData(path);
		} catch (LANRException e) {
			mainView.showErrorDialog("Could not read file!", e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens the settings window.
	 */
	public void openSettings() {
		SettingsView view = new SettingsView();
		view.initModality(Modality.APPLICATION_MODAL);
		view.showAndWait();
	}
	
	/**
	 * Ends all analyzing processes.
	 */
	public void shutdown() {
		this.model.shutdown();
	}
}
