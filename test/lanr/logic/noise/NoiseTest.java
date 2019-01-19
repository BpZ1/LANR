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
		n1 = new Noise(NoiseType.Background, 5, 5, 100);
		n2 = new Noise(NoiseType.Background, 6, 2, 50);
		n3 = new Noise(NoiseType.Background, 9, 5, 30);
		n4 = new Noise(NoiseType.Background, 1, 6, 20);
		n5 = new Noise(NoiseType.Background, 0, 20, 10);
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
		assertEquals(150.0, n1.getSeverity(), 0.00001);
		assertEquals(5, n1.getLocation());
		assertEquals(5, n1.getLength());
	}
	
	@Test
	void additionLeftBorder() {
		n1.add(n4);
		assertEquals(1, n1.getLocation());
		assertEquals(120, n1.getSeverity());
	}
	
	@Test
	void additionRightBorder() {
		n1.add(n3);
		assertEquals(5, n1.getLocation());
		assertEquals(130, n1.getSeverity());
		assertEquals(9, n1.getLength());
	}
	
	@Test
	void addBiggerNoiseTest() {
		n1.add(n5);
		assertEquals(0, n1.getLocation());
		assertEquals(20, n1.getLength());
		assertEquals(110, n1.getSeverity());
	}
}
