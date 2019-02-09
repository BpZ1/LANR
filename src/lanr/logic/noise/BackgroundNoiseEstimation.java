package lanr.logic.noise;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lanr.logic.Utils;
import lanr.logic.model.AudioData;
import lanr.logic.model.AudioStream;
import lanr.logic.model.LANRException;
import lanr.logic.model.Noise;
import lanr.logic.model.NoiseType;

/**
 * @author Nicolas Bruch
 *
 */
public class BackgroundNoiseEstimation extends FrequencySearch {

	private static final double SMOOTHING_CONSTANT_ETA = 0.7;
	private static final double SMOOTHING_CONSTANT_ALPHA_D = 0.85;
	private static final double SMOOTHING_CONSTANT_ALPHA_P = 0.2;
	private static final double ADAPTION_TIME_CONSTANT = 0.8;
	private static final double GAMMA = 0.998;
	
	
	private int frameIndex = 0;
	private double minimumFrameSPS = 0;
	
	private double[] previousSPSValues;
	private double[] previousNoiseEstimates;
	private double[] previousMinimumSPS;
	private double[] previousSProbabilities;
	private Noise currentNoise;
	private List<Noise> foundNoise = new LinkedList<Noise>();
	private double average = 0;

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
		double value = 0;
		for(int i = 0; i < bins.length; i++) {
			value += Math.pow(spsValues[i], 2);
		}
		value /= bins.length;
		value = Math.log(value);
		updateAverage(value);
		if(value >= average) {
			System.out.println(value);	
			if(currentNoise != null) {
				currentNoise.setLength(currentNoise.getLength() + windowSize);
				System.out.println("Noise extended");
			}else {
				currentNoise = new Noise(NoiseType.Background, frameIndex+1 * windowSize, windowSize, 1);
				System.out.println("New noise");
			}
		}else {
			if(currentNoise != null) {
				foundNoise.add(currentNoise);
				currentNoise = null;
			}
		}
		valueList.add(value);
		//Update the old values
		previousSPSValues = spsValues;
		previousNoiseEstimates = currentNoiseEstimate;
		previousSProbabilities = speechProbabilities;
		frameIndex++;
	}

	@Override
	public List<Noise> getNoise() {
		return foundNoise;
	}

	@Override
	public void compact() {
		if(currentNoise != null) {
			foundNoise.add(currentNoise);
			currentNoise = null;
		}
		foundNoise = combineNoises(foundNoise, 3);
		try {
			writeLogFile(valueList);
		} catch (LANRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private void updateAverage(double value) {
		average = average + (value - average) / (frameIndex+1);
	}
	
	private List<Double> valueList = new LinkedList<Double>();
	public static void writeLogFile(List<Double> values) throws LANRException {
		Path path = Paths.get("data.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
			for(int i = 0; i < values.size(); i++) {
				writer.write(String.valueOf(values.get(i)));
				writer.newLine();
			}
		} catch (IOException e) {
			throw new LANRException("Could not create log file.", e);
		}
	}
}
