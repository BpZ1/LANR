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

import javolution.util.FastTable;
import lanr.logic.model.FixedList;
import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

/**
 * 
 * Noise search algorithm that calculates the average dBFS values
 * of the last x frames in a given frequency range.
 * 
 * @author Nicolas Bruch
 *
 */
public class BackgroundNoiseSearch extends FrequencySearch {

	private static int volumeThreshold = -40;
	private static double frequencyThreshold = 1000;
	/**
	 * Number of frames over which the signal is analysed
	 */
	private int analysisFrames;
	/**
	 * Duration over which signal is averaged in seconds.
	 */
	private double duration = 0.5;
	
	/**
	 * Values of previous frames.
	 */
	private FastTable<FixedList<Double>> values;
	/**
	 * Current frame index.
	 */
	private int frameIndex = 0;
	
	private FastTable<Noise> foundNoise = new FastTable<Noise>();
	
	public BackgroundNoiseSearch(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain, mirrored);
		
		analysisFrames = (int) (duration * sampleRate / windowSize);
		values = new FastTable<FixedList<Double>>();
		
		for(int i = 0; i < windowSize; i++) {
			values.add(new FixedList<Double>(analysisFrames));
		}
	}

	@Override
	public void search(double[] bins) {
		
		boolean noiseInFrame = false;
		for(int i = 0; i < bins.length; i++) {
			FixedList<Double> frequencyValues = values.get(i);
			frequencyValues.add(bins[i]);
			if(frequencyValues.size() == analysisFrames 
					&& frequencies.get(i) > frequencyThreshold) {
				double average = 0;
				for(double d : frequencyValues.getAll()) {
					average += d;
				}
				average /= frequencyValues.size();	
				if(average > volumeThreshold) {
					noiseInFrame = true;					
				}
			}
		}
		if(noiseInFrame) {
			foundNoise.add(new Noise(NoiseType.Background, (
					frameIndex - analysisFrames) * windowSize,
					analysisFrames));
		}
		frameIndex++;
	}

	@Override
	public FastTable<Noise> getNoise() {
		return foundNoise;
	}

	@Override
	public void compact() {
		foundNoise = combineNoises(foundNoise, sampleRate * 3);
	}

}
