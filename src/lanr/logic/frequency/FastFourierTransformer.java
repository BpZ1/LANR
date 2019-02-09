package lanr.logic.frequency;

import org.jtransforms.fft.DoubleFFT_1D;

/**
 * @author Nicolas Bruch
 *
 */
public class FastFourierTransformer implements SampleConverter {
	
	@Override
	public double[] convert(double[] samples) {
		double[] result = new double[samples.length * 2];
		for (int i = 0; i < samples.length; i++) {
			result[i * 2] = samples[i];
			result[i * 2 + 1] = 0.0;
		}

		DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
		fft.complexForward(result);
				
		double[] finalResult = new double[samples.length / 2 + 1];
		for (int i = 0; i < finalResult.length; i++) {
			double real = result[i * 2];
			double imag = result[i * 2 + 1];
			double magnitude = Math.hypot(real, imag);
			finalResult[i] = magnitude;
		}	
		return finalResult;
	}
}
