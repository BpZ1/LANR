package lanr.logic;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lanr.logic.frequency.FrequencyConversion;
import lanr.logic.frequency.windowfunctions.VonHannWindow;
import lanr.logic.frequency.windowfunctions.WindowFunction;
import lanr.logic.model.Noise;
import lanr.logic.noise.BackgroundNoiseSearch;
import lanr.logic.noise.ClippingSearch;
import lanr.logic.noise.HummingSearch;
import lanr.logic.noise.NoiseSearch;
import lanr.logic.noise.SilenceSearch;
import lanr.logic.noise.VolumeSearch;

/**
 * Analyzer and the main part of the analyzing process. All analyzing
 * classes have to be added here.
 * 
 * @author Nicolas Bruch
 * 
 */
public class AudioAnalyzer {

	private List<NoiseSearch> sampleAnalyzer = new ArrayList<NoiseSearch>();
	private List<NoiseSearch> frequencyAnalyzer = new ArrayList<NoiseSearch>();

	private Spectrogram spectro;
	private FrequencyConversion conversion;
	private WindowFunction windowFunction;
	private boolean useWindowFunction;

	public AudioAnalyzer(int windowSize, int sampleRate, double replayGain, boolean createSpectorgam, boolean useWindowFunction,
			FrequencyConversion conversion) {
		
		//Sample analyzer
		sampleAnalyzer.add(new ClippingSearch(sampleRate, windowSize, replayGain));
		sampleAnalyzer.add(new SilenceSearch(sampleRate, windowSize, replayGain));
		sampleAnalyzer.add(new VolumeSearch(sampleRate, windowSize, replayGain));
		//Frequency analyzer
		frequencyAnalyzer.add(new BackgroundNoiseSearch(sampleRate, windowSize, replayGain));
		frequencyAnalyzer.add(new HummingSearch(sampleRate, windowSize, replayGain));
		
		this.conversion = conversion;
		this.useWindowFunction = useWindowFunction;
		if(useWindowFunction) {
			this.windowFunction = new VonHannWindow(windowSize);
		}
		if (createSpectorgam && windowSize < 50000) {
			// FFT method used only uses half of the output data
			if (conversion.getHalfSamples()) {
				spectro = new Spectrogram(windowSize / 2 + 1);
			} else {
				spectro = new Spectrogram(windowSize);
			}
		}
	}

	/**
	 * Returns the spectrogram generated.<br>
	 * This method should only be called after all data has been
	 * submitted as the spectrogram will otherwise be incomplete.
	 * @return
	 */
	public BufferedImage getSpectrogram() {
		if(spectro != null) {
			return spectro.getImage();			
		}
		return null;
	}
	
	/**
	 * Returns all {@link Noise}s that were found.<br>
	 * Should only be called after finish().
	 * @return List of the found noise.
	 */
	public List<Noise> getNoise() {
		List<Noise> noise = new LinkedList<Noise>();
		for(NoiseSearch analyzer : sampleAnalyzer) {
			if(analyzer.getNoise() != null) {
				noise.addAll(analyzer.getNoise());
			}
			
		}
		for(NoiseSearch analyzer : frequencyAnalyzer) {
			if(analyzer.getNoise() != null) {
				noise.addAll(analyzer.getNoise());
			}
		}
		return noise;
	}

	/**
	 * Takes an array of samples and analyzes them for the defined noises.
	 * The data will be converted into frequency domain after this method was used.
	 * It is therefore recommended to perform all operations that have to be performed
	 * on the samples before this method is called.
	 * @param data - An array of samples. <b>The size has to be a power of 2</b>
	 */
	public void anazlyze(double[] data) {
		// Analyzing the samples
		sampleAnalysis(data);
		if (useWindowFunction && conversion != FrequencyConversion.FWT) {
			windowFunction.apply(data);
		}
		double[] magnitudes = conversion.getConverter().convert(data);
		if (spectro != null) {
			spectro.addWindow(magnitudes);
		}
		// Analyzing the frequencies
		magnitudes = toDecibel(magnitudes);
		frequencyAnalysis(magnitudes);
	}

	private void sampleAnalysis(double[] samples) {
		for (NoiseSearch search : sampleAnalyzer) {
			search.search(samples);
		}
	}

	private void frequencyAnalysis(double[] magnitudes) {
		for (NoiseSearch search : frequencyAnalyzer) {
			search.search(magnitudes);
		}
	}

	/**
	 * Should be called after the analysis is complete.
	 */
	public void finish() {
		for (NoiseSearch search : frequencyAnalyzer) {
			search.compact();
		}
		for (NoiseSearch search : sampleAnalyzer) {
			search.compact();
		}
	}
	
	/**
	 * Converts amplitudes to decibel.
	 * @param values - Amplitudes.
	 * @return Decibel values
	 */
	private double[] toDecibel(double[] values) {
		for(int i = 0; i < values.length; i++) {
			values[i] = Utils.toDBFS(values[i]);
		}
		return values;
	}
}
