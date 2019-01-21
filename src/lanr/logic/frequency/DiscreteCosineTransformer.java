package lanr.logic.frequency;

import org.jtransforms.dct.DoubleDCT_1D;

/**
 * @author Nicolas Bruch
 *
 */
public class DiscreteCosineTransformer implements SampleConverter {

	@Override
	public double[] convert(double[] samples) {
		DoubleDCT_1D dct = new DoubleDCT_1D(samples.length); 
		dct.forward(samples, true);
		for(int i = 0; i < samples.length; i++) {
			samples[i] = (2 * samples[i] / samples.length) * 24;
		}
		return samples;
	}

	
}
