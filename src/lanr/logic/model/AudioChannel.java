package lanr.logic.model;

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
	
	public int getBitRate() {
		return bitRate;
	}

	public int getSampleRate() {
		return sampleRate;
	}

}
