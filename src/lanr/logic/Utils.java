package lanr.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Converts a value into dBFS (Decibel Full Scale).<br>
	 * dbFS values go from negative infinity to 0.
	 * @param sample - Value to be converted.
	 * @return dBFS value.
	 */
	public static double toDBFS(double sample) {
		return 20 * Math.log10(sample);
	}
}
