package lanr.logic.frequency;

import jwave.Transform;
import jwave.datatypes.natives.Complex;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.wavelets.daubechies.Daubechies9;

public class FastWaveletTransformer implements SampleConverter {
	
	@Override
	public double[] convert(double[] samples) {
		Complex[] complex = new Complex[samples.length];
		for(int i= 0; i < samples.length; i++) {
			Complex c = new Complex();			
			c.setReal(samples[i]);
			complex[i] = c;
		}
		Transform transform = new Transform(new FastWaveletTransform(new Daubechies9()));
		complex = transform.forward(complex);
		
		for(int i= 0; i < samples.length / 2 + 1; i++) {
			double value = complex[i].getMag();
			samples[i] = value;
		}
		return samples;
	}

}
