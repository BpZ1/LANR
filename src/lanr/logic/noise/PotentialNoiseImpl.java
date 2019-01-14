package lanr.logic.noise;

import lanr.logic.model.Noise;

public class PotentialNoiseImpl extends PotentialNoise {

	private int lastWindowIndex;
	
	public PotentialNoiseImpl(Noise noise) {
		super(noise);
	}

	public int getLastWindowIndex() {
		return lastWindowIndex;
	}

	public void setLastWindowIndex(int lastWindowIndex) {
		this.lastWindowIndex = lastWindowIndex;
	}
}
