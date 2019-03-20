package lanr;

import javafx.application.*;
import javafx.stage.Stage;
import lanr.controller.MainViewController;

/**
 * @author Nicolas Bruch
 *
 */
public class Lanr extends Application {

	public static void startUp(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainViewController mainController = new MainViewController();
		mainController.start(this.getHostServices());
	}
}
