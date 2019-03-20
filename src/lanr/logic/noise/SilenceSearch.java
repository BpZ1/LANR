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
import lanr.logic.Utils;
import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

/**
 * Searches for parts of the signal that are
 * under a specified threshold for a certain duration.
 * 
 * @author Nicolas Bruch
 *
 */
public class SilenceSearch extends NoiseSearch {

	private static float userThreshold = 0;
	/**
	 * Number of seconds a silence has to last before it is
	 * recognized as such.
	 */
	private static float minimalDuration = 3;
	/**
	 * Threshold of the audio signal under which it is
	 * recognized as silence.
	 */
	private static double threshold = -40 + userThreshold;
	/**
	 * Maximum number of samples that can be over the threshold 
	 * in a silence window.
	 */
	private final int maxSkip = sampleRate / 500;
		
	private long sampleCounter = 0;
	/**
	 * Counts the silence samples
	 */
	private long counter = 0;
	/**
	 * Number of times a value over the threshold is ignored.
	 */
	private int skipCounter = 0;
	
	
	private FastTable<Noise> foundNoise = new FastTable<Noise>();
	private Noise currentNoise;
	
	public SilenceSearch(int sampleRate, int windowSize, double replayGain) {
		super(sampleRate, windowSize, replayGain);
	}
	
	@Override
	public void search(double[] samples) {
		long minimalDurationSamples = (long) (minimalDuration * (float)sampleRate);
		for(double s : samples) {
			sampleCounter++;
			//Check if the sample is under the threshold
			double dbValue = Utils.sampleToDBFS(s) + replayGain;
			if(dbValue < threshold) {
				counter++;
				//Check if the sample
				if(counter >= minimalDurationSamples) {
					if(currentNoise != null) {
						currentNoise.setLength(currentNoise.getLength() + 1);
					}else {
						currentNoise = new Noise(NoiseType.Silence,
								sampleCounter - minimalDurationSamples,
								minimalDurationSamples);
					}
				}				
			}else {
				/*
				 * If a sample over the threshold was found increment the skip counter
				 * If the skip counter is over the allowed number of skips
				 * start a new search by resetting the counter. If a noise was already
				 * created it will be added to the list of found noises.
				 */
				skipCounter++;
				if(skipCounter > maxSkip) {
					counter = 0;
					skipCounter = 0;
					if(currentNoise != null) {
						foundNoise.add(currentNoise);
						currentNoise = null;
					}
				}
			}
		}
	}

	@Override
	public FastTable<Noise> getNoise() {
		return foundNoise;
	}

	@Override
	public void compact() {
		if(currentNoise != null) {
			foundNoise.add(currentNoise);
		}
		//Noises that are up to 3 seconds apart will still be counted as one.
		foundNoise = combineNoises(foundNoise, sampleRate * 3);		
	}
	
	public static void setThreshold(int value) {
		userThreshold = value;
	}
	
	public static void setLength(float value) {
		minimalDuration = value;
	}

}
