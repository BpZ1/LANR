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

import net.sourceforge.jdistlib.math.Bessel;

public class KaiserWindow extends WindowFunctionImpl {

	private final double[] scalars;
	private final int length;
	private final double alpha = 5.0;
	
	public KaiserWindow(int windowSize) {
		this.length = windowSize;
		this.scalars = new double[length];
		for(int i = 0; i < length; i++) {
			double z = ((2 * i) / length) - 1;
			double numerator = Bessel.i(
					Math.PI * alpha * Math.sqrt(1 - Math.pow(z, 2)),
					0, false);
			double denominator = Bessel.i(Math.PI * alpha, 0, false);
			scalars[i] = numerator / denominator;
		}
	}

	@Override
	public void apply(double[] samples) {
		if (samples.length != scalars.length) {
			throw new IllegalArgumentException(
					"Invalid length. Was " + samples.length + " but should be " + scalars.length);
		}
		for(int i = 0; i < samples.length; i++) {
			samples[i] *= scalars[i];
		}
	}
	
	
}
