package lanr.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import lanr.controller.MainViewController;
import lanr.logic.model.AudioData;
import lanr.model.MainModel;

public class MainView extends Stage {

	private MainViewController controller;
	private Pane rootPane;
	private Accordion centerPane;
	private ProgressBar progressBar;
	private ObservableList<TitledPane> audioList;

	public MainView(MainModel model, MainViewController controller) {
		// Add event handler
		model.addChangeListener(getEventHandler());
		this.controller = controller;
		this.setTitle("LANR");
		rootPane = createRootPane();
		Scene scene = new Scene(rootPane, 800, 500);
		scene.getStylesheets().add(MainView.class.getResource("Main.css").toExternalForm());
		this.setScene(scene);
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

	/**
	 * Contains the Menu bar
	 * 
	 * @return
	 */
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
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Video Files", "*.mp4", "*.avi"),
				new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"), new ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(this);
		if (selectedFile != null) {
			controller.addFile(selectedFile.getAbsolutePath());
		}
	}

	public void showInfoDialog(String header, String message) {
		if (header == null || message == null) {
			throw new IllegalArgumentException("Cant show a message with without arguments");
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(header);
		alert.setContentText(message);

		alert.showAndWait();
	}

	public void showErrorDialog(String header, String message) {
		if (header == null || message == null) {
			throw new IllegalArgumentException("Cant show a message with without arguments");
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(message);

		alert.showAndWait();
	}

	private Accordion createCenterPane() {
		Accordion center = new Accordion();
		audioList = center.getPanes();
		return center;
	}

	private Pane createBottomPane() {
		AnchorPane pane = new AnchorPane();
		this.progressBar = new ProgressBar();
		progressBar.setVisible(false);
		pane.getChildren().add(progressBar);
		AnchorPane.setRightAnchor(progressBar, 5.0);
		return pane;
	}

	private PropertyChangeListener getEventHandler() {
		PropertyChangeListener eventHandler = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Platform.runLater(() -> {
					switch (evt.getPropertyName()) {
					// If a new audio file has been added
					case MainModel.AUDIO_ADDED_PROPERTY:
						if (evt.getNewValue() instanceof AudioData) {
							AudioData data = (AudioData) evt.getNewValue();
							audioList.add(new AudioDataContainer(data));
						}
						break;

					case MainModel.AUDIO_REMOVED_PROPERTY:
						if (evt.getNewValue() instanceof AudioData) {
							AudioData data = (AudioData) evt.getNewValue();
							removeAudioData(data);
						}
						break;

					case MainModel.PROGRESS_UPDATE_PROPERTY:
						if (evt.getNewValue() instanceof Integer) {
							progressBar.setProgress((int) evt.getNewValue());
							System.out.println((int) evt.getNewValue());
						}else if(evt.getNewValue() instanceof Boolean) {
							progressBar.setVisible((boolean)evt.getNewValue());
						}
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
			if (pane.getText().equals(data.getPath())) {
				toBeRemoved = pane;
			}
		}
		audioList.remove(toBeRemoved);
	}
}
