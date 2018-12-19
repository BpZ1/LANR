package lanr.controller;

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
	
	public void addFile(String path) {
		model.addAudioData(path);
	}
}
