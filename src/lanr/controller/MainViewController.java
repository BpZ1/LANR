package lanr.controller;

import java.io.IOException;

import javafx.stage.Modality;
import lanr.logic.model.LANRException;
import lanr.model.MainModel;
import lanr.model.SettingData;
import lanr.model.Settings;
import lanr.view.MainView;
import lanr.view.SettingsView;

/**
 * Main controller.
 * 
 * @author Nicolas Bruch
 *
 */
public class MainViewController {

	private MainModel model;
	private MainView mainView;
	private boolean settingsLoaded = true;
	
	public MainViewController() {
		SettingData data = null;
		try {
			data = Settings.load();		
		} catch (IOException e) {
			settingsLoaded = false;
		}
		if(data == null) {
			data = new SettingData();
		}
		Settings.createSettings(data);
		this.model = MainModel.instance();
		mainView = new MainView(model, this);
	}
	
	public void start() {		
		if(!settingsLoaded) {
			mainView.showErrorDialog(
					"Could not load settings.ini",
					"Default settings will be used instead.");
		}
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
		if(!model.isBussy()) {
			SettingsView view = new SettingsView();
			view.initModality(Modality.APPLICATION_MODAL);
			view.showAndWait();
		}else {
			mainView.showInfoDialog("Can't open settings",
					"The settings can't be changed while files are being processed.");
		}
	}
	
	/**
	 * State of the current computing process.
	 * @return True if the program is currently reading files.
	 */
	public boolean isBussy() {
		return model.isBussy();
	}
	
	/**
	 * Ends all analyzing processes.
	 */
	public void shutdown() {
		this.model.shutdown();
	}
}
