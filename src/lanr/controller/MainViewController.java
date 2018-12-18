package lanr.controller;

import java.io.File;

import lanr.logic.FileReader;
import lanr.logic.model.AudioData;
import lanr.model.MainModel;
import lanr.view.MainView;

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
	
	public void addFile(File file) {
		AudioData data = FileReader.readFile(file.getAbsolutePath());		
		model.addAudioData(data);
	}
	
	public void showWarningDialog(String message) {
		
	}
	
	public void showErrorDialog(String message) {
		
	}
}
