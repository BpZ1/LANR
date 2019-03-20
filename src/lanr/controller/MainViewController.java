package lanr.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.HostServices;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import lanr.logic.model.LANRException;
import lanr.model.MainModel;
import lanr.model.SettingData;
import lanr.model.Settings;
import lanr.view.MainView;
import lanr.view.Utils;
import lanr.view.settings.SettingsView;

/**
 * Main controller.
 * 
 * @author Nicolas Bruch
 *
 */
public class MainViewController {

	private MainModel model;
	private MainView mainView;
	private String settingsError;
	private HostServices hostServices;
	
	public MainViewController() {
		SettingData data = null;
		try {
			data = Settings.load();		
		} catch (IOException e) {
			settingsError = e.getMessage();
		}
		if(data == null) {
			data = new SettingData();
		}
		Settings.createSettings(data);
		this.model = MainModel.instance();
		mainView = new MainView(model, this);
	}
	
	public void start(HostServices hostServices) {		
		this.hostServices = hostServices;
		if(settingsError != null) {
			Utils.showErrorDialog(
					"Could not load settings.ini",
					settingsError + System.lineSeparator() +  
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
	public void addFolder(File file) {
		try {
			model.addAudioData(file);
		} catch (LANRException e) {
			Utils.showErrorDialog("Could not read file!", e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a single file and adds it to the list.
	 * @param path
	 */
	public void addFiles(List<File> files) {
		try {
			for(File f : files) {
				model.addAudioData(f);				
			}
		} catch (LANRException e) {
			Utils.showErrorDialog("Could not read file!", e.getMessage());
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
			Utils.showInfoDialog("Can't open settings",
					"The settings can't be changed while files are being processed.");
		}
	}
	
	public void showAboutDialog() {
		Utils.showInfoDialog("About LANR",
				"Lecture Audio Noise Recognition",
				"LANR (Lecture Audio Noise Recognition) is a " 
						+ "software that strives to automate the reviewing process for the audio of"
						+ " lecture recordings at the WIAI faculty of the University of Bamberg. It was developed by " 
						+ "Nicolas Bruch as part of his bachelor thesis at the WIAI faculty of the Otto-Friedrich University Bamberg." 
						+ System.lineSeparator() + System.lineSeparator() 
						+ "Version: " + getClass().getPackage().getImplementationVersion() 
						+ System.lineSeparator() + System.lineSeparator() 
						+ "Copyright © 2019 Nicolas Bruch" + System.lineSeparator() 
						+ "This program comes with absolutely no warranty." 
						+ System.lineSeparator() 
						+ "See the GNU General Public License version 3 for details.");
	}
	
	public void showHelpDialog() {
		Hyperlink link = new Hyperlink("GitHub");
		link.setOnAction(event ->{
			hostServices.showDocument("https://github.com/BpZ1/LANR");
		});
		VBox content = new VBox();
		Label text = new Label("For questions or bugs contact:"
				+ System.lineSeparator() + "nicolas.bruch@hotmail.de "
				+ System.lineSeparator() +"or visit: ");
		content.getChildren().add(text);
		content.getChildren().add(link);
		
		Utils.showInfoDialog("Help", "Help", content);
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
