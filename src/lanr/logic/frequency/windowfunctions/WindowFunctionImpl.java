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
 * The window function is used to reduce
 * the spectral leakage in the frequency data
 * that is created after using this function.
 * 
 * @author Nicolas Bruch
 *
 */
public abstract class WindowFunctionImpl {

	protected static final double PI = Math.PI;
	
	/**
	 * Applies the function on the given samples.
	 * @param samples - Samples of a signal.
	 */
	public abstract void apply(double[] samples);
}
