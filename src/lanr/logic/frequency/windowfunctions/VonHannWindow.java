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
 * 
 * Implementation of the Von-Hann Window function.
 * 
 * @author Nicolas Bruch
 *
 */
public class VonHannWindow extends WindowFunctionImpl {	
	
	private final double[] scalars;
	
	public VonHannWindow(int windowSize) {
		scalars = new double[windowSize];
		for (int n = 0; n < windowSize; n++) {
			scalars[n] = 0.5 * (1.0 - Math.cos((2.0 * PI * n) / (double)(windowSize - 1)));
		}
	}

	@Override
	public void apply(double[] samples) {		
		if (samples.length != scalars.length) {
			throw new IllegalArgumentException(
					"Invalid length. Was " + samples.length + " but should be " + scalars.length);
		}
		for (int i = 0; i < samples.length; i++) {
			samples[i] *= scalars[i];
		}	
	}
}
