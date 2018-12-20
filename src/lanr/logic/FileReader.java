package lanr.logic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaAudio;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.javaxsound.MediaAudioConverter;
import io.humble.video.javaxsound.MediaAudioConverterFactory;
import lanr.logic.model.AudioChannel;
import lanr.logic.model.AudioData;
import lanr.logic.model.LANRFileException;

public class FileReader {

	public static final String PROGRESS_CHANGED_PROPERTY = "progress";
	public static final String LOADING_STARTED_PROPERTY = "start";
	public static final String LOADING_ENDED_PROPERTY = "end";
	public static final String DECODING_CHANNEL_STARTED_PROPERTY = "channelStart";
	public static final String DECODING_CHANNEL_ENDED_PROPERTY = "channelEnd";

	private final PropertyChangeSupport state = new PropertyChangeSupport(this);

	public void addChangeListener(PropertyChangeListener listener) {
		this.state.addPropertyChangeListener(listener);
	}

	public FileReader() {
	};

	public FileReader(PropertyChangeListener listener) {
		addChangeListener(listener);
	}

	private int currentProgress;

	/**
	 * @param path
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws LANRFileException
	 */
	public AudioData readFile(String path) throws InterruptedException, IOException, LANRFileException {

		currentProgress = 0;
		state.firePropertyChange(LOADING_STARTED_PROPERTY, null, null);
		List<AudioChannel> audioChannels = new ArrayList<AudioChannel>();
		/*
		 * Start by creating a container object, in this case a demuxer since we are
		 * reading, to get audio data from.
		 */
		Demuxer demuxer = Demuxer.make();
		demuxer.open(path, null, false, true, null, null);

		// Iterate through all audio streams
		int numStreams = demuxer.getNumStreams();
		for (int i = 0; i < numStreams; i++) {
			final DemuxerStream stream = demuxer.getStream(i);
			final Decoder decoder = stream.getDecoder();
			// Check if the found stream is an audio stream
			if (decoder != null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_AUDIO) {
				AudioChannel channel = getAudioChannelData(i, demuxer, decoder);
				if (channel != null) {
					audioChannels.add(channel);
				}
			}
		}
		if (audioChannels.isEmpty()) {
			throw new LANRFileException("No audio stream could be found in " + path);
		}
		demuxer.close();
		state.firePropertyChange(LOADING_ENDED_PROPERTY, null, null);
		AudioData data = new AudioData(path, audioChannels);
		return data;
	}

	private AudioChannel getAudioChannelData(int index, Demuxer demuxer, Decoder audioDecoder)
			throws InterruptedException, IOException {

		state.firePropertyChange(DECODING_CHANNEL_STARTED_PROPERTY, null, index);
		
		
		/*
		 * Based on code from
		 * https://github.com/artclarke/humble-video/blob/master/humble-video-demos/src
		 * /main/java/io/humble/video/demos/DecodeAndPlayAudio.java
		 * 
		 */
		audioDecoder.open(null, null);
		final MediaAudio samples = MediaAudio.make(
				audioDecoder.getFrameSize(),
				audioDecoder.getSampleRate(),
				audioDecoder.getChannels(),
				audioDecoder.getChannelLayout(),
				audioDecoder.getSampleFormat());

		//Returns not the correct bit value, therefore *4 instead of 8
		int bitDepth = samples.getBytesPerSample() * 4; 
		int sampleRate = samples.getSampleRate();
		int bytesPerSample = bitDepth / 8;
		final double progressIncrement = 100 / samples.getNumSamples();
		
		final MediaAudioConverter converter = MediaAudioConverterFactory
				.createConverter(MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO, samples);
		
		ByteBuffer rawAudio = null;
		byte[] rawSamples = new byte[0];
		final MediaPacket packet = MediaPacket.make();

		
		// Read the packets
		while (demuxer.read(packet) >= 0) {
			if (packet.getStreamIndex() == index) {
				int offset = 0;
				int bytesRead = 0;
				do {
					bytesRead += audioDecoder.decode(samples, packet, offset);
					if (samples.isComplete()) {
						//Save the packet data
						rawAudio = converter.toJavaAudio(rawAudio, samples);
						rawSamples = Utils.concatArrays(rawSamples, rawAudio.array());
						this.currentProgress = (int) (progressIncrement * bytesRead / bytesPerSample);
						state.firePropertyChange(PROGRESS_CHANGED_PROPERTY, null, currentProgress);
					}
					offset += bytesRead;
				} while (offset < packet.getSize());
			}
		}
		state.firePropertyChange(DECODING_CHANNEL_ENDED_PROPERTY, null, null);
		AudioChannel channel = new AudioChannel(rawSamples, bitDepth, sampleRate);
		return channel;
	}
}
