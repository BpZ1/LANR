package lanr.view;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lanr.controller.MainViewController;
import lanr.model.MainModel;

public class MainView extends Stage {
	
	private MainModel model;
	private MainViewController controller;
	private Pane rootPane;
	
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
		bp.setCenter(createCenterPane());
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
			
		});
		
		debugMenuItem.setOnAction(event -> {
			controller.startAnalyzing();
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
	
	private Pane createCenterPane() {
		return new BorderPane();
	}
	private Pane createBottomPane() {
		return new BorderPane();
	}
}
