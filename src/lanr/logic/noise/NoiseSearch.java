package lanr.logic.noise;

import java.util.List;

import lanr.logic.model.Noise;

public interface NoiseSearch {
	
	public void search(double[] samples);
	
	public List<Noise> getNoise();
	
	public void compact();
}
