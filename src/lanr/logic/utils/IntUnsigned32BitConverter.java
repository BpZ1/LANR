package lanr.logic.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Converter for 32 bit unsigned int from byte array.
 * 
 * @author Nicolas Bruch      
 *
 */
public class IntUnsigned32BitConverter extends DoubleConverter {

	@Override
	public double[] convert(byte[] data) {
		double[] resultData = new double[data.length / 4];
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < resultData.length; i++) {
			resultData[i] = buffer.getFloat();
		}
		return resultData;
	}

}
