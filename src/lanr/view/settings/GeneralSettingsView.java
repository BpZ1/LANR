package lanr.view.settings;

import java.io.File;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lanr.model.Settings;
import lanr.view.Utils;

/**
 * @author Nicolas Bruch
 *
 */
public class GeneralSettingsView extends GridPane {

	private Window parent;
	private SimpleBooleanProperty visualizeData = new SimpleBooleanProperty(true);
	private SimpleDoubleProperty visualFactor = new SimpleDoubleProperty();
	private SimpleBooleanProperty createLog = new SimpleBooleanProperty(true);
	private SimpleStringProperty path = new SimpleStringProperty();
	
	public GeneralSettingsView(SimpleBooleanProperty changed, Window parent) {
		this.parent = parent;
		this.setPadding(new Insets(8, 8, 8, 8));
		this.setHgap(10);
		this.setVgap(10);
		this.setAlignment(Pos.TOP_CENTER);
		createVisualizeDataControl();
		createViszalisationFactorControl();
		createLogCreationBox();
		createLogPathSelecter();
		visualizeData.addListener((obs, oldval, newVal) -> changed.setValue(true));
		visualFactor.addListener((obs, oldval, newVal) -> changed.setValue(true));
		createLog.addListener((obs, oldval, newVal) -> changed.setValue(true));
		path.addListener((obs, oldval, newVal) -> changed.setValue(true));
	}
	
	private void createVisualizeDataControl() {
		Label label = new Label("Visualize data");
		CheckBox box = new CheckBox();
		box.setSelected((boolean)Settings.getInstance()
				.getPropertyValue(Settings.SHOW_VISUAL_PROPERTY_NAME));
		visualizeData.bind(box.selectedProperty());
		this.add(label, 0, 0);
		this.add(box, 1, 0);
	}
	
	private void createViszalisationFactorControl() {
		Label label = new Label("Visualization Reduction Factor");
		Slider slider = new Slider();
		Label sliderValue = new Label();
		slider.valueProperty().addListener((obs, oldVal, newVal) ->
				sliderValue.setText(String.valueOf(Utils.round((double)newVal, 3)))
				);
		visualFactor.bind(slider.valueProperty());
		slider.setValue((double)Settings.getInstance()
				.getPropertyValue(Settings.VISUALIZATION_FACTOR_PROPERTY_NAME));
		this.add(label, 0, 1);
		this.add(slider, 1, 1);
		this.add(sliderValue, 2, 1);
	}
	
	private void createLogCreationBox() {
		CheckBox box = new CheckBox("Create Log");	
		box.setSelected((boolean)Settings.getInstance()
				.getPropertyValue(Settings.CREATE_LOG_PROPERTY_NAME));
		createLog.bind(box.selectedProperty());
		this.add(box, 0, 2);
	}
	
	private void createLogPathSelecter() {
		TextField text = new TextField();	
		text.setEditable(false);
		File initialPath = new File((String)Settings.getInstance()
				.getPropertyValue(Settings.LOG_PATH_PROPERTY_NAME));
		text.setText(initialPath.getAbsolutePath());
		path.bind(text.textProperty());
		
		Button button = new Button("Change");
		button.setOnAction(event ->{
			String selectedPath = Utils.showDirectoryDirectorySelect("Choose output directory", parent);
			if(selectedPath != null) {
				text.setText(selectedPath);
			}
		});
		
		Label label = new Label("Output path:");
		this.add(label, 0, 3, 3, 1);
		this.add(text, 0, 4, 2, 1);
		this.add(button, 2, 4);
	}
	
	public boolean getVisualizeData() {
		return visualizeData.getValue();
	}
	
	public double getVisualizationFactor() {
		return visualFactor.getValue();
	}
	
	public String getPath() {
		return path.getValue();
	}
	
	public boolean getCreateLog() {
		return createLog.getValue();
	}
}
