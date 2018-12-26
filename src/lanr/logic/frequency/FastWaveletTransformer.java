package lanr.logic.frequency;

import jwave.Transform;
import jwave.datatypes.natives.Complex;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.wavelets.haar.Haar1;


public class FastWaveletTransformer implements SampleConverter {

	@Override
	public double[] convert(double[] samples) {
		Complex[] complex = new Complex[samples.length];
		
		for(int i= 0; i < samples.length; i++) {
			Complex c = new Complex();
			c.setReal(samples[i]);
			complex[i] = c;
		}
		
		Transform transform = new Transform(new FastWaveletTransform(new Haar1()));
		complex = transform.forward(complex);
		
		for(int i= 0; i < samples.length; i++) {
			samples[i] = complex[i].getMag();
		}
		return samples;
	}

}
