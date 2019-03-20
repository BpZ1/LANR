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

package lanr.logic.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 
 * Converter for 8 bit byte from byte array.
 *
 * @author Nicolas Bruch
 *
 */
public class Byte8BitConverter extends DoubleConverter {

	private static final float MULTIPLIER = 1f / (float)Byte.MAX_VALUE;
	
	@Override
	public double[] convert(byte[] data) {
		double[] resultData = new double[data.length];
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < resultData.length; i++) {
			double value = ((float) buffer.get()) * MULTIPLIER;
			if(value > 1) {
				value = 1;
			}else if(value < -1) {
				value = -1;
			}
			resultData[i] = value;
		}
		return resultData;
	}

}
