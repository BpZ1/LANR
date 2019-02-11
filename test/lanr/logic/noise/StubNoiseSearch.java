package lanr.logic.noise;

import javolution.util.FastTable;
import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

public class StubNoiseSearch extends NoiseSearch {
	
	private FastTable<Noise> noiseList = new FastTable<Noise>();
	
	public StubNoiseSearch(int sampleRate, int windowSize) {
		super(sampleRate, windowSize, 0);
	}
	
	@Override
	public void search(double[] samples) {
		Noise n1 = new Noise(NoiseType.Clipping, 4, 10);
		Noise n2 = new Noise(NoiseType.Clipping, 8, 1);
		Noise n3 = new Noise(NoiseType.Clipping, 9, 20);
		Noise n4 = new Noise(NoiseType.Clipping, 30, 45);
		Noise n5 = new Noise(NoiseType.Clipping, 40, 50);
		
		noiseList.add(n1);
		noiseList.add(n2);
		noiseList.add(n3);
		noiseList.add(n4);
		noiseList.add(n5);
	}

	@Override
	public FastTable<Noise> getNoise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void compact() {
		// TODO Auto-generated method stub
	}
	
	public FastTable<Noise> combineNoises(long distance) {
		return combineNoises(noiseList, distance);
	}

}
