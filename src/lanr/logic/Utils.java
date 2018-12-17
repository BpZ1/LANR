package lanr.logic;

import java.util.Arrays;

public class Utils {

	/**
	 * Concatenates two arrays.
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static double[] concatArrays(double[] first, double[] second) {
		double[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
