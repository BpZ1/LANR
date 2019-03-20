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

import lanr.logic.model.Noise;

/**
 * Search method for recognizing and creating {@link Noise} in a given sample
 * array.
 * 
 * @author Nicolas Bruch
 *
 */
public abstract class NoiseSearch {

	/**
	 * Weighting factor
	 */
	protected static float weight = 1;
	/**
	 * Samples rate of the signal to be analyzed.
	 */
	protected final int sampleRate;
	/**
	 * Size of the data arrays that will be given for analysis.
	 */
	protected final int windowSize;
	
	/**
	 * Replay gain represents the overall volume of the file.
	 */
	protected final double replayGain;

	public NoiseSearch(int sampleRate, int windowSize, double replayGain) {
		this.sampleRate = sampleRate;
		this.windowSize = windowSize;
		this.replayGain = replayGain;
	}

	/**
	 * Searches for {@link Noise} in the given samples. The values of the samples
	 * should be between -1 and 1 if the given data is not in frequency domain.
	 * 
	 * @param samples - Signal samples between -1 and 1, or frequency data.
	 */
	public abstract void search(double[] samples);


	protected abstract FastTable<Noise> getNoise();
	
	/**
	 * Returns the found {@link Noise}. <b>Should only be called after compact() was
	 * executed.</b>
	 * 
	 * @return List of found noises.
	 */
	public FastTable<Noise> getFoundNoise(){
		FastTable<Noise> noise = getNoise();
		for(Noise n : noise) {
			n.setSeverity(n.getLength() / sampleRate * weight);
		}
		return noise;
	}

	public abstract void compact();

	/**
	 * Combines all overlapping noises to one noise. If the end of the previous and
	 * the beginning of the current noise overlap, they will be combined to a single
	 * noise.
	 * 
	 * @param noise
	 * @param severity - Value that will be multiplied with the length of the noise.
	 *                 The length depends on the sample rate and should therefore be
	 *                 calculated into the value.
	 * @param distance - Extra length that will be added to every noise when
	 *                 combining. This will combine noise even if they are not
	 *                 overlapping if they are the given distance away from each
	 *                 other. Value is in samples.
	 * @return
	 */
	protected FastTable<Noise> combineNoises(FastTable<Noise> noise, long distance) {
		FastTable<Noise> noises = new FastTable<Noise>().addAll(noise);
		int numberOfNoises = 0;
		//Iterate to keep combining noises until the number stays the same.
		do {
			numberOfNoises = noises.size();
			noises = combine(noises, distance);
		}while(numberOfNoises != noises.size());
		return noises;
	}
	
	private final FastTable<Noise> combine(FastTable<Noise> noise, long distance){
		if (noise == null) {
			throw new IllegalArgumentException("The given noise must'nt be null");
		}
		if (noise.isEmpty()) {
			return new FastTable<Noise>();
		}
		for (int i = 0; i < noise.size(); i++) {
			if(i +1 < noise.size()) {
				Noise current = noise.get(i);
				current.setLength(current.getLength() + distance);
				if(current.add(noise.get(i +1))) {
					noise.remove(i + 1);
					i--;
				}else{
					current.setLength(current.getLength() - distance);
				};
			}
		}
		return noise;
	}
	
	public static void setWeight(float value) {
		weight = value;
	}
}
