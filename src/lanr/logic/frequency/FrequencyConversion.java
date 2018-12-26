package lanr.logic.frequency;

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
	DCT(new DiscreteConsineTransformer(), false),
	/**
	 * Fast Fourier Transform
	 */
	FFT(new FastFourierTransformer(), true),
	/**
	 * Fast Wavelet Transform
	 */
	FWT(new FastWaveletTransformer(), true);

	private final SampleConverter converter;
	private boolean halfSamples;
	private FrequencyConversion(SampleConverter converter, boolean halfSamples) {
		this.converter = converter;
		this.halfSamples = halfSamples;
	}

	public SampleConverter getConverter() {
		return converter;
	}
	
	public boolean getHalfSamples() {
		return halfSamples;
	}
}
