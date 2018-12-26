package lanr.view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import lanr.logic.frequency.FrequencyConversion;
import lanr.model.Settings;

/**
 * @author Nicolas Bruch
 * 
 *         Pane containing the controls for changes to the parameter.
 *
 */
public class ParameterSettingsView extends GridPane {

	private SimpleIntegerProperty windowSize = new SimpleIntegerProperty();
	private SimpleBooleanProperty windowFunctionSetting = new SimpleBooleanProperty();
	private SimpleObjectProperty<FrequencyConversion> conversionMethod =
			new SimpleObjectProperty<FrequencyConversion>();

	SimpleBooleanProperty changed;

	public ParameterSettingsView(SimpleBooleanProperty changed) {
		this.setPadding(new Insets(8, 8, 8, 8));
		this.setHgap(10);
		this.setVgap(10);
		this.setAlignment(Pos.TOP_CENTER);

		createWindowSizeControl();
		createWindowFunctionControl();
		createFrequencyTransformControl();

		windowSize.addListener((obs, oldval, newVal) -> changed.setValue(true));
		windowFunctionSetting.addListener((obs, oldval, newVal) -> changed.setValue(true));
		conversionMethod.addListener((obs, oldval, newVal) -> changed.setValue(true));
		this.changed = changed;
	}

	private void createWindowSizeControl() {
		Label windowValueLabel = new Label();
		windowSize.addListener((obs, oldval, newVal) -> 
			windowValueLabel.setText(String.valueOf(newVal.intValue())));
		windowSize.setValue(Settings.getInstance().getWindowSize());
		Label windowSliderLabel = new Label("Window size");
		Slider windowSizeSlider = new Slider();
		windowSizeSlider.setValue(Math.log(windowSize.getValue()) / Math.log(2));
		windowSizeSlider.setMin(7);
		windowSizeSlider.setMax(31);
		windowSizeSlider.valueProperty()
				.addListener((obs, oldval, newVal) -> windowSizeSlider.setValue(newVal.intValue()));
		windowSizeSlider.setOnMouseReleased(event -> {
			windowSize.setValue(Math.pow(2, windowSizeSlider.getValue()));
		});
		this.add(windowSliderLabel, 0, 0);
		this.add(windowSizeSlider, 1, 0);
		this.add(windowValueLabel, 2, 0);
	}

	private void createWindowFunctionControl() {
		Label windowFunctionLabel = new Label("Use Window function");
		CheckBox box = new CheckBox();
		box.selectedProperty().bindBidirectional(windowFunctionSetting);
		windowFunctionSetting.setValue(Settings.getInstance().isUsingWindowFunction());
		this.add(windowFunctionLabel, 0, 1);
		this.add(box, 1, 1);
	}

	private void createFrequencyTransformControl() {
		Label transformLabel = new Label("Transform");
		ComboBox<FrequencyConversion> box = new ComboBox<FrequencyConversion>();
		box.setItems(FXCollections.observableArrayList(FrequencyConversion.values()));
		conversionMethod.bind(box.getSelectionModel().selectedItemProperty());
		box.getSelectionModel().select(Settings.getInstance().getConversionMethod());
		this.add(transformLabel, 0, 2);
		this.add(box, 1, 2);
	}

	public int getWindowSize() {
		return windowSize.getValue();
	}

	public boolean getWindowFunctionSetting() {
		return windowFunctionSetting.getValue();
	}

	public FrequencyConversion getConversionMethod() {
		return conversionMethod.getValue();
	}
}
