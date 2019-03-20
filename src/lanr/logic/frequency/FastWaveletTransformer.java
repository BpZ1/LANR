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

import jwave.Transform;
import jwave.datatypes.natives.Complex;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.wavelets.daubechies.Daubechies9;

/**
 * @author Nicolas Bruch
 *
 */
public class FastWaveletTransformer implements SampleConverter {
	
	@Override
	public double[] convert(double[] samples) {
		Complex[] complex = new Complex[samples.length];
		for(int i= 0; i < samples.length; i++) {
			Complex c = new Complex();			
			c.setReal(samples[i]);
			complex[i] = c;
		}
		Transform transform = new Transform(new FastWaveletTransform(new Daubechies9()));
		complex = transform.forward(complex);
		
		for(int i= 0; i < samples.length / 2 + 1; i++) {
			double value = 2 * complex[i].getMag() / samples.length;
			samples[i] = value;
		}
		return samples;
	}

}
