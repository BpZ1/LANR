package lanr.logic.noise;

import java.util.List;

import lanr.logic.FrequencyAnalyzer;
import lanr.logic.model.Noise;

public class BackgroundNoiseSearch extends FrequencyAnalyzer {

	
	public BackgroundNoiseSearch(int sampleRate, int windowSize) {
		super(sampleRate, windowSize);
	}
	
	@Override
	public void search(double[] frequencySamples) {
		
	}

	@Override
	public List<Noise> getNoise() {
		
		
		return null;
	}

	@Override
	public void compact() {
		// TODO Auto-generated method stub
		
	}

}
