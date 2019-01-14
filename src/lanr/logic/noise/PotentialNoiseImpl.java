package lanr.logic.noise;

import lanr.logic.model.Noise;

public class PotentialHumming extends PotentialNoise {

	private int lastWindowIndex;
	
	public PotentialHumming(Noise noise) {
		super(noise);
	}

	public int getLastWindowIndex() {
		return lastWindowIndex;
	}

	public void setLastWindowIndex(int lastWindowIndex) {
		this.lastWindowIndex = lastWindowIndex;
	}
}
