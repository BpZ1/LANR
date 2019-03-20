/*
 * LANR (Lecture Audio Noise Recognition) is a software that strives to automate
 * the reviewing process of lecture recordings at the WIAI faculty of the University of Bamberg.
 *
 * Copyright (C) 2019 Nicolas Bruch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package lanr.logic;

import java.awt.image.BufferedImage;

import javolution.util.FastTable;
import lanr.logic.frequency.FrequencyConversion;
import lanr.logic.frequency.windowfunctions.WindowFunction;
import lanr.logic.frequency.windowfunctions.WindowFunctionImpl;
import lanr.logic.model.Noise;
import lanr.logic.noise.ClippingSearch;
import lanr.logic.noise.FrequencySearch;
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

	private FastTable<NoiseSearch> sampleAnalyzer = new FastTable<NoiseSearch>();
	private FastTable<FrequencySearch> frequencyAnalyzer = new FastTable<FrequencySearch>();

	private Spectrogram spectro;
	private int windowSize;
	private FrequencyConversion conversion;
	private WindowFunctionImpl windowFunction;
	private double replayGain;

	public AudioAnalyzer(int windowSize, int sampleRate, double replayGain, boolean createSpectorgam,
			WindowFunction windowFunction, FrequencyConversion conversion) {
		
		this.windowFunction = windowFunction.getImplementation(windowSize);
		this.windowSize = windowSize;
		this.replayGain = replayGain;
	
		//Sample analyzer
		sampleAnalyzer.add(new ClippingSearch(sampleRate, windowSize, replayGain));
		sampleAnalyzer.add(new SilenceSearch(sampleRate, windowSize, replayGain));
		sampleAnalyzer.add(new VolumeSearch(sampleRate, windowSize, replayGain));
		//Frequency analyzer
		frequencyAnalyzer.add(new HummingSearch(sampleRate, windowSize, replayGain, conversion.getHalfSamples()));
		
		this.conversion = conversion;
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
	public FastTable<Noise> getNoise() {
		FastTable<Noise> noise = new FastTable<Noise>();
		for(NoiseSearch analyzer : sampleAnalyzer) {
			if(analyzer.getFoundNoise() != null) {
				noise.addAll(analyzer.getFoundNoise());
			}
			
		}
		for(NoiseSearch analyzer : frequencyAnalyzer) {
			if(analyzer.getFoundNoise() != null) {
				noise.addAll(analyzer.getFoundNoise());
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
		if (windowFunction != null && conversion != FrequencyConversion.FWT) {
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
			double value = 2 * values[i] / windowSize;
			values[i] = Utils.sampleToDBFS(value) + replayGain;
		}
		return values;
	}
}
