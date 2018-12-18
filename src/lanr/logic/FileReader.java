package lanr.logic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;

public class FileReader {

	/**
	 * @param path
	 */
	public static AudioData readFile(String path) {

		List<AudioChannel> audioChannels = new ArrayList<AudioChannel>();
		// Create a Xuggler container object
		IContainer container = IContainer.make();

		if (container.open(path, IContainer.Type.READ, null) < 0) {
			throw new IllegalArgumentException("Could not open file: " + path);
		}

		// Iterate through all audio streams
		int numStreams = container.getNumStreams();
		for (int i = 0; i < numStreams; i++) {
			AudioChannel channel = getAudioChannelData(i, container, path);
			if (channel != null) {
				audioChannels.add(channel);
			}
		}
		container.close();
		AudioData data = new AudioData(path, audioChannels);
		return data;
	}

	private static AudioChannel getAudioChannelData(int index, IContainer container, String path) {
		int audioStreamId = -1;
		int maxBitRate = -1;
		int maxSampleRate = -1;
		IStreamCoder audioCoder = null;
		// Find the stream object
		IStream stream = container.getStream(index);

		// Get the decoder
		IStreamCoder coder = stream.getStreamCoder();

		if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
			audioStreamId = index;
			audioCoder = coder;
		} else {
			coder.close();
			return null;
		}

		double[] sampleData = new double[0];
		if (audioCoder.open(null, null) < 0) {
			throw new RuntimeException("could not open audio decoder for container: " + path);
		}

		IPacket packet = IPacket.make();
		while (container.readNextPacket(packet) >= 0) {
			// Check if the package belongs to the audio stream
			if (packet.getStreamIndex() == audioStreamId) {
				IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());
				int offset = 0;
				while (offset < packet.getSize()) {
					int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
					if (bytesDecoded < 0) {
						throw new RuntimeException("got error decoding audio in: " + path);
					}
					offset += bytesDecoded;
					// Check if the set of samples is complete
					if (samples.isComplete()) {
						long bitRate = samples.getSampleBitDepth();
						maxBitRate = Math.max((int) bitRate, maxBitRate);
						maxSampleRate = Math.max(samples.getSampleRate(), maxSampleRate);
						double[] packageData = byteToDoubleConverter(bitRate,
								samples.getData().getByteArray(0, samples.getSize()));

						sampleData = Utils.concatArrays(sampleData, packageData);
					}
				}
			} else {
				// Package not part of audio stream
			}
		}

		if (audioCoder != null) {
			audioCoder.close();
			audioCoder = null;
		}
		AudioChannel channel = new AudioChannel(sampleData, maxBitRate, maxSampleRate);
		return channel;
	}

	public static double[] byteToDoubleConverter(long bitDepth, byte[] rawData) {
		if (bitDepth % 8 != 0) {
			throw new IllegalArgumentException("Invalid bit depth of: " + bitDepth);
		}
		if (rawData == null) {
			throw new IllegalArgumentException("byte data can't be null.");
		}
		// If we have a bit depth of 16 we need 2 byte per value
		int bytesPerSample = (int) bitDepth / 8;
		// Number of samples in the result
		int sampleCount = rawData.length / bytesPerSample;
		double[] resultData = new double[sampleCount];
		int resultCounter = 0;
		for (int i = 0; i < sampleCount; i += bytesPerSample) {
			byte[] data = Arrays.copyOfRange(rawData, i, i + bytesPerSample);
			resultData[resultCounter] = (double) ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort();
			resultCounter++;
		}
		return resultData;
	}
}
