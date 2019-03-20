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

package lanr.logic.noise;

import java.util.LinkedList;
import java.util.List;

public abstract class FrequencySearch extends NoiseSearch {

	/**
	 * List containing all frequencies that are represented by the bins.
	 */
	protected final List<Double> frequencies = new LinkedList<Double>();
	
	public FrequencySearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain);
		int size = windowSize;
		if(mirrored) size = (size / 2) + 1;
		for (int i = 0; i < size; i++) {
			frequencies.add(calculateFrequency(i));
		}
	}
	
	/**
	 * Calculates the frequency of a given index in a window.
	 * i * fs / N
	 * @param index
	 * @param sampleCount
	 * @return
	 */
	protected final double calculateFrequency(int index) {
		double freq = index * (double) sampleRate / (double) windowSize;
		return freq;
	}
	
	
}
