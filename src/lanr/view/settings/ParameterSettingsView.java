package lanr.view.settings;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;

public class ParameterSettingsView extends Pane {
	
	private SimpleObjectProperty<Float> clippingWeight = new SimpleObjectProperty<Float>();
	private SimpleObjectProperty<Float> volumeWeight = new SimpleObjectProperty<Float>();
	private SimpleObjectProperty<Float> silenceWeight = new SimpleObjectProperty<Float>();
	private SimpleObjectProperty<Float> hummingWeight = new SimpleObjectProperty<Float>();
	
	private SimpleObjectProperty<Float> clippingLength = new SimpleObjectProperty<Float>();
	private SimpleObjectProperty<Float> silenceLength = new SimpleObjectProperty<Float>();
	private SimpleObjectProperty<Float> volumeLength = new SimpleObjectProperty<Float>();
	
	private SimpleObjectProperty<Integer> clippingThreshold = new SimpleObjectProperty<Integer>();
	private SimpleObjectProperty<Integer> hummingThreshold = new SimpleObjectProperty<Integer>();
	private SimpleObjectProperty<Integer> volumeThreshold = new SimpleObjectProperty<Integer>();
	private SimpleObjectProperty<Integer> silenceThreshold = new SimpleObjectProperty<Integer>();
	
	public ParameterSettingsView(SimpleBooleanProperty changed) {
		this.setPadding(new Insets(8, 8, 8, 8));
		this.getChildren().add(createParameterControls());
		
		//Add observer value to track changes
		clippingWeight.addListener((obs, oldval, newVal) -> changed.setValue(true));
		volumeWeight.addListener((obs, oldval, newVal) -> changed.setValue(true));
		silenceWeight.addListener((obs, oldval, newVal) -> changed.setValue(true));
		hummingWeight.addListener((obs, oldval, newVal) -> changed.setValue(true));
		silenceLength.addListener((obs, oldval, newVal) -> changed.setValue(true));
		clippingLength.addListener((obs, oldval, newVal) -> changed.setValue(true));
		volumeLength.addListener((obs, oldval, newVal) -> changed.setValue(true));
		clippingThreshold.addListener((obs, oldval, newVal) -> changed.setValue(true));
		hummingThreshold.addListener((obs, oldval, newVal) -> changed.setValue(true));
		volumeThreshold.addListener((obs, oldval, newVal) -> changed.setValue(true));
		silenceThreshold.addListener((obs, oldval, newVal) -> changed.setValue(true));
	}
	
	private Accordion createParameterControls() {
		Accordion accordion = new Accordion();
		accordion.getPanes().add(createClippingControl());
		accordion.getPanes().add(createSilenceControl());
		accordion.getPanes().add(createVolumeControl());
		accordion.getPanes().add(createHummingControl());
		return accordion;
	}
	
	private TitledPane createClippingControl() {
		TitledPane pane = new TitledPane();
		pane.setText("Clipping");
		return pane;
	}
	private TitledPane createSilenceControl() {
		TitledPane pane = new TitledPane();
		pane.setText("Silence");
		return pane;
	}
	private TitledPane createVolumeControl() {
		TitledPane pane = new TitledPane();
		pane.setText("Clipping");
		return pane;
	}
	private TitledPane createHummingControl() {
		TitledPane pane = new TitledPane();
		pane.setText("Humming");
		return pane;
	}
	
}
