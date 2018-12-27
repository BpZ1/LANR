package lanr.logic.frequency;

import lanr.logic.model.Noise;

public class PotentialNoise {

	private Noise noise;
	private int counter = 0;
	private int skipCounter = 0;
	private int lastWindowIndex;
	
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
	
	public int getSkipCounter() {
		return skipCounter;
	}

	public void setSkipCounter(int skipCounter) {
		this.skipCounter = skipCounter;
	}

	public int getLastWindowIndex() {
		return lastWindowIndex;
	}

	public void setLastWindowIndex(int lastWindowIndex) {
		this.lastWindowIndex = lastWindowIndex;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
}
