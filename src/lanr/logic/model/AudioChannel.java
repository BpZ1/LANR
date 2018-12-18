package lanr.logic.model;

public class AudioChannel {

	private int bitRate;
	private int sampleRate; 
	private double[] samples; 
	
	
	public AudioChannel(double[] samples, int bitRate, int sampleRate) {
		this.samples = samples;
		this.bitRate = bitRate;
		this.sampleRate = sampleRate;
		int duration = samples.length/sampleRate;
		System.out.println("Length: " + duration + " seconds");
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
