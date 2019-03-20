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

import org.jtransforms.fft.DoubleFFT_1D;

/**
 * @author Nicolas Bruch
 *
 */
public class FastFourierTransformer implements SampleConverter {
	
	@Override
	public double[] convert(double[] samples) {
		double[] result = new double[samples.length * 2];
		for (int i = 0; i < samples.length; i++) {
			result[i * 2] = samples[i];
			result[i * 2 + 1] = 0.0;
		}

		DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
		fft.complexForward(result);
				
		double[] finalResult = new double[samples.length / 2 + 1];
		for (int i = 0; i < finalResult.length; i++) {
			double real = result[i * 2];
			double imag = result[i * 2 + 1];
			double magnitude = Math.hypot(real, imag);
			finalResult[i] = magnitude;
		}	
		return finalResult;
	}
}
