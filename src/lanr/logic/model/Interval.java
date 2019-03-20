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

package lanr.logic.model;

/**
 * @author Nicolas Bruch
 *
 */
public class Interval {

	private final double lowerBound;
	private final double upperBound;
	
	public Interval(double lower, double upper) {
		this.lowerBound = lower;
		this.upperBound = upper;
	}
	
	/**
	 * Checks if the given value is inside of the defined bounds.
	 * The bounds are checked including the upper bound excluding the lower.
	 * @param value
	 * @return
	 */
	public boolean contains(double value) {
		if(value > lowerBound && value <= upperBound) {
			return true;
		}
		return false;
	}
	
	public double getLowerBorder() {
		return lowerBound;
	}
	
	public double getUpperBorder() {
		return upperBound;
	}
}
