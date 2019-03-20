package lanr.view.settings;

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
import lanr.view.MainView;
import lanr.view.Utils;

/**
 * @author Nicolas Bruch
 *
 */
public class SettingsView extends Stage {
	
	private SimpleBooleanProperty changed = new SimpleBooleanProperty(false);
	private AnalysisSettingsView analysisView;
	private GeneralSettingsView generalView;
	private SpectrogramSettingsView spectroView;
	private ParameterSettingsView parameterView;
	
	public SettingsView() {
		Pane root = createRootPane();
		this.setTitle("Settings");
		Scene scene = new Scene(root, 450, 450);		
		this.setScene(scene);
		this.setMinWidth(450);
		this.setMinHeight(450);
		this.setMaxHeight(450);
		this.setMaxWidth(450);
		this.setWidth(450);
		this.setHeight(450);
		this.setResizable(false);
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
		generalView = new GeneralSettingsView(changed,
				Stage.getWindows().get(0));
		performanceTab.setContent(generalView);		
		//Parameter tab
		Tab analysisTab = new Tab("Analysis");
		analysisTab.setClosable(false);
		this.analysisView = new AnalysisSettingsView(changed);
		analysisTab.setContent(analysisView);
		
		//Spectrogram tab
		Tab spectroTab = new Tab("Spectrogram");
		spectroTab.setClosable(false);
		this.spectroView = new SpectrogramSettingsView(changed,
				Stage.getWindows().get(0));
		spectroTab.setContent(spectroView);
		
		//Parameter Tab
		Tab parameterTab = new Tab("Parameter");
		parameterTab.setClosable(false);
		this.parameterView = new ParameterSettingsView(changed);
		parameterTab.setContent(parameterView);
		
		pane.getTabs().add(performanceTab);
		pane.getTabs().add(analysisTab);
		pane.getTabs().add(spectroTab);
		pane.getTabs().add(parameterTab);
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
	
	/**
	 * Saves the data of all sub nodes into the {@link Settings} instance.
	 */
	private void saveSettings() {
		Settings s = Settings.getInstance();
		s.setConversionMethod(analysisView.getConversionMethod());
		s.setWindowFunction(analysisView.getWindowFunction());
		s.setWindowSize(analysisView.getWindowSize());
		s.setConversionMethod(analysisView.getConversionMethod());
		s.setShowVisualisation(generalView.getVisualizeData());
		s.setVisualisationFactor(generalView.getVisualizationFactor());
		s.setLogCreation(generalView.getCreateLog());
		s.setLogPath(generalView.getPath());
		s.setCreateSpectrogram(spectroView.getCreateSpectrogram());
		s.setSpectrogramContrast(spectroView.getContrast());
		s.setSpectrogramPath(spectroView.getPath());
		s.setClippingWeight(parameterView.getClippingWeight());
		s.setVolumeWeight(parameterView.getVolumeWeight());
		s.setSilenceWeight(parameterView.getSilenceWeight());
		s.setHummingWeight(parameterView.getHummingWeight());
		s.setClippingThreshold(parameterView.getClippingThreshold());
		s.setSilenceThreshold(parameterView.getSilenceThreshold());
		s.setVolumeThreshold(parameterView.getVolumeThreshold());
		s.setHummingThreshold(parameterView.getHummingThreshold());
		s.setHummingLength(parameterView.getHummingLength());
		s.setSilenceLength(parameterView.getSilenceLength());
		s.setVolumeLength(parameterView.getVolumeLength());
		try {
			s.save();
		} catch (IOException e) {
			Utils.showErrorDialog("Could not save settings", e.getMessage());
		}
	}
}
