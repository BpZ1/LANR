package lanr.logic.noise;

import lanr.logic.model.Noise;

public class PotentialClipping {

	private Noise noise;
	private long skipCounter = 0;
	private double maxAmp;
	private double minAmp;
		
	public PotentialClipping(Noise noise) {
		this.noise = noise;
	}
	
	public Noise getNoise() {
		return noise;
	}
	
	public void setNoise(Noise noise) {
		this.noise = noise;
	}
	
	public double getMaxAmp() {
		return maxAmp;
	}
	
	public void setMaxAmp(double maxAmp) {
		this.maxAmp = maxAmp;
	}
	
	public double getMinAmp() {
		return minAmp;
	}
	
	public void setMinAmp(double minAmp) {
		this.minAmp = minAmp;
	}
	
	public void addSample(double sample) {
		if(noise != null) {
			noise.setLength(noise.getLength() +1);
		}
		if(maxAmp < sample) {
			maxAmp = sample;
		}
		if(minAmp > sample) {
			minAmp = sample;
		}
	}

	public void incrementSkipCounter() {
		this.skipCounter++;
	}
	
	public long getSkipCounter() {
		return skipCounter;
	}

	public void setSkipCounter(long skipCounter) {
		this.skipCounter = skipCounter;
	}
}
