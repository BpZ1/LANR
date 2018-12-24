package lanr.logic.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Nicolas Bruch
 * 
 * 	Converter for 64 bit doubles from byte array.
 *
 */
public class Double64BitConverter extends DoubleConverter{

	@Override
	public double[] convert(byte[] data) {
		double[] resultData = new double[data.length / 8];
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < resultData.length; i ++) {
			resultData[i] = buffer.getDouble();
		}
		return resultData;
	}

}
