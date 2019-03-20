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
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

/**
 * Slider control for numeric types.
 * 
 * @author Nicolas Bruch
 *
 * @param <T>
 */
public abstract class SliderControl<T extends Number> extends GridPane {

	/**
	 * Value property.
	 */
	protected SimpleObjectProperty<T> value = new SimpleObjectProperty<T>();
	/**
	 * Slider control.
	 */
	protected Slider slider;
	/**
	 * Minimum of the slider.
	 */
	protected T min;
	/**
	 * Maximum of the slider.
	 */
	protected T max;
	/**
	 * Name label.
	 */
	protected Label nameLabel;
	/**
	 * Value label.
	 */
	protected Label valueLabel;
	
	public SliderControl(String name, T min, T max, T initialValue,
			String description, SimpleBooleanProperty changedProperty) {
		
		this.setPadding(new Insets(4,4,2,2));
		this.setHgap(4);
		this.setVgap(2);
		this.min = min;
		this.max = max;		
		nameLabel = new Label(name);
		valueLabel = new Label();
		slider = new Slider(); 
		slider.setValue(initialValue.doubleValue());
		value.set(initialValue);
		slider.setMin(min.doubleValue());
		slider.setMax(max.doubleValue());
		GridPane.setFillWidth(slider, true);
		//Add elements to the GridPane
		this.add(nameLabel, 0, 0);
		this.add(valueLabel, 1, 0);
		if(description != null) {
			this.add(new InfoButton(description), 2, 0);
		}		
		this.add(slider, 0, 1, 3 , 1);
		
		if(changedProperty != null) {
			value.addListener((obs, oldVal, newVal) -> changedProperty.setValue(true));
		}
	}
	
	/**
	 * Checks if the given value is inside the min and max range.
	 * @param value to be checked.
	 * @return
	 */
	public boolean isInRange(T value) {
		if(value.doubleValue() > max.doubleValue() && value.doubleValue() < min.doubleValue()) {
			return false;
		}
		return true;
	}
	
	public void setValue(T value) {
		if(value.doubleValue() > max.doubleValue() && value.doubleValue() < min.doubleValue()) {
			throw new IllegalArgumentException("Value is out of range for this control");
		}
		this.value.setValue(value);
		this.slider.setValue(value.doubleValue());
	}
	
	public void setMin(T min) {
		if(min.doubleValue() > this.max.doubleValue()) {
			throw new IllegalArgumentException("The minimum of the slider can't be greater than the maximum.");
		}
		this.min = min;
	}
	
	public void setMax(T max) {
		if(max.doubleValue() < this.min.doubleValue()) {
			throw new IllegalArgumentException("The maximum of the slider can't be smaller than the minimum.");
		}
		this.max = max;
	}
	
	public T getValue() {
		return value.getValue();
	}
}
