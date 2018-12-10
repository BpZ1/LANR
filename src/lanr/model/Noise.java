package lanr.model;

/**
 * @author Nicolas Bruch
 * 
 *         Represents noise found in an audio signal.
 *
 */
public class Noise {

	private double severity;
	private int location;
	private int length;
	private NoiseType type;

	public Noise(NoiseType type, int location, int length, double severity) {
		this.type = type;
		this.location = location;
		this.severity = severity;
		this.length = length;
	}

	public double getSeverity() {
		return severity;
	}

	public void setSeverity(double severity) {
		this.severity = severity;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public NoiseType getType() {
		return type;
	}

	public void setType(NoiseType type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
