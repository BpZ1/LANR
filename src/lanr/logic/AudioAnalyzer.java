package lanr.logic;

import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import lanr.logic.frequency.FrequencyConversion;
import lanr.logic.noise.BackgroundNoiseSearch;
import lanr.logic.noise.ClippingSearch;
import lanr.logic.noise.NoiseSearch;
import lanr.logic.noise.SilenceSearch;
import lanr.logic.noise.VolumeSearch;
import lanr.logic.noise.WindowFunction;

/**
 * @author Nicolas Bruch
 * 
 *         Analyzer and the main part of the analyzing process. All analyzing
 *         classes have to be added here.
 * 
 *
 */
public class AudioAnalyzer {

	@SuppressWarnings("serial")
	private List<NoiseSearch> sampleAnalyzer = new ArrayList<NoiseSearch>() {
		{
			add(new ClippingSearch());
			add(new SilenceSearch());
			add(new VolumeSearch());
			// Add new sample analyting methods here
		}
	};

	@SuppressWarnings("serial")
	private List<NoiseSearch> frequencyAnalyzer = new ArrayList<NoiseSearch>() {
		{
			add(new BackgroundNoiseSearch());
			// Add new frequency analyzing methods here
		}
	};

	private Spectrogram spectro;
	private final int sampleRate;
	private FrequencyConversion conversion;

	private boolean useWindowFunction;

	public AudioAnalyzer(int frameSize, int sampleRate, boolean createSpectorgam, boolean useWindowFunction,
			FrequencyConversion conversion) {
		this.conversion = conversion;
		this.sampleRate = sampleRate;
		this.useWindowFunction = useWindowFunction;
		if (createSpectorgam) {
			// FFT method used only uses half of the output data
			if (conversion.getHalfSamples()) {
				spectro = new Spectrogram(frameSize / 2 + 1);
			} else {
				spectro = new Spectrogram(frameSize);
			}
		}
	}

	/**
	 * Returns the spectrogram generated.
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
			WindowFunction wf = new WindowFunction(data.length, conversion);
			wf.apply(data);
		}
		double[] magnitudes = conversion.getConverter().convert(data);
		if (spectro != null) {
			spectro.addWindow(magnitudes);
		}
		// Analyzing the frequencies
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

	private static double[] getFrequencyRange(double lower, double upper, double[] data, double sampleRate,
			double sampleSize) {
		DoubleBuffer results = DoubleBuffer.allocate(data.length);
		int currentFrequency = 0;
		int counter = 0;
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			double frequency = ((sampleRate / 2) / sampleSize) * i; // i * fs / N
			if (frequency > lower && frequency < upper) {
				results.put(data[i]);
			}
		}
		int pos = results.position();
		results.flip();
		double[] endResult = new double[pos];
		results.get(endResult, 0, pos);

		return endResult;
	}
}
