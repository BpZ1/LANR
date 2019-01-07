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
