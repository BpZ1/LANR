package lanr.logic;

import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import lanr.logic.noise.BackgroundNoiseSearch;
import lanr.logic.noise.ClippingSearch;
import lanr.logic.noise.FrequencyConversion;
import lanr.logic.noise.NoiseSearch;
import lanr.logic.noise.SilenceSearch;
import lanr.logic.noise.VolumeSearch;
import lanr.logic.noise.WindowFunction;

public class AudioAnalyzer {
	
	@SuppressWarnings("serial")
	private List<NoiseSearch> sampleAnalyzer = new ArrayList<NoiseSearch>() {{
		add(new ClippingSearch());
		add(new SilenceSearch());
		add(new VolumeSearch());
		//Add new sample analyting methods here
	}};
	
	@SuppressWarnings("serial")
	private List<NoiseSearch> frequencyAnalyzer = new ArrayList<NoiseSearch>() {{
		add(new BackgroundNoiseSearch());	
		//Add new frequency analyzing methods here
	}};
	
	private Spectrogram spectro;
	private final int sampleRate;
	private FrequencyConversion conversion;
	

	private boolean useWindowFunction;
	public AudioAnalyzer(int frameSize, int sampleRate, boolean createSpectorgam,
			boolean useWindowFunction, FrequencyConversion conversion) {
		this.conversion = conversion;
		this.sampleRate = sampleRate;
		this.useWindowFunction = useWindowFunction;
		if (createSpectorgam) {
			//FFT method used only uses half of the output data
			if(conversion == FrequencyConversion.FFT) {
				spectro = new Spectrogram(frameSize / 2);
			}else {
				spectro = new Spectrogram(frameSize);
			}			
		}
	}

	public BufferedImage getSpectrogram() {
		return spectro.getImage();
	}

	public void anazlyze(double[] data) {
		//Analyzing the samples
		sampleAnalysis(data);
		
		if(useWindowFunction) {
			WindowFunction wf = new WindowFunction(data.length, conversion);
			wf.apply(data);
		}
		double[] magnitudes = conversion.getConverter().convert(data);
		if (spectro != null) {
			spectro.addFrame(magnitudes);
		}
		//Analyzing the frequencies
		frequencyAnalysis(magnitudes);		
	}
	
	private void sampleAnalysis(double[] samples) {
		for(NoiseSearch search : sampleAnalyzer) {
			search.search(samples);
		}
	}
	private void frequencyAnalysis(double[] magnitudes) {
		for(NoiseSearch search : frequencyAnalyzer) {
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
