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

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lanr.logic.model.Noise;

/**
 * Tests for the abstract noise search class.
 * 
 * @author Nicolas Bruch
 *
 */
public class NoiseCombinationTest {

	/**
	 * Noises created:<br>
	 * Position - length<br>
	 * 4 - 10<br>
	 * 8 - 1<br>
	 * 9 - 20<br>
	 * 30 - 45<br>
	 * 40 - 50
	 */
	private static StubNoiseSearch search;
	/**
	 * Number of noises found if distance was added
	 */
	private static final long DISTANCE_COUNT = 1;
	/**
	 * Number of noises found if no distance was added
	 */
	private static final long NO_DISTANCE_COUNT = 2;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		search = new StubNoiseSearch(1, 1);
		search.search(null);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void noExtraDistanceTest() {
		List<Noise> noises = search.combineNoises(0);
		assertEquals(NO_DISTANCE_COUNT, noises.size());
		if (noises.size() == NO_DISTANCE_COUNT) {
			assertEquals(25, noises.get(0).getLength());
			assertEquals(60, noises.get(1).getLength());
		}
	}

	@Test
	void extraDistanceTest() {
		List<Noise> noises = search.combineNoises(12);
		assertEquals(DISTANCE_COUNT, noises.size());
		if (noises.size() == DISTANCE_COUNT) {
			assertEquals(86, noises.get(0).getLength());
		}
	}
}
