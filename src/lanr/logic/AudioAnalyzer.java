package lanr.logic;

import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;

import org.jtransforms.fft.DoubleFFT_1D;

public class AudioAnalyzer {

	private Spectrogram spectro;
	private final int sampleRate;

	public AudioAnalyzer(int frameSize, int sampleRate, boolean createSpectorgam) {
		this.sampleRate = sampleRate;
		if (createSpectorgam) {
			spectro = new Spectrogram(frameSize / 2);
		}
	}

	public BufferedImage getSpectrogram() {
		return spectro.getImage();
	}

	public void anazlyze(double[] data) {
		int sampleSize = data.length;

		double[] result = new double[sampleSize * 2];
		for (int i = 0; i < sampleSize; i++) {
			 data[i] /= 10000;
			result[i * 2] = data[i];
			result[i * 2 + 1] = 0.0;
		}

		DoubleFFT_1D fft = new DoubleFFT_1D(sampleSize);

		fft.complexForward(result);
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		double[] finalResult = new double[sampleSize / 2];
		for (int i = 0; i < finalResult.length; i++) {
			double real = result[i * 2];
			double imag = result[i * 2 + 1];
			double magnitude = Math.hypot(real, imag);
			max = Math.max(max, magnitude);
			min = Math.min(min, magnitude);
			finalResult[i] = magnitude;
		}	

		if (spectro != null) {
			spectro.addFrame(finalResult);
		}
		
	}

	private static double[] getFrequencyRange(double lower, double upper, double[] data, double sampleRate,
			double sampleSize) {
		DoubleBuffer results = DoubleBuffer.allocate(data.length);
		int currentFrequency = 0;
		int counter = 0;
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			double frequency = ((sampleRate / 2) / sampleSize) * i;
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
