package lanr.logic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Utils {

	/**
	 * Concatenates two arrays.
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static byte[] concatArrays(byte[] first, byte[] second) {
		byte[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	/**
	 * Concatenates two arrays.
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static short[] concatArrays(short[] first, short[] second) {
		short[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
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
	
	public static double[] byteToDoubleConverter(int bitPerValue, byte[] rawData) {
		// Number of samples in the result
		int sampleCount = rawData.length / bitPerValue;
		double[] resultData = new double[sampleCount];
		int resultCounter = 0;
		for (int i = 0; i < sampleCount; i += bitPerValue) {
			byte[] data = Arrays.copyOfRange(rawData, i, i + bitPerValue);
			resultData[resultCounter] = (double) ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort();
			resultCounter++;
		}
		return resultData;
	}
	
	public static short[] byteToShortConverter(int bitPerValue, byte[] rawData) {
		// Number of samples in the result
		int sampleCount = rawData.length / bitPerValue;
		short[] resultData = new short[sampleCount];
		int resultCounter = 0;
		for (int i = 0; i < sampleCount; i += bitPerValue) {
			byte[] data = Arrays.copyOfRange(rawData, i, i + bitPerValue);
			resultData[resultCounter] = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort();
			resultCounter++;
		}
		return resultData;
	}
}
