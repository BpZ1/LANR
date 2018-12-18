package lanr.logic.model;

/**
 * @author Nicolas Bruch
 * 
 *         Contains the data for a single audio channel.
 *
 */
public class AudioChannel {

	private int bitRate;
	private int sampleRate;
	private double[] samples;

	public AudioChannel(double[] samples, int bitRate, int sampleRate) {
		this.samples = samples;
		this.bitRate = bitRate;
		this.sampleRate = sampleRate;
	}

	public double[] getSamples() {
		return samples;
	}

	public int getBitDepth() {
		return bitRate;
	}

	public int getSampleRate() {
		return sampleRate;
	}

}
