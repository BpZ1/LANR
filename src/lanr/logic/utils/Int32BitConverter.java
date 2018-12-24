package lanr.logic.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Nicolas Bruch
 *
 *         Converter for 32 bit floats from byte array.
 *
 */
public class Int32BitConverter extends DoubleConverter {

	@Override
	public double[] convert(byte[] data) {
		double[] resultData = new double[data.length / 4];
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < resultData.length; i++) {
			resultData[i] = buffer.getInt();
		}
		return resultData;
	}

}
