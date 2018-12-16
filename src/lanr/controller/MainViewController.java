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
	
	public void close() {
		this.mainView.close();
	}
	
	public void startAnalyzing() {
		model.analyze();
	}
	
	public void showWarningDialog(String message) {
		
	}
	
	public void showErrorDialog(String message) {
		
	}
}
