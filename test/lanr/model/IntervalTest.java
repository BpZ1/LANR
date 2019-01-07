package lanr.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
