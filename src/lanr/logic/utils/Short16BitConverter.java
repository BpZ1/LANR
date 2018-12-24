package lanr.logic.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Nicolas Bruch
 * 
 *         Converter for 16 bit shorts from byte array.
 *
 */
public class Short16BitConverter extends DoubleConverter {

	@Override
	public double[] convert(byte[] data) {
		double[] resultData = new double[data.length / 2];
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < resultData.length; i++) {
			resultData[i] = buffer.getShort();
		}
		return resultData;
	}

}
