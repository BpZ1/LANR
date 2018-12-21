package lanr.logic.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import lanr.logic.Utils;

/**
 * @author Nicolas Bruch
 * 
 *         Contains the data for a single audio channel.
 *
 */
public class AudioChannel {

	public static final int VISUALISATION_REDUCTION_FACTOR = 100;
	public final static String DATA_ADDED_PROPERTY = "added";
	private final PropertyChangeSupport state = new PropertyChangeSupport(this);
	
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
	
	public void addRawData(byte[] data) {	
		short[] samples = Utils.byteToShortConverter(bytePerSample, data);
		short[] visualSamples = new short[samples.length / VISUALISATION_REDUCTION_FACTOR];
		//Take every Xth element
		int counter = 0;
		for(int i = 0; i < visualSamples.length; i++) {
			if(samples[counter] > Short.MAX_VALUE) {
				visualSamples[i] = Short.MAX_VALUE;
			}else {
				visualSamples[i] = (short) samples[counter];
			}	
			counter += VISUALISATION_REDUCTION_FACTOR;
		}
		state.firePropertyChange(DATA_ADDED_PROPERTY, null, visualSamples);
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
