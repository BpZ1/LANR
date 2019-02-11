package lanr.logic.model;

/**
 * @author Nicolas Bruch
 *
 */
public class Interval {

	private final double lowerBound;
	private final double upperBound;
	
	public Interval(double lower, double upper) {
		this.lowerBound = lower;
		this.upperBound = upper;
	}
	
	/**
	 * Checks if the given value is inside of the defined bounds.
	 * The bounds are checked including the upper bound excluding the lower.
	 * @param value
	 * @return
	 */
	public boolean contains(double value) {
		if(value > lowerBound && value <= upperBound) {
			return true;
		}
		return false;
	}
	
	public double getLowerBorder() {
		return lowerBound;
	}
	
	public double getUpperBorder() {
		return upperBound;
	}
}
