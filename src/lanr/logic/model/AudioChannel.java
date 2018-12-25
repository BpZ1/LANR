package lanr.logic.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import lanr.logic.AudioAnalyzer;
import lanr.model.Settings;

/**
 * @author Nicolas Bruch
 * 
 *         Contains the data for a single audio channel.
 *
 */
public class AudioChannel {

	private static final String OUTPUT_FOLDER = "spectrograms/";
	/**
	 * Percentage of the frame that will be visualized.
	 */
	public static double visualisationReductionFactor = 0.1;
	public final static String DATA_ADDED_PROPERTY = "added";
	private final PropertyChangeSupport state = new PropertyChangeSupport(this);

	private final AudioData parent;
	private AudioAnalyzer analyzer;
	private final long length;
	/**
	 * Id of the audio stream
	 */
	private final int id;
	/**
	 * Index of the channel.
	 */
	private final int index;
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

	public AudioChannel(AudioData parent, int bitRate, int sampleRate, int id, int index, long length) {
		this.parent = parent;
		this.index = index;
		this.bitRate = bitRate;
		this.sampleRate = sampleRate;
		this.id = id;
		this.length = length;
		this.bytePerSample = bitRate / 8;
	}

	public void analyseStart(int frameSize) {
		analyzer = new AudioAnalyzer(frameSize, sampleRate, true);
	}

	public void analyseEnd() throws LANRException {
		if (analyzer != null) {
			if(Settings.getInstance().createSpectrogram()) {
				StringBuilder fileName = new StringBuilder();
				fileName.append(OUTPUT_FOLDER);
				fileName.append(parent.getName());
				fileName.append("_");
				fileName.append(index);
				fileName.append(".png");
				File outputfile = new File(fileName.toString());
				try {
					ImageIO.write(analyzer.getSpectrogram(), "PNG", outputfile);
				} catch (IOException e) {
					throw new LANRException("Could not create spectrogram!", e);
				}
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
	
	public AudioData getParent() {
		return parent;
	}

	public int getBitDepth() {
		return bitRate;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getId() {
		return id;
	}
	
	public int getIndex() {
		return index;
	}

	public long getLength() {
		return length;
	}
}
