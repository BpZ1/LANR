package lanr.logic.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import lanr.logic.AudioAnalyzer;

/**
 * @author Nicolas Bruch
 * 
 *         Contains the data for a single audio channel.
 *
 */
public class AudioChannel {

	/**
	 * Percentage of the frame that will be visualized.
	 */
	public static double visualisationReductionFactor = 0.1;
	public final static String DATA_ADDED_PROPERTY = "added";
	private final PropertyChangeSupport state = new PropertyChangeSupport(this);

	private AudioAnalyzer analyzer;
	private int index;
	private long length;
	/**
	 * Bit depth per sample.
	 */
	private final int bitRate;
	private final int bytePerSample;
	/**
	 * Samples per second.
	 */
	private final int sampleRate;
	/**
	 * List of found {@link Noise} types in the different channel of this audio
	 * file.
	 */
	private List<Noise> foundNoise = new ArrayList<Noise>();

	public AudioChannel(int bitRate, int sampleRate, int index, long length) {
		this.bitRate = bitRate;
		this.sampleRate = sampleRate;
		this.index = index;
		this.length = length;
		this.bytePerSample = bitRate / 8;
		addNoise(new Noise(NoiseType.Clipping, 200, 10000, 0.5));
		addNoise(new Noise(NoiseType.Hum, 5000, 20000, 0.92));
	}

	public void analyseStart(int frameSize) {
		analyzer = new AudioAnalyzer(frameSize, sampleRate, true);
	}

	public void analyseEnd() {
		if (analyzer != null) {
			File outputfile = new File("spectrograms/spectro.png");
			try {
				ImageIO.write(analyzer.getSpectrogram(), "PNG", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			analyzer = null;
		}
	}

	public void analyzeData(double[] samples) {
		state.firePropertyChange(DATA_ADDED_PROPERTY, null, samples);
		analyzer.anazlyze(samples);
	}
	
	public void clearAnalysisData() {
		if(this.analyzer != null) {
			analyzer = null;
		}
	}

	public void setFoundNoise(List<Noise> foundNoise) {
		this.foundNoise = foundNoise;
		foundNoise.forEach(n -> n.setChannel(index));
	}

	public void addNoise(Noise noise) {
		this.foundNoise.add(noise);
		noise.setChannel(index);
	}

	public List<Noise> getFoundNoise() {
		return foundNoise;
	}

	public void removeNoise(Noise noise) {
		this.foundNoise.remove(noise);
	}

	public void addChangeListener(PropertyChangeListener listener) {
		this.state.addPropertyChangeListener(listener);
	}

	public int getBitDepth() {
		return bitRate;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getIndex() {
		return index;
	}

	public long getLength() {
		return length;
	}
}
