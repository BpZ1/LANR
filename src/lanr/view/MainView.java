package lanr.view;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lanr.controller.MainViewController;
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
			this.controller.close();
		});
		
		settingsMenuItem.setOnAction(event -> {
			
		});
		
		menuBar.getMenus().add(fileMenu);
		menuBar.getMenus().add(editMenu);			
		return menuBar;
	}
	
	private void openFileDialog() {
		Frame frame = new Frame();
		FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
		fd.setDirectory("C:\\");
		fd.setFile("*.mp4");
		fd.setVisible(true);
		File[] files = fd.getFiles();
		for(File file : files) {
			controller.addFile(file);
		}
	}
	
	private VBox createCenterPane() {
		return new VBox();
	}
	private Pane createBottomPane() {
		return new BorderPane();
	}
}
