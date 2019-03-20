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

package lanr.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

/**
 * @author Nicolas Bruch
 *
 */
public class Utils {

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Converts a sample into dBFS (Decibel Full Scale).<br>
	 * dbFS values go from negative infinity to 0.
	 * @param sample - Value to be converted.
	 * @return dBFS value.
	 */
	public static double sampleToDBFS(double sample) {
		return 20 * Math.log10(Math.abs(sample));
	}
	
	public static String getDurationString(long durationInSeconds) {
		LocalTime timeOfDay;
		if(durationInSeconds > 86399) {
			timeOfDay = LocalTime.ofSecondOfDay(86399);		
		}else {
			timeOfDay = LocalTime.ofSecondOfDay(durationInSeconds);			
		}
		String time = timeOfDay.toString();
		return time;
	}
	
	public static double getMax(double[] array) {
		double max = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < array.length; i++) {
			max = Math.max(max, array[i]);
		}
		return max;
	}
	
	public static double getMin(double[] array) {
		double min = Double.POSITIVE_INFINITY;
		for(int i = 0; i < array.length; i++) {
			min = Math.min(min, array[i]);
		}
		return min;
	}
}
