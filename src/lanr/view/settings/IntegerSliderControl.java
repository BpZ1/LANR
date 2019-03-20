package lanr.view.settings;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * 
 * Slider control for integer values.
 * 
 * @author Nicolas Bruch
 *
 */
public class IntegerSliderControl extends SliderControl<Integer> {
	
	public IntegerSliderControl(String name, int min, int max, int initialValue,
			String description, SimpleBooleanProperty changedProperty) {
		super(name, min, max, initialValue, description, changedProperty);

		//Text is updated if the value changes
		value.addListener((obs, oldval, newVal) -> valueLabel.setText(String.valueOf(newVal.intValue())));
		//Value is updated if the slider changes
		slider.valueProperty().addListener((obs, oldval, newVal) -> value.setValue(newVal.intValue()));
	}
}
