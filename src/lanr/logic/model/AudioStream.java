package lanr.logic.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import lanr.logic.AudioAnalyzer;
import lanr.logic.frequency.FrequencyConversion;
import lanr.logic.frequency.windowfunctions.WindowFunction;

/**
 * Contains the data for a single audio channel.
 * 
 * @author Nicolas Bruch
 * 
 */
public class AudioStream {

	private static final String OUTPUT_FOLDER = "spectrograms/";
	
	public final static String DATA_ADDED_PROPERTY = "added";	
	public final static String ANALYZING_COMPLETE = "complete";
	
	private final PropertyChangeSupport state = new PropertyChangeSupport(this);

	private final AudioData parent;
	private AudioAnalyzer analyzer;
	private double replayGain;
	private final long length;
	/**
	 * Id of the audio stream
	 */
	private final int id;
	/**
	 * Bit depth per sample.
	 */
	private final int bitRate;
	
	/**
	 * Converter for the conversion from time to frequency domain.
	 */
	private static FrequencyConversion converter = FrequencyConversion.FFT;
	private static WindowFunction windowFunction = WindowFunction.Hanning;
	private static boolean usingWindowFunction = false;
	private static boolean createSpectrogram = true;
	/**
	 * Samples per second.
	 */
	private final int sampleRate;
	/**
	 * List of found {@link Noise} types in the different channel of this audio
	 * file.
	 */
	private List<Noise> foundNoise = new ArrayList<Noise>();

	public AudioStream(AudioData parent, int bitRate, int sampleRate, int id, long length) {
		this.parent = parent;
		this.bitRate = bitRate;
		this.sampleRate = sampleRate;
		this.id = id;
		this.length = length;
	}

	public void analyseStart(int frameSize) {
		analyzer = new AudioAnalyzer(frameSize, sampleRate, replayGain,
				createSpectrogram, windowFunction, converter);
	}

	public void analyseEnd() throws LANRException {		
		if (analyzer != null) {
			analyzer.finish();
			this.foundNoise = analyzer.getNoise();
			this.state.firePropertyChange(ANALYZING_COMPLETE, null, foundNoise);
			if(createSpectrogram) {
				StringBuilder fileName = new StringBuilder();
				fileName.append(OUTPUT_FOLDER);
				fileName.append(parent.getName());
				fileName.append("_");
				fileName.append(id);
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
	
	public void setReplayGain(double value) {
		this.replayGain = value;
	}
	
	public double getReplayGain() {
		return replayGain;
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
		foundNoise.forEach(n -> n.setChannel(id));
	}

	public void addNoise(Noise noise) {
		this.foundNoise.add(noise);
		noise.setChannel(id);
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

	public long getLength() {
		return length;
	}
	
	public static boolean getCreateSpectrogram() {
		return createSpectrogram;
	}
	
	public static void setCreateSpectrogram(boolean value) {
		createSpectrogram = value;
	}
	
	public static boolean getUsingWindowFunction() {
		return usingWindowFunction;
	}
	
	public static void setUsingWindowFunction(boolean value) {
		usingWindowFunction = value;
	}
	
	public static FrequencyConversion getConverter() {
		return converter;
	}

	public static void setConverter(FrequencyConversion converter) {
		AudioStream.converter = converter;
	}
	
	public static WindowFunction getWindowFunction() {
		return windowFunction;
	}
	
	public static void setWindowFunction(WindowFunction windowFunction) {
		AudioStream.windowFunction = windowFunction;
	}
}
