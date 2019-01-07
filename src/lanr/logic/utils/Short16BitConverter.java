package lanr.logic.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Converter for 16 bit shorts from byte array.
 * 
 * @author Nicolas Bruch        
 *
 */
public class Short16BitConverter extends DoubleConverter {
	
	private static final float MULTIPLIER = 1f / (float)Short.MAX_VALUE;
	
	@Override
	public double[] convert(byte[] data) {
		double[] resultData = new double[data.length / 2];
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < resultData.length; i++) {
			double value = ((float) buffer.getShort()) * MULTIPLIER;
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
