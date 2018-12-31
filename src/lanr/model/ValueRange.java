package lanr.model;

public class ValueRange {

	private int upperLevel;
	private int lowerLevel;
	
	public ValueRange(int lower, int upper) {
		this.upperLevel = upper;
		this.lowerLevel = lower;
	}
	
	/**
	 * Checks if the value is inside the defined range.
	 * @param value
	 * @return
	 */
	public boolean isInside(int value) {
		if(value > this.lowerLevel && value < this.upperLevel) {
			return true;
		}
		return false;
	}

	public int getLowerLevel() {
		return lowerLevel;
	}

	public void setLowerLevel(int lowerLevel) {
		this.lowerLevel = lowerLevel;
	}

	public int getUpperLevel() {
		return upperLevel;
	}

	public void setUpperLevel(int upperLevel) {
		this.upperLevel = upperLevel;
	}
	
	@Override
	public int hashCode() {
	    return upperLevel * 31 + lowerLevel;
	}
}
