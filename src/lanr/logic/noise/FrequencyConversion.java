package lanr.logic.noise;

import lanr.logic.frequency.SampleConverter;
import lanr.logic.frequency.FastFourierTransformer;
import lanr.logic.frequency.DiscreteConsineTransformer;
import lanr.logic.frequency.FastWaveletTransformer;

/**
 * @author Nicolas Bruch
 * 
 *         Different converter for samples into frequency domain.
 *
 */
public enum FrequencyConversion {
	/**
	 * Discrete Consine Transform
	 */
	DCT(new DiscreteConsineTransformer()),
	/**
	 * Fast Fourier Transform
	 */
	FFT(new FastFourierTransformer()),
	/**
	 * Fast Wavelet Transform
	 */
	FWT(new FastWaveletTransformer());

	private final SampleConverter converter;

	private FrequencyConversion(SampleConverter converter) {
		this.converter = converter;
	}

	public SampleConverter getConverter() {
		return converter;
	}
}
