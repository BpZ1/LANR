package lanr.logic.frequency;

import org.jtransforms.dct.DoubleDCT_1D;

public class DiscreteConsineTransformer implements SampleConverter {

	@Override
	public double[] convert(double[] samples) {
		DoubleDCT_1D dct = new DoubleDCT_1D(samples.length); 
		dct.forward(samples, true);
		return samples;
	}

	
}
