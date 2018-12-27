package lanr.view;

import java.io.IOException;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lanr.model.Settings;

/**
 * @author Nicolas Bruch
 *
 */
public class SettingsView extends Stage {
	
	private SimpleBooleanProperty changed = new SimpleBooleanProperty(false);
	private ParameterSettingsView parameterView;
	private GeneralSettingsView generalView;
	private SpectrogramSettingsView spectroView;
	
	public SettingsView() {
		Pane root = createRootPane();
		this.setTitle("Settings");
		Scene scene = new Scene(root, 450, 450);
		this.setScene(scene);
		scene.getStylesheets().add(MainView.class.getResource("Main.css").toExternalForm());
	}
	
	private Pane createRootPane() {
		BorderPane mainPane = new BorderPane();
		TabPane pane = new TabPane();
		pane.setSide(Side.LEFT);
		pane.setTabDragPolicy(TabDragPolicy.FIXED);
		//Performance tab
		Tab performanceTab = new Tab("General");
		performanceTab.setClosable(false);
		generalView = new GeneralSettingsView(changed);
		performanceTab.setContent(generalView);		
		//Parameter tab
		Tab parametersTab = new Tab("Parameter");
		parametersTab.setClosable(false);
		this.parameterView = new ParameterSettingsView(changed);
		parametersTab.setContent(parameterView);
		
		//Parameter tab
		Tab spectroTab = new Tab("Spectrogram");
		spectroTab.setClosable(false);
		this.spectroView = new SpectrogramSettingsView(changed);
		spectroTab.setContent(spectroView);
		
		pane.getTabs().add(performanceTab);
		pane.getTabs().add(parametersTab);
		pane.getTabs().add(spectroTab);
		//Buttons
		Button applyButton = new Button("Apply");
		applyButton.setOnAction(event ->{
			saveSettings();
			this.changed.setValue(false);
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event ->{
			if(changed.getValue()) {
				if(Utils.confirmationDialog(
						"Unsaved changes",
						"Do you still want to close?",
						"If yes the changes will be reverted.")) {
					this.close();
				}
			}else {
				this.close();
			}
		});
		GridPane buttonPane = new GridPane();
		buttonPane.setAlignment(Pos.CENTER_RIGHT);
		buttonPane.setHgap(10);
		buttonPane.setPadding(new Insets(4, 4, 4, 4));
		buttonPane.add(applyButton, 0, 0);
		buttonPane.add(cancelButton, 1, 0);
		mainPane.setCenter(pane);
		mainPane.setBottom(buttonPane);
		return mainPane;
	}
	
	private void saveSettings() {
		Settings s = Settings.getInstance();
		s.setConversionMethod(parameterView.getConversionMethod());
		s.setUsingWindowFunction(parameterView.getWindowFunctionSetting());
		s.setWindowSize(parameterView.getWindowSize());
		s.setConversionMethod(parameterView.getConversionMethod());
		s.setShowVisualisation(generalView.getVisualizeData());
		s.setVisualisationFactor(generalView.getVisualizationFactor());
		s.setCreateSpectrogram(spectroView.getCreateSpectrogram());
		s.setSpectrogramContrast(spectroView.getContrast());
		try {
			s.save();
		} catch (IOException e) {
			Utils.showErrorDialog("Could not save settings", e.getMessage());
		}
	}
}
