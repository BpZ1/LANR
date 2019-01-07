package lanr.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import lanr.controller.AudioController;
import lanr.controller.MainViewController;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRException;
import lanr.model.MainModel;

/**
 * 
 * @author Nicolas Bruch
 *
 */
public class MainView extends Stage {
	
	private static final String BOTTOM_PANE_CSS_ID = "bottomPane";
	private MainViewController controller;
	private Pane rootPane;
	private Accordion centerPane;
	private ProgressBar progressBar;
	private ProgressBar memoryUsage;
	private SimpleStringProperty memoryValue = new SimpleStringProperty("");
	private ObservableList<TitledPane> audioList;

	public MainView(MainModel model, MainViewController controller) {				
		this.controller = controller;
		this.setTitle("LANR");
		rootPane = createRootPane();
		Scene scene = new Scene(rootPane, 800, 500);
		this.setMinWidth(700);
		this.setMinHeight(400);
		scene.getStylesheets().add(MainView.class.getResource("Main.css").toExternalForm());
		this.setScene(scene);
		// Add event handler
		model.addChangeListener(getEventHandler());
		scene.getWindow().setOnCloseRequest(event ->{
			controller.shutdown();
		});
	}

	private Pane createRootPane() {
		BorderPane bp = new BorderPane();
		bp.setTop(createTopMenu());
		centerPane = createCenterPane();
		bp.setCenter(centerPane);
		bp.setBottom(createBottomPane());
		return bp;
	}
	
	private void updateMemoryUsage(double value) {
		memoryUsage.setProgress(value);
		if(value < 0.6) {
			memoryUsage.setStyle("-fx-accent: green;");
		}else if(value < 0.7) {
			memoryUsage.setStyle("-fx-accent: yellow;");
		}else if(value < 0.8) {
			memoryUsage.setStyle("-fx-accent: orange;");
		}else{
			memoryUsage.setStyle("-fx-accent: red;");
		}
		memoryValue.setValue((int)(value * 100) + "%");
	}


	/**
	 * Contains the Menu bar
	 * 
	 * @return
	 */
	private Node createTopMenu() {
		MenuBar menuBar = new MenuBar();
		menuBar.setPadding(new Insets(2,2,2,2));
		Menu fileMenu = new Menu("File");
		MenuItem openMenuItem = new MenuItem("Open");
		MenuItem openFolderMenuItem = new MenuItem("Open Folder");
		MenuItem removeMenuItem = new MenuItem("Remove all");
		MenuItem exitMenuItem = new MenuItem("Exit");
		fileMenu.getItems().add(openMenuItem);
		fileMenu.getItems().add(openFolderMenuItem);
		fileMenu.getItems().add(exitMenuItem);
		Menu editMenu = new Menu("Edit");
		MenuItem analysisMenuItem = new MenuItem("Analyse all");
		MenuItem settingsMenuItem = new MenuItem("Settings");
		editMenu.getItems().add(settingsMenuItem);
		editMenu.getItems().add(analysisMenuItem);

		openMenuItem.setOnAction(event -> {
			openFileDialog();
		});
		
		openFolderMenuItem.setOnAction(event -> {
			openDirectoryDialog();
		});
		
		removeMenuItem.setOnAction(event -> {
			if(Utils.confirmationDialog("Confirm your selection",
					"Are you sure you want to remove all loaded data?",
					"Created spectrograms and logs will not be deleted.")) {
				audioList.clear();				
			}
		});

		exitMenuItem.setOnAction(event -> {
			controller.shutdown();
			Stage stage = (Stage) this.getScene().getWindow();
			stage.close();
		});
		
		analysisMenuItem.setOnAction(event ->{
			boolean conirmation = Utils.confirmationDialog("Analyse all data",
					"Do you want to analyse all files?",
					"Depending on the number and length of the files this may take a while.");
			if(conirmation) {
				controller.startAnalyzing();
			}
		});

		settingsMenuItem.setOnAction(event -> {
			controller.openSettings();
		});

		menuBar.getMenus().add(fileMenu);
		menuBar.getMenus().add(editMenu);
		return menuBar;
	}
	
	private void openFileDialog() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Lecture Recording");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Media Files", "*.mp4", "*.avi", "*.mp3"),
				new ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(this);
		if (selectedFile != null) {
			controller.addFile(selectedFile);
		}
	}
	
	private void openDirectoryDialog() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Folder");
		File selectedDirectory = directoryChooser.showDialog(this);		
		if(selectedDirectory != null) {
			controller.addFile(selectedDirectory);
		}
	}

	private Accordion createCenterPane() {
		Accordion center = new Accordion();
		audioList = center.getPanes();
		return center;
	}

	private Pane createBottomPane() {
		AnchorPane pane = new AnchorPane();
		pane.setId(BOTTOM_PANE_CSS_ID);
		// Creating the memory progress bar
		this.memoryUsage = new ProgressBar();
		this.memoryUsage.tooltipProperty().set(new Tooltip() {{
			textProperty().bind(memoryValue);
		}});
		HBox memoryBox = new HBox();
		memoryBox.setSpacing(3);
		Label memoryLabel = new Label("Memory usage");
		memoryBox.getChildren().addAll(memoryLabel, memoryUsage);
				
		this.progressBar = new ProgressBar();
		this.progressBar.setVisible(false);
		pane.getChildren().addAll(memoryBox, progressBar);
		AnchorPane.setLeftAnchor(memoryBox, 5.0);
		AnchorPane.setRightAnchor(progressBar, 5.0);
		return pane;
	}

	private PropertyChangeListener getEventHandler() {
		PropertyChangeListener eventHandler = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Platform.runLater(() -> {
					switch (evt.getPropertyName()) {
						//Update the memory usage bar
						case MainModel.MEMORY_USAGE_PROPERTY:
							updateMemoryUsage((double)evt.getNewValue());
							break;
						// If a new audio file has been added
						case MainModel.AUDIO_ADDED_PROPERTY:
							AudioData[] addedData = (AudioData[]) evt.getNewValue();
							for(AudioData audio : addedData) {
								audioList.add(new AudioDataContainer(
										audio,
										new AudioController(MainModel.instance())));
							}													
							break;
						//Audio file has been removed
						case MainModel.AUDIO_REMOVED_PROPERTY:
							AudioData removedData = (AudioData) evt.getNewValue();
							removeAudioData(removedData);
							break;
						
						case MainModel.IS_WORKING_PROPERTY:
							progressBar.setVisible(true);
							break;
						
						case MainModel.PROGRESS_UPDATE_PROPERTY:
							progressBar.setProgress((double)evt.getNewValue());
							break;
							
						case MainModel.IS_IDLE_PROPERTY:
							progressBar.setVisible(false);
							break;
							
						case MainModel.ERROR_PROPERTY:
							LANRException e = (LANRException) evt.getNewValue();
							Utils.showErrorDialog(e.getMessage(), e.getCause().getMessage());
							break;
						}
				});
			}

		};
		return eventHandler;
	}

	private void removeAudioData(AudioData data) {
		TitledPane toBeRemoved = null;
		for (TitledPane pane : audioList) {
			if(((AudioDataContainer)pane).getData().equals(data)) {
				toBeRemoved = pane;
			}
		}
		audioList.remove(toBeRemoved);
	}
}
