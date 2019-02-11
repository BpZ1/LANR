package lanr.logic.noise;

import javolution.util.FastTable;
import lanr.logic.model.Noise;

/**
 * 
 * Implementation of the noise estimation algorithm proposed by:
 * Rangachari,  Sundarrajan ;Loizou,  Philipos C.: 
 * A noise-estimation algorithmfor highly non-stationary environments. 
 * In:Speech  communication48 (2006), Nr.2, S. 220–231
 *
 */
public class BackgroundNoiseEstimation extends FrequencySearch {

	private static final double SMOOTHING_CONSTANT_ETA = 0.7;
	private static final double SMOOTHING_CONSTANT_ALPHA_D = 0.85;
	private static final double SMOOTHING_CONSTANT_ALPHA_P = 0.2;
	private static final double ADAPTION_TIME_CONSTANT = 0.8;
	private static final double GAMMA = 0.998;
	
	private int frameIndex = 0;
	
	/**
	 * Smoothed power spectra of the previous window.
	 */
	private double[] previousSPSValues;
	/**
	 * Noise estimate of the previous window.
	 */
	private double[] previousNoiseEstimates;
	/**
	 * Minimum sps values of the previous window.
	 */
	private double[] previousMinimumSPS;
	/**
	 * Sps minimum of the previous window.
	 */
	private double[] previousSProbabilities;

	public BackgroundNoiseEstimation(int sampleRate, int windowSize, double replayGain, boolean mirrored) {
		super(sampleRate, windowSize, replayGain, mirrored);
		previousSPSValues = new double[windowSize];
		previousNoiseEstimates = new double[windowSize];
		previousMinimumSPS = new double[windowSize];
		previousSProbabilities = new double[windowSize];
		for (int i = 0; i < windowSize; i++) {
			frequencies.add(calculateFrequency(i));
			previousSPSValues[i] = 0;
			previousNoiseEstimates[i] = 0;
			previousMinimumSPS[i] = Double.POSITIVE_INFINITY;
			previousSProbabilities[i] = 0;
		}
	}

	@Override
	public void search(double[] bins) {
		double[] spsValues = new double[bins.length]; 
		double[] currentNoiseEstimate = new double[bins.length]; 
		
		for(int i = 0; i < bins.length; i++) {
			spsValues[i] = calculateSPS(frameIndex, i, bins[i]);
			previousMinimumSPS[i] = calculateMinimumSPS(
					spsValues[i],
					previousMinimumSPS[i]);
		}
		
		double[] speechProbabilities = new double[bins.length];
		for(int i = 0; i < spsValues.length; i++) {
			speechProbabilities[i] = SMOOTHING_CONSTANT_ALPHA_P * previousSProbabilities[i]
					+ (1 - SMOOTHING_CONSTANT_ALPHA_P) * containsSpeech(frameIndex, i, spsValues[i]);
			
			double alphaS = calculateTimeFrequencySmoothingFactor(frameIndex, i, speechProbabilities[i]);
			currentNoiseEstimate[i] = alphaS * previousNoiseEstimates[i] 
					+ (1 - alphaS) * Math.pow(spsValues[i], 2);
		}

		//Analyse estimates here!
	
		//Update the old values
		previousSPSValues = spsValues;
		previousNoiseEstimates = currentNoiseEstimate;
		previousSProbabilities = speechProbabilities;
		frameIndex++;
	}

	@Override
	public FastTable<Noise> getNoise() {
		//NOT IMPLEMENTED
		return null;
	}

	@Override
	public void compact() {
		//NOT IMPLEMENTED
	}
	
	/**
	 * Calculates the smoothed power spectrum.
	 * @param frameIndex
	 * @param frequencyIndex
	 * @return
	 */
	private double calculateSPS(int frameIndex, int frequencyIndex, double frequencyBin) {
		if(frameIndex < 0) return 0;
		double result = SMOOTHING_CONSTANT_ETA * previousSPSValues[frequencyIndex]
				+ (1 - SMOOTHING_CONSTANT_ETA) * Math.pow(frequencyBin, 2);
		return result;	
	}
	
	private double calculateMinimumSPS(double previousMinimum, double sps) {
		if(previousMinimum < sps) {
			return GAMMA * previousMinimum 
					+ ((1 - GAMMA) / (1 - ADAPTION_TIME_CONSTANT)) 
					* (sps - ADAPTION_TIME_CONSTANT * previousMinimum);
		}else {
			return sps;
		}
	}
	
	private int containsSpeech(int frameIndex, int frequencyIndex, double sps) {
		double s = sps / previousSPSValues[frequencyIndex];
		if(s > getFrequencyDependencyThreshold(frequencyIndex)) {
			return 1;
		}else {
			return 0;
		}
	}
	
	private double calculateTimeFrequencySmoothingFactor(
			int frameIndex, int frequencyIndex, double speechProbability) {
		return SMOOTHING_CONSTANT_ALPHA_D 
				+ (1 - SMOOTHING_CONSTANT_ALPHA_D) * speechProbability;
	}
	
	private double getFrequencyDependencyThreshold(int frequencyIndex) {
		double frequency = frequencies.get(frequencyIndex);
		if(frequency < 1000) {
			return 2.0;
		}else if(frequency < 3000) {
			return 2.0;
		}else {
			return 5.0;
		}
	}
}
