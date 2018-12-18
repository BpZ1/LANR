package lanr.view;

import java.io.File;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import lanr.controller.MainViewController;
import lanr.logic.FileReader;
import lanr.logic.model.AudioData;
import lanr.model.MainModel;

public class MainView extends Stage {
	
	private MainModel model;
	private MainViewController controller;
	private Pane rootPane;
	private VBox centerPane;
	
	public MainView(MainModel model, MainViewController controller) {
		this.model = model;
		this.controller = controller;
		
		this.setTitle("LANR");
		rootPane = createRootPane();
		Scene scene = new Scene(rootPane, 800, 500);
		scene.getStylesheets().add(MainView.class.getResource("Main.css").toExternalForm());
		this.setScene(scene);
	}
	
	private Pane createRootPane() {

		BorderPane bp = new BorderPane();
		bp.setTop(createTopMenu());
		centerPane = createCenterPane();
		bp.setCenter(centerPane);
		bp.setBottom(createBottomPane());

		return bp;
	}
	
	private Node createTopMenu() {		
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem openMenuItem = new MenuItem("Open");	
		MenuItem debugMenuItem = new MenuItem("Debug");	
		MenuItem exitMenuItem = new MenuItem("Exit");
		fileMenu.getItems().add(openMenuItem);
		fileMenu.getItems().add(debugMenuItem);
		fileMenu.getItems().add(exitMenuItem);
		Menu editMenu = new Menu("Edit");
		MenuItem settingsMenuItem = new MenuItem("Exit");
		editMenu.getItems().add(settingsMenuItem);
		
		openMenuItem.setOnAction(event -> {
			openFileDialog();
		});
		
		debugMenuItem.setOnAction(event -> {
			for(AudioData data : model.getAudioData()) {
				centerPane.getChildren().add(new AudioVisualisation(data));
			}
		});
		
		exitMenuItem.setOnAction(event -> {
			 Stage stage = (Stage) this.getScene().getWindow();
			 stage.close();
		});
		
		settingsMenuItem.setOnAction(event -> {
			
		});
		
		menuBar.getMenus().add(fileMenu);
		menuBar.getMenus().add(editMenu);			
		return menuBar;
	}
	
	private void openFileDialog() {
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Open Lecture Recording");
		 fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("Video Files", "*.mp4", "*.avi"),
		         new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
		         new ExtensionFilter("All Files", "*.*"));
		 File selectedFile = fileChooser.showOpenDialog(this);
		 if (selectedFile != null) {
			 model.addAudioData(FileReader.readFile(selectedFile.getAbsolutePath()));
		 }
	}
	
	private VBox createCenterPane() {
		return new VBox();
	}
	private Pane createBottomPane() {
		return new BorderPane();
	}
}
