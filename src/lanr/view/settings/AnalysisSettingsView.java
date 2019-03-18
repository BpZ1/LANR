package lanr.view.settings;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import lanr.logic.frequency.FrequencyConversion;
import lanr.logic.frequency.windowfunctions.WindowFunction;
import lanr.model.Settings;
import lanr.view.Descriptions;

/**
 * Pane containing the controls for changes to the parameter.
 * 
 * @author Nicolas Bruch
 *
 */
public class AnalysisSettingsView extends GridPane {
	
	private SimpleIntegerProperty windowSize = new SimpleIntegerProperty();
	private SimpleObjectProperty<FrequencyConversion> conversionMethod = new SimpleObjectProperty<FrequencyConversion>();
	private SimpleObjectProperty<WindowFunction> windowFunction = new SimpleObjectProperty<WindowFunction>();

	public AnalysisSettingsView(SimpleBooleanProperty changed) {
		this.setPadding(new Insets(8, 8, 8, 8));
		this.setHgap(10);
		this.setVgap(10);
		this.setAlignment(Pos.TOP_CENTER);

		createWindowSizeControl();
		createWindowFunctionControl();
		createFrequencyTransformControl();

		windowSize.addListener((obs, oldval, newVal) -> changed.setValue(true));
		conversionMethod.addListener((obs, oldval, newVal) -> changed.setValue(true));
		windowFunction.addListener((obs, oldval, newVal) -> changed.setValue(true));
	}

	private void createWindowSizeControl() {
		Label windowValueLabel = new Label();
		windowSize.addListener((obs, oldval, newVal) -> windowValueLabel.setText(String.valueOf(newVal.intValue())));
		windowSize.setValue((int) Settings.getInstance()
				.getPropertyValue(Settings.WINDOW_SIZE_PROPERTY_NAME));
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
		this.add(new InfoButton(Descriptions.WINDOW_SIZE_DESCRIPTION), 3, 0);
	}

	private void createWindowFunctionControl() {
		Label windowFuncLabel = new Label("Transform");
		ComboBox<WindowFunction> box = new ComboBox<WindowFunction>();
		box.setItems(FXCollections.observableArrayList(WindowFunction.values()));
		windowFunction.bind(box.getSelectionModel().selectedItemProperty());
		box.getSelectionModel().select((WindowFunction)Settings.getInstance()
				.getPropertyValue(Settings.WINDOWFUNCTION_PROPERTY_NAME));
		this.add(windowFuncLabel, 0, 1);
		this.add(box, 1, 1);
		this.add(new InfoButton(Descriptions.WINDOW_FUNCTION_DESCRIPTION), 2, 1);
	}

	private void createFrequencyTransformControl() {
		Label transformLabel = new Label("Transform");
		ComboBox<FrequencyConversion> box = new ComboBox<FrequencyConversion>();
		box.setItems(FXCollections.observableArrayList(FrequencyConversion.values()));
		conversionMethod.bind(box.getSelectionModel().selectedItemProperty());
		box.getSelectionModel().select((FrequencyConversion)Settings.getInstance()
				.getPropertyValue(Settings.CONVERSION_METHOD_PROPERTY_NAME));
		this.add(transformLabel, 0, 2);
		this.add(box, 1, 2);
		this.add(new InfoButton(Descriptions.FREQUENCY_TRANSFORM_DESCRIPTION), 2, 2);
	}

	public int getWindowSize() {
		return windowSize.getValue();
	}

	public WindowFunction getWindowFunction() {
		return windowFunction.getValue();
	}

	public FrequencyConversion getConversionMethod() {
		return conversionMethod.getValue();
	}
}
