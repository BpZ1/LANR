package lanr.view.settings;

import javafx.beans.property.SimpleBooleanProperty;

public class FloatSliderControl extends SliderControl<Float> {
	
	public FloatSliderControl(String name, float min, float max, float initialValue,
			String description, SimpleBooleanProperty changedProperty) {
		super(name, min, max, initialValue, description, changedProperty);

		//Text is updated if the value changes
		value.addListener((obs, oldval, newVal) -> valueLabel.setText(String.valueOf(newVal.floatValue())));
		//Value is updated if the slider changes
		slider.valueProperty().addListener((obs, oldval, newVal) -> value.setValue(newVal.floatValue()));
	}
}
