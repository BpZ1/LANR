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

	public final static String DATA_ADDED_PROPERTY = "added";
	private final PropertyChangeSupport state = new PropertyChangeSupport(this);
	
	private int index;
	private long length;
	/**
	 * Bit depth per sample.
	 */
	private final int bitRate;
	private final int bytesPerSample;
	/**
	 * Samples per second.
	 */
	private final int sampleRate;
	/**
	 * Samples of the audio channel.
	 */
	private byte[] samples;
	/**
	 * List of found {@link Noise} types in the different channel of this audio
	 * file.
	 */
	private List<Noise> foundNoise = new ArrayList<Noise>();
	
	public AudioChannel(int bitRate, int sampleRate, int index, long length) {
		this.bitRate = bitRate;
		this.sampleRate = sampleRate;
		this.bytesPerSample = bitRate / 8;
		this.index = index;
		this.length = length;
		
		addNoise(new Noise(NoiseType.Clipping, 200, 10000, 0.5));
		addNoise(new Noise(NoiseType.Hum, 5000, 20000, 0.92));
	}
	
	public void addRawData(ByteBuffer buffer) {		
		state.firePropertyChange(DATA_ADDED_PROPERTY,
				null,
				Utils.byteToShortConverter(bitRate, buffer.array()));
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

	public byte[] getSamples() {
		return samples;
	}

	private byte[] getValueRange(int fromIndex, int toIndex) {
		// Double the size because of byte short conversion
		int size = (toIndex - fromIndex) * bytesPerSample;
		byte[] data = new byte[size];
		int counter = fromIndex * bytesPerSample;
		for (int i = 0; i < size; i++) {
			data[i] = samples[counter];
			counter++;
		}
		return data;
	}

	/**
	 * Converts the value in the given index to a 16 bit value.
	 * @param index - Index of the sample.
	 * @return Sample 16 bit value for the given index.
	 */
	public short get16BitSampleValue(int index) {
		byte[] data = { 
				samples[index * bytesPerSample],
				samples[index * bytesPerSample + 1]
					};
		return Utils.byteToShortConverter(bytesPerSample, data)[0];
	}

	/**
	 * Converts a range of samples to 16 bit values.
	 * @param fromIndex - Beginning index of the sample.
	 * @param toIndex - End index of the sample.
	 * @return Sample 16 bit values for the given range.
	 */
	public short[] get16BitSampleValues(int fromIndex, int toIndex) {
		return Utils.byteToShortConverter(bytesPerSample, getValueRange(fromIndex, toIndex));
	}

	/**
	 * Converts the value in the given index to a 64 bit value.
	 * @param index - Index of the sample value.
	 * @return Sample 64 bit value for the given index.
	 */
	public double get64BitSampleValue(int index) {
		byte[] data = { 
				samples[index * bytesPerSample],
				samples[index * bytesPerSample + 1]
					};
		return Utils.byteToDoubleConverter(bytesPerSample, data)[0];
	}

	/**
	 * Converts a range of samples to 64 bit values.<br>
	 * <b>Should not be used for long ranges because of the memory usage!</b>
	 * @param fromIndex - Beginning index of the sample.
	 * @param toIndex - End index of the sample.
	 * @return Sample 64 bit values for the given range.
	 */
	public double[] get64BitSampleValues(int fromIndex, int toIndex) {
		return Utils.byteToDoubleConverter(bytesPerSample, getValueRange(fromIndex, toIndex));
	}
	
	public void addChangeListener(PropertyChangeListener listener) {
		this.state.addPropertyChangeListener(listener);
	}

	/**
	 * Converts all samples into 16 bit values.
	 * This will double the memory used, so use with caution!
	 * @return
	 */
	public short[] get16BitSampleValues() {
		return Utils.byteToShortConverter(bytesPerSample, samples);
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
