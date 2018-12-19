package lanr.logic.model;

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

	/**
	 * Audio file containing the channel.
	 */
	private AudioData parent;
	private int index;
	/**
	 * Bit depth per sample.
	 */
	private final int bitRate;
	/**
	 * Number of samples in the channel.
	 */
	private final int sampleCount;
	private final int bytesPerSample;
	/**
	 * Samples per second.
	 */
	private final int sampleRate;
	/**
	 * Samples of the audio channel.
	 */
	private final byte[] samples;
	/**
	 * List of found {@link Noise} types in the different channel of this audio
	 * file.
	 */
	private List<Noise> foundNoise = new ArrayList<Noise>();

	public AudioChannel(byte[] samples, int bitRate, int sampleRate) {
		this.samples = samples;
		this.bitRate = bitRate;
		this.sampleRate = sampleRate;
		this.bytesPerSample = bitRate / 8;
		this.sampleCount = samples.length / bytesPerSample;
	}

	public void setParent(AudioData parent) {
		this.parent = parent;
	}

	public void setFoundNoise(List<Noise> foundNoise) {
		this.foundNoise = foundNoise;
		foundNoise.forEach(n -> n.setChannel(index));
		parent.setAnalyzed(true);
		parent.calculateSeverity();
	}

	public void addNoise(Noise noise) {
		this.foundNoise.add(noise);
		noise.setChannel(index);
		parent.setAnalyzed(true);
		parent.calculateSeverity();
	}

	public List<Noise> getFoundNoise() {
		return foundNoise;
	}

	public void removeNoise(Noise noise) {
		this.foundNoise.remove(noise);
		parent.calculateSeverity();
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

	public void setIndex(int index) {
		this.index = index;
	}

	public int getSampleCount() {
		return sampleCount;
	}
}
