package lanr;

import javafx.application.*;
import javafx.stage.Stage;
import lanr.controller.MainViewController;
import lanr.model.MainModel;

public class Lanr extends Application {

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainViewController mainController = new MainViewController();
		mainController.start();
	}
}
