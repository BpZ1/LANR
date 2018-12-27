package lanr.view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import lanr.model.Settings;

/**
 * @author Nicolas Bruch
 *
 */
public class GeneralSettingsView extends GridPane {

	private SimpleBooleanProperty visualizeData = new SimpleBooleanProperty(true);
	private SimpleDoubleProperty visualFactor = new SimpleDoubleProperty();
	
	public GeneralSettingsView(SimpleBooleanProperty changed) {
		this.setPadding(new Insets(8, 8, 8, 8));
		this.setHgap(10);
		this.setVgap(10);
		this.setAlignment(Pos.TOP_CENTER);
		generateVisualizeDataControl();
		generateViszalisationFactorControl();
		visualizeData.addListener((obs, oldval, newVal) -> changed.setValue(true));
		visualFactor.addListener((obs, oldval, newVal) -> changed.setValue(true));
	}
	
	private void generateVisualizeDataControl() {
		Label label = new Label("Visualize data");
		CheckBox box = new CheckBox();
		box.setSelected(Settings.getInstance().showVisualisation());
		visualizeData.bind(box.selectedProperty());
		this.add(label, 0, 0);
		this.add(box, 1, 0);
	}
	
	private void generateViszalisationFactorControl() {
		Label label = new Label("Visualization Reduction Factor");
		Slider slider = new Slider();
		Label sliderValue = new Label();
		slider.valueProperty().addListener((obs, oldVal, newVal) ->
				sliderValue.setText(String.valueOf(Utils.round((double)newVal, 3)))
				);
		visualFactor.bind(slider.valueProperty());
		slider.setValue(Settings.getInstance().getVisualisationFactor());
		this.add(label, 0, 1);
		this.add(slider, 1, 1);
		this.add(sliderValue, 2, 1);
	}
	
	public boolean getVisualizeData() {
		return visualizeData.getValue();
	}
	
	public double getVisualizationFactor() {
		return visualFactor.getValue();
	}
}
