package lanr.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

/**
 * @author Nicolas Bruch
 *
 */
public class Utils {

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Converts a sample into dBFS (Decibel Full Scale).<br>
	 * dbFS values go from negative infinity to 0.
	 * @param sample - Value to be converted.
	 * @return dBFS value.
	 */
	public static double sampleToDBFS(double sample) {
		return 20 * Math.log10(Math.abs(sample));
	}
	
	public static String getDurationString(long durationInSeconds) {
		LocalTime timeOfDay;
		if(durationInSeconds > 86399) {
			timeOfDay = LocalTime.ofSecondOfDay(86399);		
		}else {
			timeOfDay = LocalTime.ofSecondOfDay(durationInSeconds);			
		}
		String time = timeOfDay.toString();
		return time;
	}
	
	public static double getMax(double[] array) {
		double max = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < array.length; i++) {
			max = Math.max(max, array[i]);
		}
		return max;
	}
	
	public static double getMin(double[] array) {
		double min = Double.POSITIVE_INFINITY;
		for(int i = 0; i < array.length; i++) {
			min = Math.min(min, array[i]);
		}
		return min;
	}
}
