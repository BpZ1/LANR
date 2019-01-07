package lanr.logic.noise;

import java.util.List;

import lanr.logic.model.AudioData;
import lanr.logic.model.Noise;

public class SilenceSearch extends NoiseSearch {

	public SilenceSearch(int sampleRate, int windowSize) {
		super(sampleRate, windowSize);
	}

	@Override
	public void search(double[] samples) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Noise> getNoise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void compact() {
		// TODO Auto-generated method stub
		
	}

}
