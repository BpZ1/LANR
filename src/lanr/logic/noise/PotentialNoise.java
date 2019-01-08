package lanr.logic.noise;

import lanr.logic.model.Noise;

public abstract class PotentialNoise {

	protected Noise noise;
	protected int counter = 0;
	protected int skipCounter = 0;
	
	public PotentialNoise(Noise noise) {
		this.noise = noise;
	}
	
	public Noise getNoise() {
		return noise;
	}

	public void setNoise(Noise noise) {
		this.noise = noise;
	}

	public void incrementSkipCounter() {
		this.skipCounter++;
	}
	
	public void incrementCounter() {
		this.counter++;
	}
	
	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public int getSkipCounter() {
		return skipCounter;
	}

	public void setSkipCounter(int skipCounter) {
		this.skipCounter = skipCounter;
	}
}
