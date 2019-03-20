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

package lanr.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lanr.logic.model.Interval;

class IntervalTest {

	private static Interval interval1;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		interval1 = new Interval(0.0, 0.521);
	}

	@Test
	void containsTest() {
		assertTrue(interval1.contains(0.2));
		assertTrue(interval1.contains(0.521));
		assertFalse(interval1.contains(0.0));
		assertFalse(interval1.contains(0.522));
	}

}
