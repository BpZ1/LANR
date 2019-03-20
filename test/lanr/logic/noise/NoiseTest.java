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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

public class NoiseTest {

	
	private static Noise n1;
	private static Noise n2;
	private static Noise n3;
	private static Noise n4;
	private static Noise n5;
	
	
	@BeforeEach
	void beforeEach() throws Exception {
		n1 = new Noise(NoiseType.Background, 5, 5);
		n2 = new Noise(NoiseType.Background, 6, 2);
		n3 = new Noise(NoiseType.Background, 9, 5);
		n4 = new Noise(NoiseType.Background, 1, 6);
		n5 = new Noise(NoiseType.Background, 0, 20);
	}
	
	@Test
	void isInsideTest() {
		assertTrue(n1.isInside(n2));
		assertFalse(n2.isInside(n3));
	}
	
	@Test
	void isOutsideTest() {
		assertTrue(n2.isOutside(n3));
		assertFalse(n1.isOutside(n2));
	}
	
	@Test
	void additionInsideTest() {		
		n1.add(n2);
		assertEquals(5, n1.getLocation());
		assertEquals(5, n1.getLength());
	}
	
	@Test
	void additionLeftBorder() {
		n1.add(n4);
		assertEquals(1, n1.getLocation());
	}
	
	@Test
	void additionRightBorder() {
		n1.add(n3);
		assertEquals(5, n1.getLocation());
		assertEquals(9, n1.getLength());
	}
	
	@Test
	void addBiggerNoiseTest() {
		n1.add(n5);
		assertEquals(0, n1.getLocation());
		assertEquals(20, n1.getLength());
	}
}
