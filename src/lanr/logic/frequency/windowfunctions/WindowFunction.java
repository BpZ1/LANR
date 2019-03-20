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

package lanr.logic.frequency.windowfunctions;

/**
 * Contains the different types of window funcions.
 * 
 * @author Nicolas Bruch
 *
 */
public enum WindowFunction {

	None(""),
	Hanning("Hanning"),
	Hamming("Hamming"),
	Kaiser("Kaiser");
	
	private String selected;
	
	private WindowFunction(String selection) {
		this.selected = selection;
	}
	
	/**
	 * @param windowSize - Size of the window.
	 * @return Implementation for the selected window.
	 */
	public WindowFunctionImpl getImplementation(int windowSize) {
		switch(this.selected) {
			case "Hanning":
				return new VonHannWindow(windowSize);
				
			case "Hamming":
				return new HammingWindow(windowSize);
				
			case "Kaiser":
				return new KaiserWindow(windowSize);
				
			default:
				return null;
		}	
	}
}
