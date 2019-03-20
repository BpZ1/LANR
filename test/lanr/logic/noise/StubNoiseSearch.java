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
import lanr.logic.model.NoiseType;

public class StubNoiseSearch extends NoiseSearch {
	
	private FastTable<Noise> noiseList = new FastTable<Noise>();
	
	public StubNoiseSearch(int sampleRate, int windowSize) {
		super(sampleRate, windowSize, 0);
	}
	
	@Override
	public void search(double[] samples) {
		Noise n1 = new Noise(NoiseType.Clipping, 4, 10);
		Noise n2 = new Noise(NoiseType.Clipping, 8, 1);
		Noise n3 = new Noise(NoiseType.Clipping, 9, 20);
		Noise n4 = new Noise(NoiseType.Clipping, 30, 45);
		Noise n5 = new Noise(NoiseType.Clipping, 40, 50);
		
		noiseList.add(n1);
		noiseList.add(n2);
		noiseList.add(n3);
		noiseList.add(n4);
		noiseList.add(n5);
	}

	@Override
	public FastTable<Noise> getNoise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void compact() {
		// TODO Auto-generated method stub
	}
	
	public FastTable<Noise> combineNoises(long distance) {
		return combineNoises(noiseList, distance);
	}

}
