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
 * Represents noise found in an audio signal.
 * 
 * @author Nicolas Bruch      
 *
 */
public class Noise {
	
	private double severity;	
	/**
	 * Start index of the noise in samples.
	 */
	private long location;
	/**
	 * Length of the noise in samples.
	 */
	private long length;
	/**
	 * Index of the channel in which the noise was found.
	 */
	private int channel;
	/**
	 * End index of the noise in samples.
	 */
	private long end;
	/**
	 * Noise type.
	 */
	private NoiseType type;

	public Noise(NoiseType type, long location, long length) {
		this.type = type;
		this.location = location;
		this.length = length;
		this.end = location + length;
	}
	
	/**
	 * Adds the length of the noises together and takes the higher severity.
	 * @param noise
	 */
	public boolean add(Noise noise) {
		if(noise == null) {
			throw new IllegalArgumentException("Can't add null to noise");
		}
		if(noise.getType() != type) {
			throw new IllegalArgumentException("Only noise of the same type can be added");
		}
		if(isOutside(noise)) {
			return false;
		}else if(isInside(noise)) {
			this.severity = Math.max(this.severity, noise.severity);
			return true;
		}else if(noise.isInside(this)){
			noise.add(this);
			this.location = noise.getLocation();
			this.length = noise.getLength();
			this.severity = Math.max(this.severity, noise.severity);
			return true;
		}else {
			//Overlapping bounds
			if(noise.getLocation() < end && noise.getEnd() > end) {
				//Overlapping on the right bound
				long additionalLength = noise.getEnd() - end;
				this.length += additionalLength;
				this.severity = Math.max(this.severity, noise.severity);
				return true;
			}else{
				//Overlapping left bound
				this.location = noise.getLocation();
				this.severity += noise.getSeverity();
				return true;
			}
		}
	}
	
	/**
	 * Checks if the given noise is inside
	 * of the time interval of the noise.
	 * @return True if the given noise is completely inside of this noise.
	 */
	public boolean isInside(Noise noise) {
		if(location <= noise.getLocation() && end >= noise.getEnd()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the given noise is not overlapping with the noise.
	 * @param noise
	 * @return True if they are not overlapping.
	 */
	public boolean isOutside(Noise noise) {
		if(noise.getLocation() > end || noise.getEnd() < location) {
			return true;
		}
		return false;
	}

	public double getSeverity() {
		return severity;
	}

	public void setSeverity(double severity) {
		this.severity = severity;
	}

	public long getLocation() {
		return location;
	}

	public void setLocation(long location) {
		this.location = location;
		this.end = location + length;
	}

	public NoiseType getType() {
		return type;
	}

	public void setType(NoiseType type) {
		this.type = type;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
		this.end = location + length;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	public long getEnd() {
		return end;
	}

}
