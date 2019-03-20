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

package lanr.logic.frequency;

/**
 * Different converter for samples into frequency domain.
 * 
 * @author Nicolas Bruch
 */
public enum FrequencyConversion {
	/**
	 * Discrete Consine Transform
	 */
	DCT(new DiscreteCosineTransformer(), false),
	/**
	 * Fast Fourier Transform
	 */
	FFT(new FastFourierTransformer(), true),
	/**
	 * Fast Wavelet Transform
	 */
	FWT(new FastWaveletTransformer(), true);

	private final SampleConverter converter;
	private boolean halfSamples;

	private FrequencyConversion(SampleConverter converter, boolean halfSamples) {
		this.converter = converter;
		this.halfSamples = halfSamples;
	}

	public SampleConverter getConverter() {
		return converter;
	}

	public boolean getHalfSamples() {
		return halfSamples;
	}
}
