/*
 * LANR (Lecture Audio Noise Recognition) is a software that strives to automate
 * the reviewing process of lecture recordings at the WIAI faculty of the University of Bamberg.
 *
 * Copyright (C) 2019 Nicolas Bruch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
