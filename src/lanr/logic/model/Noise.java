package lanr.logic.model;

/**
 * @author Nicolas Bruch
 * 
 *         Represents noise found in an audio signal.
 *
 */
public class Noise {

	private double severity;
	private long location;
	private long length;
	private int channel;
	private NoiseType type;

	public Noise(NoiseType type, long location, long length, double severity) {
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

	public long getLocation() {
		return location;
	}

	public void setLocation(long location) {
		this.location = location;
	}

	public NoiseType getType() {
		return type;
	}

	public void setType(NoiseType type) {
		this.type = type;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

}
