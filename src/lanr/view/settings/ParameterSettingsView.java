package lanr.view.settings;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lanr.model.Settings;
import lanr.view.Descriptions;

public class ParameterSettingsView extends Pane {

	private IntegerSliderControl clippingThresholdControl;
	private FloatSliderControl clippingWeightControl;

	private IntegerSliderControl volumeThresholdControl;
	private FloatSliderControl volumeWeightControl;
	private FloatSliderControl volumeLengthControl;

	private IntegerSliderControl silenceThresholdControl;
	private FloatSliderControl silenceWeightControl;
	private FloatSliderControl silenceLengthControl;

	private IntegerSliderControl hummingThresholdControl;
	private FloatSliderControl hummingWeightControl;
	private FloatSliderControl hummingLengthControl;

	public ParameterSettingsView(SimpleBooleanProperty changed) {
		this.setPadding(new Insets(8, 8, 8, 8));
		this.getChildren().add(createParameterControls(changed));
	}

	private Accordion createParameterControls(SimpleBooleanProperty changed) {
		Accordion accordion = new Accordion();
		accordion.getPanes().add(createClippingControl(changed));
		accordion.getPanes().add(createSilenceControl(changed));
		accordion.getPanes().add(createVolumeControl(changed));
		accordion.getPanes().add(createHummingControl(changed));
		accordion.setPrefWidth(400);
		return accordion;
	}

	private TitledPane createClippingControl(SimpleBooleanProperty changed) {
		TitledPane pane = new TitledPane();
		pane.setText("Clipping");
		// VBox containing the different controls
		VBox content = new VBox();
		content.setAlignment(Pos.CENTER);
		content.setSpacing(4);
		pane.setContent(content);

		clippingWeightControl = new FloatSliderControl("Weight: ",
				0, 10, (float) Settings.getInstance()
				.getPropertyValue(Settings.CLIPPING_WEIGHT_PROPERTY_NAME),
				Descriptions.CLIPPING_WEIGHT_DESCRIPTION,
				changed);
		content.getChildren().add(clippingWeightControl);

		clippingThresholdControl = new IntegerSliderControl("Threshold: ", -10, 10,
				(int) Settings.getInstance()
				.getPropertyValue(Settings.CLIPPING_THRESHOLD_PROPERTY_NAME), 
				Descriptions.CLIPPING_THRESHOLD_DESCRIPTION, changed);
		content.getChildren().add(clippingThresholdControl);

		return pane;
	}

	private TitledPane createSilenceControl(SimpleBooleanProperty changed) {
		TitledPane pane = new TitledPane();
		pane.setText("Silence");
		// VBox containing the different controls
		VBox content = new VBox();
		content.setAlignment(Pos.CENTER);
		content.setSpacing(4);
		pane.setContent(content);

		silenceWeightControl = new FloatSliderControl("Weight: ", 0,
				10, (float) Settings.getInstance()
				.getPropertyValue(Settings.CLIPPING_WEIGHT_PROPERTY_NAME),
				Descriptions.CLIPPING_WEIGHT_DESCRIPTION,
				changed);
		content.getChildren().add(silenceWeightControl);

		silenceThresholdControl = new IntegerSliderControl("Threshold: ", -10, 10,
				(int) Settings.getInstance()
				.getPropertyValue(Settings.SILENCE_THRESHOLD_PROPERTY_NAME),
				Descriptions.SILENCE_THRESHOLD_DESCRIPTION, changed);
		content.getChildren().add(silenceThresholdControl);
		
		silenceLengthControl = new FloatSliderControl("Length: ", 0,
				20, (float) Settings.getInstance()
				.getPropertyValue(Settings.SILENCE_LENGTH_PROPERTY_NAME),
				Descriptions.SILENCE_LENGTH_DESCRIPTION,
				changed);
		content.getChildren().add(silenceLengthControl);
		
		return pane;
	}

	private TitledPane createVolumeControl(SimpleBooleanProperty changed) {
		TitledPane pane = new TitledPane();
		pane.setText("Volume");
		// VBox containing the different controls
		VBox content = new VBox();
		content.setAlignment(Pos.CENTER);
		content.setSpacing(4);
		pane.setContent(content);

		volumeWeightControl = new FloatSliderControl("Weight: ", 0,
				10, (float) Settings.getInstance()
				.getPropertyValue(Settings.CLIPPING_WEIGHT_PROPERTY_NAME),
				Descriptions.CLIPPING_WEIGHT_DESCRIPTION,
				changed);
		content.getChildren().add(volumeWeightControl);

		volumeThresholdControl = new IntegerSliderControl("Threshold: ", -10, 10,
				(int) Settings.getInstance()
				.getPropertyValue(Settings.SILENCE_THRESHOLD_PROPERTY_NAME),
				Descriptions.SILENCE_THRESHOLD_DESCRIPTION, changed);
		content.getChildren().add(volumeThresholdControl);
				
		volumeLengthControl = new FloatSliderControl("Length: ", 0,
				20, (float) Settings.getInstance()
				.getPropertyValue(Settings.SILENCE_LENGTH_PROPERTY_NAME),
				Descriptions.SILENCE_LENGTH_DESCRIPTION,
				changed);
		content.getChildren().add(volumeLengthControl);
		return pane;
	}

	private TitledPane createHummingControl(SimpleBooleanProperty changed) {
		TitledPane pane = new TitledPane();
		pane.setText("Humming");
		// VBox containing the different controls
		VBox content = new VBox();
		content.setAlignment(Pos.CENTER);
		content.setSpacing(4);
		pane.setContent(content);

		hummingWeightControl = new FloatSliderControl("Weight: ", 0,
				10, (float) Settings.getInstance()
				.getPropertyValue(Settings.CLIPPING_WEIGHT_PROPERTY_NAME),
				Descriptions.CLIPPING_WEIGHT_DESCRIPTION,
				changed);
		content.getChildren().add(hummingWeightControl);

		hummingThresholdControl = new IntegerSliderControl("Threshold: ", -10, 10,
				(int) Settings.getInstance()
				.getPropertyValue(Settings.SILENCE_THRESHOLD_PROPERTY_NAME),
				Descriptions.SILENCE_THRESHOLD_DESCRIPTION, changed);
		content.getChildren().add(hummingThresholdControl);
						
		hummingLengthControl = new FloatSliderControl("Length: ", 0,
				20, (float) Settings.getInstance()
				.getPropertyValue(Settings.SILENCE_LENGTH_PROPERTY_NAME),
				Descriptions.SILENCE_LENGTH_DESCRIPTION,
				changed);
		content.getChildren().add(hummingLengthControl);
		return pane;
	}

	public float getClippingWeight() {
		return clippingWeightControl.getValue();
	}

	public float getVolumeWeight() {
		return volumeWeightControl.getValue();
	}

	public float getHummingWeight() {
		return hummingWeightControl.getValue();
	}

	public float getSilenceWeight() {
		return silenceWeightControl.getValue();
	}

	public float getHummingLength() {
		return hummingLengthControl.getValue();
	}

	public float getSilenceLength() {
		return silenceLengthControl.getValue();
	}

	public float getVolumeLength() {
		return volumeLengthControl.getValue();
	}

	public int getHummingThreshold() {
		return hummingThresholdControl.getValue();
	}

	public int getVolumeThreshold() {
		return volumeThresholdControl.getValue();
	}

	public int getSilenceThreshold() {
		return silenceThresholdControl.getValue();
	}

	public int getClippingThreshold() {
		return clippingThresholdControl.getValue();
	}
}
