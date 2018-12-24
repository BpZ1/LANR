package lanr.logic.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Nicolas Bruch
 *
 *         Converter for 8 bit byte from byte array.
 *
 */
public class Byte8BitConverter extends DoubleConverter {

	@Override
	public double[] convert(byte[] data) {
		double[] resultData = new double[data.length];
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < resultData.length; i++) {
			resultData[i] = buffer.get();
		}
		return resultData;
	}

}
