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
 * Type of noise found in an audio stream.
 *         
 * @author Nicolas Bruch
 * 
 */
public enum NoiseType {	
	/**
	 * Occurrence of disturbing frequency.
	 */
	Hum("Continuous low frequency noise (under 400 Hz)."),
	/**
	 * Samples exceed the limit of their recording level.
	 */
	Clipping("Samples exeeding the limit of their recording device."
			+ " Causes distortion in the audio."),
	/**
	 * Longer silent area in the audio stream.
	 */
	Silence("Longer silent area in the audio stream."),
	/**
	 * Difference in volume in the audio stream.
	 */
	Volume("Very loud or quiet parts in the audio stream."),
	/**
	 * Impulsive noise.
	 */
	Background("Unwanted sounds over 400 Hz.");
	
	private final String definition;
	
	private NoiseType(String definition) {
		this.definition = definition;
	}
	
	public String getDefinition() {
		return this.definition;
	}
}
